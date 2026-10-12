package com.consoli.agenda_banhoetosa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.consoli.agenda_banhoetosa.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
