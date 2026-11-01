package com.consoli.agenda_banhoetosa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class LoginRedirectTest {

    @LocalServerPort
    private int port;

    @Test
    void primeiroLoginRedirecionaParaARaizSemJsessionid() throws Exception {
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build();
        HttpRequest login = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("usuario=atendente&senha=banho123"))
                .build();

        HttpResponse<String> resposta = client.send(login, HttpResponse.BodyHandlers.ofString());

        assertEquals(302, resposta.statusCode());
        String location = resposta.headers().firstValue("location").orElseThrow();
        assertFalse(location.contains("jsessionid"), location);

        URI destino = location.startsWith("http") ? URI.create(location)
                : URI.create("http://localhost:" + port + location);
        HttpRequest inicio = HttpRequest.newBuilder()
                .uri(destino)
                .header("Cookie", jsession(resposta.headers().allValues("set-cookie")))
                .GET()
                .build();
        HttpResponse<String> pagina = client.send(inicio, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, pagina.statusCode());
        assertTrue(pagina.body().contains("Agende Aqui"));
    }

    private static String jsession(List<String> cookies) {
        return cookies.stream()
                .filter(valor -> valor.startsWith("JSESSIONID="))
                .map(valor -> valor.split(";", 2)[0])
                .findFirst()
                .orElseThrow();
    }

}
