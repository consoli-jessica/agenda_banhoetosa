package com.consoli.agenda_banhoetosa.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.consoli.agenda_banhoetosa.entity.Cliente;
import com.consoli.agenda_banhoetosa.repository.ClienteRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clientes;

    public ClienteService(ClienteRepository clientes) {
        this.clientes = clientes;
    }

    public List<Cliente> listar() {
        return clientes.findAll();
    }

    public Cliente buscar(Long id) {
        return clientes.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente " + id));
    }

    public Cliente criar(Cliente cliente) {
        cliente.setId(null);
        return clientes.save(cliente);
    }

    public Cliente editar(Long id, Cliente dados) {
        Cliente cliente = buscar(id);
        cliente.setNomeCompleto(dados.getNomeCompleto());
        cliente.setTelefone(dados.getTelefone());
        cliente.setEndereco(dados.getEndereco());
        return clientes.save(cliente);
    }

    public void deletar(Long id) {
        clientes.delete(buscar(id));
    }

}
