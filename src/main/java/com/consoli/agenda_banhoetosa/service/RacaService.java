package com.consoli.agenda_banhoetosa.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.consoli.agenda_banhoetosa.entity.Raca;
import com.consoli.agenda_banhoetosa.repository.RacaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class RacaService {

    private final RacaRepository racas;

    public RacaService(RacaRepository racas) {
        this.racas = racas;
    }

    public List<Raca> listar() {
        return racas.findAll();
    }

    public Raca buscar(Long id) {
        return racas.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Raça " + id));
    }

    public Raca criar(Raca raca) {
        raca.setId(null);
        return racas.save(raca);
    }

    public Raca editar(Long id, Raca dados) {
        Raca raca = buscar(id);
        raca.setNome(dados.getNome());
        return racas.save(raca);
    }

    public void deletar(Long id) {
        racas.delete(buscar(id));
    }

}
