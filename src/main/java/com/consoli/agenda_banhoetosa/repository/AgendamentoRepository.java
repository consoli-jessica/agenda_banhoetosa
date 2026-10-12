package com.consoli.agenda_banhoetosa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.consoli.agenda_banhoetosa.entity.Agendamento;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByClienteId(Long clienteId);

    List<Agendamento> findByCachorroId(Long cachorroId);

    List<Agendamento> findByServicoId(Long servicoId);

}
