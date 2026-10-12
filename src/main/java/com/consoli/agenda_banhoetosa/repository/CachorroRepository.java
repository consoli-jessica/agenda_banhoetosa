package com.consoli.agenda_banhoetosa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.consoli.agenda_banhoetosa.entity.Cachorro;

public interface CachorroRepository extends JpaRepository<Cachorro, Long> {

    List<Cachorro> findByDonoId(Long donoId);

    List<Cachorro> findByRacaId(Long racaId);

}
