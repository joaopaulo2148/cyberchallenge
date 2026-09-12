package com.cyberchallenge.repository;

import com.cyberchallenge.model.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {

    // MELHORIA (ranking separado por nivel): o ranking nunca mistura
    // participantes de niveis diferentes -- o filtro por nivel e feito
    // diretamente na consulta.
    //
    // Regra de ordenacao (documentada tambem no README):
    //   1) maior pontuacao;
    //   2) menor tempo medio de resposta, em caso de empate.
    // O criterio de "maior percentual de acerto" pedido como segundo
    // desempate e matematicamente equivalente ao primeiro (pontuacao e
    // percentual de acerto sao diretamente proporcionais numa partida de
    // sempre 10 perguntas: percentual = pontuacao * 10), entao na pratica
    // o desempate relevante e sempre o tempo medio.
    List<Partida> findByNivelOrderByPontuacaoDescTempoMedioAsc(Integer nivel);
}
