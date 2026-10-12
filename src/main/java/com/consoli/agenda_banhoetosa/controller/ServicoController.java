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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.consoli.agenda_banhoetosa.entity.Servico;
import com.consoli.agenda_banhoetosa.service.ServicoService;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService servicos;
    private final Respostas respostas;

    public ServicoController(ServicoService servicos, Respostas respostas) {
        this.servicos = servicos;
        this.respostas = respostas;
    }

    @GetMapping
    public List<ServicoResposta> listar(@RequestParam(required = false) String expand) {
        return respostas.servicos(servicos.listar(), Expansao.de(expand, Respostas.SERVICO));
    }

    @GetMapping("/{id}")
    public ServicoResposta buscar(@PathVariable Long id, @RequestParam(required = false) String expand) {
        return respostas.servico(servicos.buscar(id), Expansao.de(expand, Respostas.SERVICO));
    }

    @PostMapping
    public ResponseEntity<Servico> criar(@RequestBody Servico servico) {
        Servico salvo = servicos.criar(servico);
        return ResponseEntity.created(ClienteController.localizacao(salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    public Servico editar(@PathVariable Long id, @RequestBody Servico servico) {
        return servicos.editar(id, servico);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        servicos.deletar(id);
    }

}
