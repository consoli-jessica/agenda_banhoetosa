package com.consoli.agenda_banhoetosa.controller;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.consoli.agenda_banhoetosa.entity.Agendamento;
import com.consoli.agenda_banhoetosa.entity.Cachorro;
import com.consoli.agenda_banhoetosa.entity.Cliente;
import com.consoli.agenda_banhoetosa.entity.Raca;
import com.consoli.agenda_banhoetosa.entity.Servico;
import com.consoli.agenda_banhoetosa.service.AgendamentoService;
import com.consoli.agenda_banhoetosa.service.CachorroService;

@Component
public class Respostas {

    public static final Set<String> CLIENTE = Set.of(
            "cachorros", "cachorros.raca",
            "agendamentos", "agendamentos.cachorro", "agendamentos.servico");

    public static final Set<String> RACA = Set.of("cachorros", "cachorros.dono");

    public static final Set<String> CACHORRO = Set.of(
            "dono", "raca",
            "agendamentos", "agendamentos.cliente", "agendamentos.servico");

    public static final Set<String> SERVICO = Set.of(
            "agendamentos", "agendamentos.cliente", "agendamentos.cachorro");

    public static final Set<String> AGENDAMENTO = Set.of(
            "cliente", "cachorro", "servico", "cachorro.dono", "cachorro.raca");

    private final CachorroService cachorros;
    private final AgendamentoService agendamentos;

    public Respostas(CachorroService cachorros, AgendamentoService agendamentos) {
        this.cachorros = cachorros;
        this.agendamentos = agendamentos;
    }

    public List<ClienteResposta> clientes(List<Cliente> lista, Expansao expansao) {
        return lista.stream().map(cliente -> cliente(cliente, expansao)).toList();
    }

    public ClienteResposta cliente(Cliente cliente, Expansao expansao) {
        List<CachorroResposta> dogs = expansao.pede("cachorros")
                ? cachorros.listarPorDono(cliente.getId()).stream()
                        .map(cachorro -> cachorro(cachorro, expansao.aninhada("cachorros")))
                        .toList()
                : null;
        List<AgendamentoResposta> horarios = expansao.pede("agendamentos")
                ? agendamentos.listarPorCliente(cliente.getId()).stream()
                        .map(agendamento -> agendamento(agendamento, expansao.aninhada("agendamentos")))
                        .toList()
                : null;
        return ClienteResposta.de(cliente).com(dogs, horarios);
    }

    public List<RacaResposta> racas(List<Raca> lista, Expansao expansao) {
        return lista.stream().map(raca -> raca(raca, expansao)).toList();
    }

    public RacaResposta raca(Raca raca, Expansao expansao) {
        List<CachorroResposta> dogs = expansao.pede("cachorros")
                ? cachorros.listarPorRaca(raca.getId()).stream()
                        .map(cachorro -> cachorro(cachorro, expansao.aninhada("cachorros")))
                        .toList()
                : null;
        return RacaResposta.de(raca).com(dogs);
    }

    public List<CachorroResposta> cachorros(List<Cachorro> lista, Expansao expansao) {
        return lista.stream().map(cachorro -> cachorro(cachorro, expansao)).toList();
    }

    public CachorroResposta cachorro(Cachorro cachorro, Expansao expansao) {
        ClienteResposta dono = expansao.pede("dono") ? ClienteResposta.de(cachorro.getDono()) : null;
        RacaResposta raca = expansao.pede("raca") ? RacaResposta.de(cachorro.getRaca()) : null;
        List<AgendamentoResposta> horarios = expansao.pede("agendamentos")
                ? agendamentos.listarPorCachorro(cachorro.getId()).stream()
                        .map(agendamento -> agendamento(agendamento, expansao.aninhada("agendamentos")))
                        .toList()
                : null;
        return CachorroResposta.de(cachorro, dono, raca, horarios);
    }

    public List<ServicoResposta> servicos(List<Servico> lista, Expansao expansao) {
        return lista.stream().map(servico -> servico(servico, expansao)).toList();
    }

    public ServicoResposta servico(Servico servico, Expansao expansao) {
        List<AgendamentoResposta> horarios = expansao.pede("agendamentos")
                ? agendamentos.listarPorServico(servico.getId()).stream()
                        .map(agendamento -> agendamento(agendamento, expansao.aninhada("agendamentos")))
                        .toList()
                : null;
        return ServicoResposta.de(servico).com(horarios);
    }

    public List<AgendamentoResposta> agendamentos(List<Agendamento> lista, Expansao expansao) {
        return lista.stream().map(agendamento -> agendamento(agendamento, expansao)).toList();
    }

    public AgendamentoResposta agendamento(Agendamento agendamento, Expansao expansao) {
        ClienteResposta cliente = expansao.pede("cliente") ? ClienteResposta.de(agendamento.getCliente()) : null;
        ServicoResposta servico = expansao.pede("servico") ? ServicoResposta.de(agendamento.getServico()) : null;
        CachorroResposta cachorro = expansao.pede("cachorro")
                ? cachorro(agendamento.getCachorro(), expansao.aninhada("cachorro"))
                : null;
        return AgendamentoResposta.de(agendamento, cachorro, servico, cliente);
    }

}
