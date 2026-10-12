package com.consoli.agenda_banhoetosa.controller;

import java.net.URI;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.consoli.agenda_banhoetosa.entity.Cliente;
import com.consoli.agenda_banhoetosa.service.ClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clientes;
    private final Respostas respostas;

    public ClienteController(ClienteService clientes, Respostas respostas) {
        this.clientes = clientes;
        this.respostas = respostas;
    }

    @GetMapping
    public List<ClienteResposta> listar(@RequestParam(required = false) String expand) {
        return respostas.clientes(clientes.listar(), Expansao.de(expand, Respostas.CLIENTE));
    }

    @GetMapping("/{id}")
    public ClienteResposta buscar(@PathVariable Long id, @RequestParam(required = false) String expand) {
        return respostas.cliente(clientes.buscar(id), Expansao.de(expand, Respostas.CLIENTE));
    }

    @PostMapping
    public ResponseEntity<Cliente> criar(@RequestBody Cliente cliente) {
        Cliente salvo = clientes.criar(cliente);
        return ResponseEntity.created(localizacao(salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    public Cliente editar(@PathVariable Long id, @RequestBody Cliente cliente) {
        return clientes.editar(id, cliente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        clientes.deletar(id);
    }

    static URI localizacao(Long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }

}
