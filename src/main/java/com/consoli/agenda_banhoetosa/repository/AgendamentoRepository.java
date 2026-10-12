package com.consoli.agenda_banhoetosa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.consoli.agenda_banhoetosa.entity.Agendamento;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
}
