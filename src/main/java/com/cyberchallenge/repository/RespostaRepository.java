package com.cyberchallenge.repository;

import com.cyberchallenge.model.Resposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RespostaRepository extends JpaRepository<Resposta, Long> {

    List<Resposta> findByPerguntaId(Long perguntaId);

    List<Resposta> findByParticipanteId(Long participanteId);

    // Usado pelo Dashboard: traz todas as respostas ja com a Pergunta associada
    // carregada (evita o problema de N+1 select ao calcular as metricas por
    // pergunta/tema).
    @Query("SELECT r FROM Resposta r JOIN FETCH r.pergunta")
    List<Resposta> findAllComPergunta();
}
