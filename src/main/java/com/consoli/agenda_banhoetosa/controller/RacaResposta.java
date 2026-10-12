package com.consoli.agenda_banhoetosa.controller;

import java.util.List;

import com.consoli.agenda_banhoetosa.entity.Raca;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RacaResposta(Long id, String nome, List<CachorroResposta> cachorros) {

    public static RacaResposta de(Raca raca) {
        return new RacaResposta(raca.getId(), raca.getNome(), null);
    }

    public RacaResposta com(List<CachorroResposta> cachorros) {
        return new RacaResposta(id, nome, cachorros);
    }

}
