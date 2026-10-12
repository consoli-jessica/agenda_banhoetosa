package com.consoli.agenda_banhoetosa.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.consoli.agenda_banhoetosa.entity.Agendamento;
import com.consoli.agenda_banhoetosa.repository.AgendamentoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class AgendamentoService {

    private final AgendamentoRepository agendamentos;

    public AgendamentoService(AgendamentoRepository agendamentos) {
        this.agendamentos = agendamentos;
    }

    public List<Agendamento> listar() {
        return agendamentos.findAll();
    }

    public Agendamento buscar(Long id) {
        return agendamentos.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento " + id));
    }

    public Agendamento criar(Agendamento agendamento) {
        agendamento.setId(null);
        return agendamentos.save(agendamento);
    }

    public Agendamento editar(Long id, Agendamento dados) {
        Agendamento agendamento = buscar(id);
        agendamento.setCachorro(dados.getCachorro());
        agendamento.setServico(dados.getServico());
        agendamento.setCliente(dados.getCliente());
        agendamento.setData(dados.getData());
        return agendamentos.save(agendamento);
    }

    public void deletar(Long id) {
        agendamentos.delete(buscar(id));
    }

}
