package com.consoli.agenda_banhoetosa.controller;

import java.time.LocalDateTime;

import com.consoli.agenda_banhoetosa.entity.Agendamento;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AgendamentoResposta(
        Long id,
        Long cachorroId,
        Long servicoId,
        Long clienteId,
        LocalDateTime data,
        CachorroResposta cachorro,
        ServicoResposta servico,
        ClienteResposta cliente) {

    public static AgendamentoResposta de(Agendamento agendamento, CachorroResposta cachorro, ServicoResposta servico,
            ClienteResposta cliente) {
        return new AgendamentoResposta(agendamento.getId(), agendamento.getCachorro().getId(),
                agendamento.getServico().getId(), agendamento.getCliente().getId(), agendamento.getData(),
                cachorro, servico, cliente);
    }

}
