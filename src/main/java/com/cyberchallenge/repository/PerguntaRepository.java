package com.cyberchallenge.repository;

import com.cyberchallenge.model.Pergunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerguntaRepository extends JpaRepository<Pergunta, Long> {

    // MELHORIA: sorteia exatamente 10 perguntas ativas do nivel escolhido,
    // sem repetir dentro da mesma partida (uma unica query traz linhas
    // distintas, entao repeticao dentro da mesma partida ja fica
    // naturalmente descartada).
    // RAND() e a funcao de sorteio do MySQL (e tambem funciona no H2, usado
    // anteriormente). Se migrar para PostgreSQL, troque para RANDOM().
    @Query(value = "SELECT * FROM perguntas WHERE ativa = true AND nivel = :nivel ORDER BY RAND() LIMIT 10", nativeQuery = true)
    List<Pergunta> findRandomPerguntasAtivasPorNivel(@Param("nivel") Integer nivel);

    // MELHORIA: mesma selecao aleatoria, mas evitando repetir (quando possivel)
    // perguntas usadas pelo mesmo participante na partida anterior daquele
    // nivel. Usado para dar mais variedade quando o jogador clica em
    // "jogar novamente".
    @Query(value = "SELECT * FROM perguntas WHERE ativa = true AND nivel = :nivel AND id NOT IN (:idsExcluidos) ORDER BY RAND() LIMIT 10", nativeQuery = true)
    List<Pergunta> findRandomPerguntasAtivasPorNivelExcluindo(@Param("nivel") Integer nivel, @Param("idsExcluidos") List<Long> idsExcluidos);

    long countByAtivaTrueAndNivel(Integer nivel);

    List<Pergunta> findByTema(String tema);

    List<Pergunta> findByAtivaTrue();

    List<Pergunta> findByNivel(Integer nivel);
}
