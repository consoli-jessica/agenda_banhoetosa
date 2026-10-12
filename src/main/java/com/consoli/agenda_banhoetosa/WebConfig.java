package com.consoli.agenda_banhoetosa;

import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;

import com.consoli.agenda_banhoetosa.controller.PainelController;
import com.consoli.agenda_banhoetosa.entity.Raca;
import com.consoli.agenda_banhoetosa.entity.Servico;
import com.consoli.agenda_banhoetosa.service.RacaService;
import com.consoli.agenda_banhoetosa.service.ServicoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/", "/agenda", "/agenda/**", "/app/**");
    }

    // ponytail: catálogo inicial só com a base vazia; a API segue sem login
    @Bean
    @ConditionalOnProperty(name = "agenda.seed", havingValue = "true", matchIfMissing = true)
    ApplicationRunner dadosIniciais(RacaService racas, ServicoService servicos) {
        return args -> {
            if (racas.listar().isEmpty()) {
                for (String nome : List.of("SRD", "Shih Tzu", "Labrador", "Yorkshire", "Golden Retriever")) {
                    Raca raca = new Raca();
                    raca.setNome(nome);
                    racas.criar(raca);
                }
            }
            if (servicos.listar().isEmpty()) {
                gravar(servicos, "Banho", 60);
                gravar(servicos, "Tosa", 90);
                gravar(servicos, "Banho e tosa", 120);
            }
        };
    }

    private static void gravar(ServicoService servicos, String descricao, int minutos) {
        Servico servico = new Servico();
        servico.setDescricao(descricao);
        servico.setTempoDuracao(minutos);
        servicos.criar(servico);
    }

    static final class LoginInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                throws Exception {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute(PainelController.SESSAO) != null) {
                return true;
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
    }

}
