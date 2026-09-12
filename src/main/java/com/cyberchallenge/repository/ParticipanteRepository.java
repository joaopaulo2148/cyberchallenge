package com.cyberchallenge.repository;

import com.cyberchallenge.model.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipanteRepository extends JpaRepository<Participante, Long> {
    Optional<Participante> findByNickname(String nickname);
    boolean existsByNickname(String nickname);
}
