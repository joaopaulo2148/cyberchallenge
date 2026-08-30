package com.cyberchallenge.repository;

import com.cyberchallenge.model.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipanteRepository extends JpaRepository<Participante, Long> {

    // Regra 11: Ranking considerando Maior Pontuacao -> Menor Tempo Total -> Menor Tempo Medio
    List<Participante> findAllByOrderByPontuacaoDescTempoTotalAscTempoMedioAsc();
}
