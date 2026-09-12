package com.cyberchallenge.service;

import com.cyberchallenge.dto.PartidaSubmitDTO;
import com.cyberchallenge.dto.PerguntaDTO;
import com.cyberchallenge.dto.RankingDTO;
import com.cyberchallenge.dto.RespostaSubmitDTO;
import com.cyberchallenge.exception.RecursoNaoEncontradoException;
import com.cyberchallenge.exception.RegraDeNegocioException;
import com.cyberchallenge.model.Alternativa;
import com.cyberchallenge.model.Participante;
import com.cyberchallenge.model.Partida;
import com.cyberchallenge.model.Pergunta;
import com.cyberchallenge.model.Resposta;
import com.cyberchallenge.model.TipoPergunta;
import com.cyberchallenge.repository.ParticipanteRepository;
import com.cyberchallenge.repository.PartidaRepository;
import com.cyberchallenge.repository.PerguntaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PartidaService {

    // Cada partida tem exatamente 10 perguntas, sorteadas aleatoriamente
    // entre o estoque do nivel escolhido pelo jogador.
    private static final int TOTAL_PERGUNTAS_POR_PARTIDA = 10;

    // MELHORIA (simplificacao do sistema de pontuacao): 1 ponto por
    // acerto, sem pesos diferentes entre perguntas. Pontuacao maxima = 10.
    private static final int PONTOS_POR_ACERTO = 1;

    private static final int NIVEL_MINIMO = 1;
    private static final int NIVEL_MAXIMO = 4;

    private final PerguntaRepository perguntaRepository;
    private final PartidaRepository partidaRepository;
    private final ParticipanteRepository participanteRepository;

    public PartidaService(PerguntaRepository perguntaRepository, PartidaRepository partidaRepository,
                           ParticipanteRepository participanteRepository) {
        this.perguntaRepository = perguntaRepository;
        this.partidaRepository = partidaRepository;
        this.participanteRepository = participanteRepository;
    }

    /**
     * Retorna 10 perguntas aleatorias e ativas do nivel escolhido.
     *
     * Quando o front-end informa uma lista de IDs usados na partida
     * anterior daquele mesmo nivel (parametro idsRecentes), o sistema
     * tenta evitar repeti-los imediatamente, para aumentar a variedade
     * entre partidas seguidas. Se, apos excluir esses IDs, nao houver mais
     * perguntas suficientes no estoque, o sistema cai de volta para o
     * sorteio sem exclusao — o jogo nunca fica bloqueado por falta de
     * perguntas.
     */
    public List<PerguntaDTO> iniciarPartida(Integer nivel, List<Long> idsRecentes) {
        validarNivel(nivel);

        long totalAtivasNoNivel = perguntaRepository.countByAtivaTrueAndNivel(nivel);
        if (totalAtivasNoNivel < TOTAL_PERGUNTAS_POR_PARTIDA) {
            throw new RegraDeNegocioException(
                "Nao ha perguntas ativas suficientes cadastradas para o nivel " + nivel +
                " (sao necessarias pelo menos " + TOTAL_PERGUNTAS_POR_PARTIDA + ")."
            );
        }

        List<Pergunta> perguntas = List.of();
        List<Long> exclusao = idsRecentes == null ? Collections.emptyList() : idsRecentes;

        if (!exclusao.isEmpty()) {
            perguntas = perguntaRepository.findRandomPerguntasAtivasPorNivelExcluindo(nivel, exclusao);
        }

        if (perguntas.size() < TOTAL_PERGUNTAS_POR_PARTIDA) {
            perguntas = perguntaRepository.findRandomPerguntasAtivasPorNivel(nivel);
        }

        if (perguntas.size() < TOTAL_PERGUNTAS_POR_PARTIDA) {
            throw new RegraDeNegocioException(
                "Nao ha perguntas ativas suficientes cadastradas para o nivel " + nivel +
                " (sao necessarias pelo menos " + TOTAL_PERGUNTAS_POR_PARTIDA + ")."
            );
        }

        return perguntas.stream()
                .map(PerguntaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void processarFinalPartida(PartidaSubmitDTO dto) {
        Participante participante = participanteRepository.findById(dto.participanteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Participante nao encontrado: " + dto.participanteId()));

        validarNivel(dto.nivel());

        // BUG EVITADO: sem essa checagem, um payload malformado poderia
        // responder a mesma pergunta mais de uma vez na mesma partida.
        Set<Long> idsUnicos = new HashSet<>();
        for (RespostaSubmitDTO r : dto.respostas()) {
            if (!idsUnicos.add(r.perguntaId())) {
                throw new RegraDeNegocioException("A partida contem a pergunta " + r.perguntaId() + " respondida mais de uma vez.");
            }
        }

        Partida partida = new Partida();
        partida.setParticipante(participante);
        partida.setNivel(dto.nivel());

        int pontuacaoTotal = 0;
        int acertosTotal = 0;
        double tempoTotal = 0.0;

        for (RespostaSubmitDTO resDto : dto.respostas()) {
            Pergunta perguntaOficial = perguntaRepository.findById(resDto.perguntaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Pergunta nao encontrada: " + resDto.perguntaId()));

            if (!dto.nivel().equals(perguntaOficial.getNivel())) {
                throw new RegraDeNegocioException(
                    "A pergunta " + perguntaOficial.getId() + " nao pertence ao nivel " + dto.nivel() + "."
                );
            }

            Resposta resposta = new Resposta();
            boolean acertou = avaliarResposta(perguntaOficial, resDto, resposta);

            if (acertou) {
                pontuacaoTotal += PONTOS_POR_ACERTO;
                acertosTotal++;
            }
            tempoTotal += resDto.tempoGasto();

            resposta.setPartida(partida);
            resposta.setPergunta(perguntaOficial);
            resposta.setCorreta(acertou);
            resposta.setTempoResposta(resDto.tempoGasto());

            partida.getRespostas().add(resposta);
        }

        int totalPerguntas = dto.respostas().size();
        int errosTotal = totalPerguntas - acertosTotal;

        // MELHORIA (simplificacao do sistema de pontuacao): com 1 ponto por
        // acerto numa partida de exatamente 10 perguntas, pontuacao,
        // acertos e nota final (0 a 10) sao sempre o mesmo numero.
        // Exemplo: 8 acertos -> pontuacao 8 -> nota final 8.0.
        partida.setPontuacao(pontuacaoTotal);
        partida.setAcertos(acertosTotal);
        partida.setErros(errosTotal);
        partida.setTempoTotal(tempoTotal);
        partida.setTempoMedio(tempoTotal / totalPerguntas);
        partida.setNotaFinal((double) acertosTotal);

        partidaRepository.save(partida);
    }

    private boolean avaliarResposta(Pergunta pergunta, RespostaSubmitDTO resDto, Resposta resposta) {
        if (pergunta.getTipo() == TipoPergunta.MULTIPLA_ESCOLHA) {
            if (resDto.alternativaEscolhidaId() == null) {
                throw new RegraDeNegocioException(
                    "A pergunta " + pergunta.getId() + " e de multipla escolha: informe alternativaEscolhidaId."
                );
            }
            Alternativa escolhida = pergunta.getAlternativas().stream()
                    .filter(a -> a.getId().equals(resDto.alternativaEscolhidaId()))
                    .findFirst()
                    .orElseThrow(() -> new RegraDeNegocioException(
                        "A alternativa " + resDto.alternativaEscolhidaId() + " nao pertence a pergunta " + pergunta.getId() + "."
                    ));
            resposta.setAlternativaEscolhida(escolhida);
            return Boolean.TRUE.equals(escolhida.getCorreta());
        } else {
            if (resDto.respostaEscolhida() == null) {
                throw new RegraDeNegocioException(
                    "A pergunta " + pergunta.getId() + " e Verdadeiro/Falso: informe respostaEscolhida."
                );
            }
            resposta.setResposta(resDto.respostaEscolhida());
            return pergunta.getRespostaCorreta().equals(resDto.respostaEscolhida());
        }
    }

    private void validarNivel(Integer nivel) {
        if (nivel == null || nivel < NIVEL_MINIMO || nivel > NIVEL_MAXIMO) {
            throw new RegraDeNegocioException(
                "nivel invalido: deve ser um numero entre " + NIVEL_MINIMO + " e " + NIVEL_MAXIMO + "."
            );
        }
    }

    /**
     * MELHORIA (ranking separado por nivel, item 2/7): retorna o ranking
     * de um unico nivel por vez -- nunca mistura participantes de niveis
     * diferentes. Ordenacao: maior pontuacao e, em caso de empate, menor
     * tempo medio de resposta (ver PartidaRepository para a explicacao
     * completa da regra de desempate).
     */
    public List<RankingDTO> obterRanking(Integer nivel) {
        validarNivel(nivel);
        return partidaRepository.findByNivelOrderByPontuacaoDescTempoMedioAsc(nivel)
                .stream()
                .map(RankingDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
