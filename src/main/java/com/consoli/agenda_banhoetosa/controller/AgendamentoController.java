package com.consoli.agenda_banhoetosa.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.consoli.agenda_banhoetosa.entity.Agendamento;
import com.consoli.agenda_banhoetosa.service.AgendamentoService;
import com.consoli.agenda_banhoetosa.service.CachorroService;
import com.consoli.agenda_banhoetosa.service.ClienteService;
import com.consoli.agenda_banhoetosa.service.ServicoService;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentos;
    private final CachorroService cachorros;
    private final ServicoService servicos;
    private final ClienteService clientes;

    public AgendamentoController(AgendamentoService agendamentos, CachorroService cachorros,
            ServicoService servicos, ClienteService clientes) {
        this.agendamentos = agendamentos;
        this.cachorros = cachorros;
        this.servicos = servicos;
        this.clientes = clientes;
    }

    @GetMapping
    public List<Agendamento> listar() {
        return agendamentos.listar();
    }

    @GetMapping("/{id}")
    public Agendamento buscar(@PathVariable Long id) {
        return agendamentos.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Agendamento> criar(@RequestBody Agendamento agendamento) {
        vincular(agendamento);
        Agendamento salvo = agendamentos.criar(agendamento);
        return ResponseEntity.created(ClienteController.localizacao(salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    public Agendamento editar(@PathVariable Long id, @RequestBody Agendamento agendamento) {
        vincular(agendamento);
        return agendamentos.editar(id, agendamento);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        agendamentos.deletar(id);
    }

    private void vincular(Agendamento agendamento) {
        if (agendamento.getCachorro() == null || agendamento.getCachorro().getId() == null) {
            throw new IllegalArgumentException("id do cachorro é obrigatório");
        }
        if (agendamento.getServico() == null || agendamento.getServico().getId() == null) {
            throw new IllegalArgumentException("id do serviço é obrigatório");
        }
        if (agendamento.getCliente() == null || agendamento.getCliente().getId() == null) {
            throw new IllegalArgumentException("id do cliente é obrigatório");
        }
        agendamento.setCachorro(cachorros.buscar(agendamento.getCachorro().getId()));
        agendamento.setServico(servicos.buscar(agendamento.getServico().getId()));
        agendamento.setCliente(clientes.buscar(agendamento.getCliente().getId()));
    }

}
