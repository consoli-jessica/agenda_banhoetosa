package com.consoli.agenda_banhoetosa;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PainelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginEAgendaDoAtendente() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(post("/login").param("usuario", "atendente").param("senha", "errada"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("inválidos")));

        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/login").session(session)
                        .param("usuario", "atendente").param("senha", "banho123"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Agende Aqui")))
                .andExpect(content().string(containsString("cachorro.png")));

        mockMvc.perform(post("/app/clientes").session(session)
                        .param("nomeCompleto", "Lia Pires")
                        .param("telefone", "11988887777")
                        .param("endereco", "Rua das Flores, 10"))
                .andExpect(redirectedUrl("/app/clientes"));

        mockMvc.perform(get("/app/clientes").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Lia Pires")));

        long clienteId = id(mockMvc.perform(get("/clientes")).andReturn());

        mockMvc.perform(post("/app/cachorros").session(session)
                        .param("nome", "Thor")
                        .param("donoId", String.valueOf(clienteId))
                        .param("novaRaca", "Beagle"))
                .andExpect(redirectedUrl("/app/cachorros"));

        mockMvc.perform(get("/app/cachorros").session(session))
                .andExpect(content().string(containsString("Thor")))
                .andExpect(content().string(containsString("Beagle")));

        long cachorroId = id(mockMvc.perform(get("/cachorros")).andReturn());
        long servicoId = id(mockMvc.perform(post("/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descricao\":\"Tosa teste\",\"tempoDuracao\":90}"))
                .andReturn());

        mockMvc.perform(post("/app/agendamentos").session(session)
                        .param("cachorroId", String.valueOf(cachorroId))
                        .param("servicoId", String.valueOf(servicoId))
                        .param("data", "2026-10-12T14:00"))
                .andExpect(redirectedUrl("/agenda?mes=2026-10&dia=2026-10-12"));

        long agendamentoId = id(mockMvc.perform(get("/agendamentos")).andReturn());

        mockMvc.perform(get("/agenda").session(session).param("mes", "2026-10").param("dia", "2026-10-12"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Thor")))
                .andExpect(content().string(containsString("Tosa teste")));

        mockMvc.perform(post("/app/agendamentos/" + agendamentoId).session(session)
                        .param("cachorroId", String.valueOf(cachorroId))
                        .param("servicoId", String.valueOf(servicoId))
                        .param("data", "2026-10-13T09:00"))
                .andExpect(redirectedUrl("/agenda?mes=2026-10&dia=2026-10-13"));

        mockMvc.perform(post("/app/agendamentos/" + agendamentoId + "/excluir").session(session))
                .andExpect(redirectedUrl("/agenda?mes=2026-10&dia=2026-10-13"));

        mockMvc.perform(get("/agenda").session(session).param("mes", "2026-10"))
                .andExpect(content().string(not(containsString("Thor"))));
    }

    private long id(MvcResult result) throws Exception {
        String json = result.getResponse().getContentAsString();
        if (json.startsWith("[")) {
            return JsonPath.parse(json).read("$[0].id", Long.class);
        }
        return JsonPath.parse(json).read("$.id", Long.class);
    }

}
