package com.cyberchallenge.repository;

import com.cyberchallenge.model.Pergunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerguntaRepository extends JpaRepository<Pergunta, Long> {

    // Regra 5: Sorteia 5 perguntas ativas aleatoriamente, sem repetir dentro da partida
    // (uma unica query traz linhas distintas, entao repeticao dentro da mesma partida
    // ja fica naturalmente descartada).
    // RAND() funciona tanto em H2 quanto em MySQL. Se migrar para PostgreSQL, troque para RANDOM().
    @Query(value = "SELECT * FROM perguntas WHERE ativa = true ORDER BY RAND() LIMIT 5", nativeQuery = true)
    List<Pergunta> findRandomPerguntasAtivas();

    // Area administrativa e dashboard
    List<Pergunta> findByTema(String tema);

    List<Pergunta> findByAtivaTrue();

    // BUG CORRIGIDO: havia um "} { }" sobrando depois do fechamento da interface,
    // o que quebrava a compilacao de todo o modulo.
}
