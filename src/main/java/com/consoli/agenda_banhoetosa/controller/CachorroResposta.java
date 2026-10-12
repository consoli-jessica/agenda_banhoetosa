package com.consoli.agenda_banhoetosa.controller;

import java.util.List;

import com.consoli.agenda_banhoetosa.entity.Cachorro;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CachorroResposta(
        Long id,
        String nome,
        Long donoId,
        Long racaId,
        ClienteResposta dono,
        RacaResposta raca,
        List<AgendamentoResposta> agendamentos) {

    public static CachorroResposta de(Cachorro cachorro, ClienteResposta dono, RacaResposta raca,
            List<AgendamentoResposta> agendamentos) {
        return new CachorroResposta(cachorro.getId(), cachorro.getNome(), cachorro.getDono().getId(),
                cachorro.getRaca().getId(), dono, raca, agendamentos);
    }

}
