package com.consoli.agenda_banhoetosa.controller;

import java.util.List;

import com.consoli.agenda_banhoetosa.entity.Servico;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ServicoResposta(Long id, String descricao, Integer tempoDuracao, List<AgendamentoResposta> agendamentos) {

    public static ServicoResposta de(Servico servico) {
        return new ServicoResposta(servico.getId(), servico.getDescricao(), servico.getTempoDuracao(), null);
    }

    public ServicoResposta com(List<AgendamentoResposta> agendamentos) {
        return new ServicoResposta(id, descricao, tempoDuracao, agendamentos);
    }

}
