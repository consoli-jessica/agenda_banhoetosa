package com.consoli.agenda_banhoetosa.controller;

import java.util.List;

import com.consoli.agenda_banhoetosa.entity.Cliente;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClienteResposta(
        Long id,
        String nomeCompleto,
        String telefone,
        String endereco,
        List<CachorroResposta> cachorros,
        List<AgendamentoResposta> agendamentos) {

    public static ClienteResposta de(Cliente cliente) {
        return new ClienteResposta(cliente.getId(), cliente.getNomeCompleto(), cliente.getTelefone(),
                cliente.getEndereco(), null, null);
    }

    public ClienteResposta com(List<CachorroResposta> cachorros, List<AgendamentoResposta> agendamentos) {
        return new ClienteResposta(id, nomeCompleto, telefone, endereco, cachorros, agendamentos);
    }

}
