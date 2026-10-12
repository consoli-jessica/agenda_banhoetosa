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

import com.consoli.agenda_banhoetosa.entity.Raca;
import com.consoli.agenda_banhoetosa.service.RacaService;

@RestController
@RequestMapping("/racas")
public class RacaController {

    private final RacaService racas;
    private final Respostas respostas;

    public RacaController(RacaService racas, Respostas respostas) {
        this.racas = racas;
        this.respostas = respostas;
    }

    @GetMapping
    public List<RacaResposta> listar(@RequestParam(required = false) String expand) {
        return respostas.racas(racas.listar(), Expansao.de(expand, Respostas.RACA));
    }

    @GetMapping("/{id}")
    public RacaResposta buscar(@PathVariable Long id, @RequestParam(required = false) String expand) {
        return respostas.raca(racas.buscar(id), Expansao.de(expand, Respostas.RACA));
    }

    @PostMapping
    public ResponseEntity<Raca> criar(@RequestBody Raca raca) {
        Raca salva = racas.criar(raca);
        return ResponseEntity.created(ClienteController.localizacao(salva.getId())).body(salva);
    }

    @PutMapping("/{id}")
    public Raca editar(@PathVariable Long id, @RequestBody Raca raca) {
        return racas.editar(id, raca);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        racas.deletar(id);
    }

}
