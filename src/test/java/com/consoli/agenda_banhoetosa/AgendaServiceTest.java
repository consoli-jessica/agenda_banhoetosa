package com.consoli.agenda_banhoetosa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.consoli.agenda_banhoetosa.entity.Agendamento;
import com.consoli.agenda_banhoetosa.entity.Cachorro;
import com.consoli.agenda_banhoetosa.entity.Cliente;
import com.consoli.agenda_banhoetosa.entity.Raca;
import com.consoli.agenda_banhoetosa.entity.Servico;
import com.consoli.agenda_banhoetosa.service.AgendamentoService;
import com.consoli.agenda_banhoetosa.service.CachorroService;
import com.consoli.agenda_banhoetosa.service.ClienteService;
import com.consoli.agenda_banhoetosa.service.RacaService;
import com.consoli.agenda_banhoetosa.service.ServicoService;

import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@Transactional
class AgendaServiceTest {

    @Autowired
    private ClienteService clientes;

    @Autowired
    private RacaService racas;

    @Autowired
    private CachorroService cachorros;

    @Autowired
    private ServicoService servicos;

    @Autowired
    private AgendamentoService agendamentos;

    @Test
    void executaCrud() {
        Cliente cliente = new Cliente();
        cliente.setNomeCompleto("Ana Silva");
        cliente.setTelefone("11999999999");
        cliente.setEndereco("Rua A");
        cliente = clientes.criar(cliente);

        Cliente editado = new Cliente();
        editado.setNomeCompleto("Ana Souza");
        editado.setTelefone("11888888888");
        editado.setEndereco("Rua B");
        cliente = clientes.editar(cliente.getId(), editado);

        assertEquals("Ana Souza", clientes.buscar(cliente.getId()).getNomeCompleto());
        assertEquals(1, clientes.listar().size());

        Raca raca = new Raca();
        raca.setNome("Poodle");
        raca = racas.criar(raca);

        Cachorro cachorro = new Cachorro();
        cachorro.setNome("Bob");
        cachorro.setDono(cliente);
        cachorro.setRaca(raca);
        cachorro = cachorros.criar(cachorro);

        Servico servico = new Servico();
        servico.setDescricao("Banho");
        servico.setTempoDuracao(60);
        servico = servicos.criar(servico);

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setCachorro(cachorro);
        agendamento.setServico(servico);
        agendamento.setData(LocalDateTime.of(2026, 10, 12, 14, 0));
        agendamento = agendamentos.criar(agendamento);

        Agendamento novaData = new Agendamento();
        novaData.setCliente(cliente);
        novaData.setCachorro(cachorro);
        novaData.setServico(servico);
        novaData.setData(LocalDateTime.of(2026, 10, 13, 9, 0));
        agendamento = agendamentos.editar(agendamento.getId(), novaData);

        assertEquals(LocalDateTime.of(2026, 10, 13, 9, 0), agendamentos.buscar(agendamento.getId()).getData());

        agendamentos.deletar(agendamento.getId());
        cachorros.deletar(cachorro.getId());
        servicos.deletar(servico.getId());
        racas.deletar(raca.getId());
        clientes.deletar(cliente.getId());

        assertTrue(clientes.listar().isEmpty());
        Long idRemovido = cliente.getId();
        assertThrows(EntityNotFoundException.class, () -> clientes.buscar(idRemovido));
    }

}
