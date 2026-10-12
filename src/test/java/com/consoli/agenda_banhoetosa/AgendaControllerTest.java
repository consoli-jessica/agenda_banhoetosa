package com.consoli.agenda_banhoetosa;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AgendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void crudDosRecursos() throws Exception {
        MvcResult clienteCriado = mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nomeCompleto":"Ana Silva","telefone":"11999999999","endereco":"Rua A"}
                        """))
                .andExpect(status().isCreated())
                .andReturn();
        long clienteId = id(clienteCriado);
        assertTrue(clienteCriado.getResponse().getHeader("Location").endsWith("/clientes/" + clienteId));

        mockMvc.perform(get("/clientes/" + clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeCompleto").value("Ana Silva"));

        mockMvc.perform(put("/clientes/" + clienteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nomeCompleto":"Ana Souza","telefone":"11888888888","endereco":"Rua B"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeCompleto").value("Ana Souza"));

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(clienteId));

        long racaId = id(mockMvc.perform(post("/racas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Poodle\"}"))
                .andExpect(status().isCreated())
                .andReturn());

        long cachorroId = id(mockMvc.perform(post("/cachorros")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nome":"Bob","dono":{"id":%d},"raca":{"id":%d}}
                        """.formatted(clienteId, racaId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dono.nomeCompleto").value("Ana Souza"))
                .andReturn());

        long servicoId = id(mockMvc.perform(post("/servicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"descricao\":\"Banho\",\"tempoDuracao\":60}"))
                .andExpect(status().isCreated())
                .andReturn());

        long agendamentoId = id(mockMvc.perform(post("/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"cachorro":{"id":%d},"servico":{"id":%d},"cliente":{"id":%d},"data":"2026-10-12T14:00:00"}
                        """.formatted(cachorroId, servicoId, clienteId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cachorro.nome").value("Bob"))
                .andReturn());

        mockMvc.perform(put("/agendamentos/" + agendamentoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"cachorro":{"id":%d},"servico":{"id":%d},"cliente":{"id":%d},"data":"2026-10-13T09:00:00"}
                        """.formatted(cachorroId, servicoId, clienteId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("2026-10-13T09:00:00"));

        mockMvc.perform(get("/agendamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(agendamentoId))
                .andExpect(jsonPath("$[0].cachorroId").value(cachorroId))
                .andExpect(jsonPath("$[0].cachorro").doesNotExist());

        mockMvc.perform(get("/cachorros/" + cachorroId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.donoId").value(clienteId))
                .andExpect(jsonPath("$.dono").doesNotExist());

        mockMvc.perform(get("/cachorros?expand=dono,raca"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dono.nomeCompleto").value("Ana Souza"))
                .andExpect(jsonPath("$[0].raca.nome").value("Poodle"));

        mockMvc.perform(get("/agendamentos/" + agendamentoId + "?expand=cachorro.dono,servico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cachorro.dono.nomeCompleto").value("Ana Souza"))
                .andExpect(jsonPath("$.cachorro.raca").doesNotExist())
                .andExpect(jsonPath("$.servico.descricao").value("Banho"))
                .andExpect(jsonPath("$.cliente").doesNotExist());

        mockMvc.perform(get("/clientes/" + clienteId + "?expand=cachorros.raca"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cachorros[0].nome").value("Bob"))
                .andExpect(jsonPath("$.cachorros[0].raca.nome").value("Poodle"))
                .andExpect(jsonPath("$.cachorros[0].dono").doesNotExist());

        mockMvc.perform(get("/racas/" + racaId + "?expand=cachorros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cachorros[0].id").value(cachorroId));

        mockMvc.perform(get("/servicos/" + servicoId + "?expand=agendamentos.cliente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agendamentos[0].cliente.nomeCompleto").value("Ana Souza"))
                .andExpect(jsonPath("$.agendamentos[0].servico").doesNotExist());

        mockMvc.perform(get("/cachorros/" + cachorroId + "?expand=agendamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agendamentos[0].id").value(agendamentoId))
                .andExpect(jsonPath("$.agendamentos[0].cachorro").doesNotExist());

        mockMvc.perform(get("/agendamentos/" + agendamentoId + "?expand=*"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cliente.id").value(clienteId))
                .andExpect(jsonPath("$.cachorro.raca.nome").value("Poodle"));

        mockMvc.perform(get("/cachorros/" + cachorroId + "?expand=foo"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(delete("/agendamentos/" + agendamentoId))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/cachorros/" + cachorroId))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/servicos/" + servicoId))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/racas/" + racaId))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/clientes/" + clienteId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/clientes/" + clienteId))
                .andExpect(status().isNotFound());
    }

    @Test
    void cachorroSemDonoRetorna400() throws Exception {
        mockMvc.perform(post("/cachorros")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Bob\"}"))
                .andExpect(status().isBadRequest());
    }

    private long id(MvcResult result) throws Exception {
        return JsonPath.parse(result.getResponse().getContentAsString()).read("$.id", Long.class);
    }

}
