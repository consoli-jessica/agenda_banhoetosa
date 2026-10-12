package com.consoli.agenda_banhoetosa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.consoli.agenda_banhoetosa.entity.Cachorro;

public interface CachorroRepository extends JpaRepository<Cachorro, Long> {
}
