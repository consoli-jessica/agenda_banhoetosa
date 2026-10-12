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

import com.consoli.agenda_banhoetosa.entity.Cachorro;
import com.consoli.agenda_banhoetosa.service.CachorroService;
import com.consoli.agenda_banhoetosa.service.ClienteService;
import com.consoli.agenda_banhoetosa.service.RacaService;

@RestController
@RequestMapping("/cachorros")
public class CachorroController {

    private final CachorroService cachorros;
    private final ClienteService clientes;
    private final RacaService racas;

    public CachorroController(CachorroService cachorros, ClienteService clientes, RacaService racas) {
        this.cachorros = cachorros;
        this.clientes = clientes;
        this.racas = racas;
    }

    @GetMapping
    public List<Cachorro> listar() {
        return cachorros.listar();
    }

    @GetMapping("/{id}")
    public Cachorro buscar(@PathVariable Long id) {
        return cachorros.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Cachorro> criar(@RequestBody Cachorro cachorro) {
        vincular(cachorro);
        Cachorro salvo = cachorros.criar(cachorro);
        return ResponseEntity.created(ClienteController.localizacao(salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    public Cachorro editar(@PathVariable Long id, @RequestBody Cachorro cachorro) {
        vincular(cachorro);
        return cachorros.editar(id, cachorro);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        cachorros.deletar(id);
    }

    private void vincular(Cachorro cachorro) {
        if (cachorro.getDono() == null || cachorro.getDono().getId() == null) {
            throw new IllegalArgumentException("id do dono é obrigatório");
        }
        if (cachorro.getRaca() == null || cachorro.getRaca().getId() == null) {
            throw new IllegalArgumentException("id da raça é obrigatório");
        }
        cachorro.setDono(clientes.buscar(cachorro.getDono().getId()));
        cachorro.setRaca(racas.buscar(cachorro.getRaca().getId()));
    }

}
