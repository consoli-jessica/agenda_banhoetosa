package com.consoli.agenda_banhoetosa.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.consoli.agenda_banhoetosa.entity.Cachorro;
import com.consoli.agenda_banhoetosa.repository.CachorroRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class CachorroService {

    private final CachorroRepository cachorros;

    public CachorroService(CachorroRepository cachorros) {
        this.cachorros = cachorros;
    }

    public List<Cachorro> listar() {
        return cachorros.findAll();
    }

    public Cachorro buscar(Long id) {
        return cachorros.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cachorro " + id));
    }

    public Cachorro criar(Cachorro cachorro) {
        cachorro.setId(null);
        return cachorros.save(cachorro);
    }

    public Cachorro editar(Long id, Cachorro dados) {
        Cachorro cachorro = buscar(id);
        cachorro.setNome(dados.getNome());
        cachorro.setDono(dados.getDono());
        cachorro.setRaca(dados.getRaca());
        return cachorros.save(cachorro);
    }

    public void deletar(Long id) {
        cachorros.delete(buscar(id));
    }

}
