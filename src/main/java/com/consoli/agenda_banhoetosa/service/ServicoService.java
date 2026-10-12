package com.consoli.agenda_banhoetosa.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.consoli.agenda_banhoetosa.entity.Servico;
import com.consoli.agenda_banhoetosa.repository.ServicoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class ServicoService {

    private final ServicoRepository servicos;

    public ServicoService(ServicoRepository servicos) {
        this.servicos = servicos;
    }

    public List<Servico> listar() {
        return servicos.findAll();
    }

    public Servico buscar(Long id) {
        return servicos.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Serviço " + id));
    }

    public Servico criar(Servico servico) {
        servico.setId(null);
        return servicos.save(servico);
    }

    public Servico editar(Long id, Servico dados) {
        Servico servico = buscar(id);
        servico.setDescricao(dados.getDescricao());
        servico.setTempoDuracao(dados.getTempoDuracao());
        return servicos.save(servico);
    }

    public void deletar(Long id) {
        servicos.delete(buscar(id));
    }

}
