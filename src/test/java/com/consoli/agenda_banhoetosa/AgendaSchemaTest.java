package com.consoli.agenda_banhoetosa;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import com.consoli.agenda_banhoetosa.repository.AgendamentoRepository;
import com.consoli.agenda_banhoetosa.repository.CachorroRepository;
import com.consoli.agenda_banhoetosa.repository.ClienteRepository;
import com.consoli.agenda_banhoetosa.repository.RacaRepository;
import com.consoli.agenda_banhoetosa.repository.ServicoRepository;

@SpringBootTest
@Transactional
class AgendaSchemaTest {

    @Autowired
    private ClienteRepository clientes;

    @Autowired
    private RacaRepository racas;

    @Autowired
    private CachorroRepository cachorros;

    @Autowired
    private ServicoRepository servicos;

    @Autowired
    private AgendamentoRepository agendamentos;

    @Test
    void persisteRelacionamentos() {
        Cliente cliente = new Cliente();
        cliente.setNomeCompleto("Ana Silva");
        cliente.setTelefone("11999999999");
        cliente.setEndereco("Rua A");
        cliente = clientes.save(cliente);

        Raca raca = new Raca();
        raca.setNome("Poodle");
        raca = racas.save(raca);

        Cachorro cachorro = new Cachorro();
        cachorro.setNome("Bob");
        cachorro.setDono(cliente);
        cachorro.setRaca(raca);
        cachorro = cachorros.save(cachorro);

        Servico servico = new Servico();
        servico.setDescricao("Banho");
        servico.setTempoDuracao(60);
        servico = servicos.save(servico);

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setCachorro(cachorro);
        agendamento.setServico(servico);
        agendamento.setData(LocalDateTime.of(2026, 10, 12, 14, 0));
        agendamentos.save(agendamento);

        assertEquals(1, agendamentos.findAll().size());
        assertEquals(cliente.getId(), cachorros.findAll().get(0).getDono().getId());
    }

}
