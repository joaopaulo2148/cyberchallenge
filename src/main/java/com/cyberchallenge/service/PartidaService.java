package com.cyberchallenge.service;

import com.cyberchallenge.dto.PartidaSubmitDTO;
import com.cyberchallenge.dto.PerguntaDTO;
import com.cyberchallenge.dto.RankingDTO;
import com.cyberchallenge.dto.RespostaSubmitDTO;
import com.cyberchallenge.exception.RecursoNaoEncontradoException;
import com.cyberchallenge.exception.RegraDeNegocioException;
import com.cyberchallenge.model.Participante;
import com.cyberchallenge.model.Pergunta;
import com.cyberchallenge.model.Resposta;
import com.cyberchallenge.repository.ParticipanteRepository;
import com.cyberchallenge.repository.PerguntaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PartidaService {

    private static final int TOTAL_PERGUNTAS_POR_PARTIDA = 5;
    private static final int PONTOS_POR_ACERTO = 2;

    private final PerguntaRepository perguntaRepository;
    private final ParticipanteRepository participanteRepository;

    public PartidaService(PerguntaRepository perguntaRepository, ParticipanteRepository participanteRepository) {
        this.perguntaRepository = perguntaRepository;
        this.participanteRepository = participanteRepository;
    }

    // Retorna 5 perguntas aleatorias e ativas
    public List<PerguntaDTO> iniciarPartida() {
        List<Pergunta> perguntas = perguntaRepository.findRandomPerguntasAtivas();

        // BUG CORRIGIDO: se o banco de perguntas ativo tiver menos de 5 perguntas
        // cadastradas, o jogo simplesmente comecava incompleto e travava depois,
        // sem nenhum aviso claro do motivo.
        if (perguntas.size() < TOTAL_PERGUNTAS_POR_PARTIDA) {
            throw new RegraDeNegocioException(
                "Nao ha perguntas ativas suficientes cadastradas para iniciar uma partida " +
                "(sao necessarias pelo menos " + TOTAL_PERGUNTAS_POR_PARTIDA + ")."
            );
        }

        return perguntas.stream()
                .map(PerguntaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional // Garante que ou salva tudo (participante + respostas) ou desfaz em caso de erro
    public void processarFinalPartida(PartidaSubmitDTO dto) {
        // BUG CORRIGIDO: nome vazio/so-espacos nao era validado no back-end
        // (a regra 4/16 do briefing exige nome obrigatorio).
        String nome = dto.nomeParticipante() == null ? "" : dto.nomeParticipante().trim();
        if (nome.isEmpty()) {
            throw new RegraDeNegocioException("O nome do participante e obrigatorio.");
        }

        // BUG CORRIGIDO: nao havia checagem contra perguntas duplicadas na mesma
        // submissao (regra 16: "Exibir perguntas duplicadas dentro da mesma
        // partida" deve ser impedido).
        Set<Long> idsUnicos = new HashSet<>();
        for (RespostaSubmitDTO r : dto.respostas()) {
            if (!idsUnicos.add(r.perguntaId())) {
                throw new RegraDeNegocioException("A partida contem a pergunta " + r.perguntaId() + " respondida mais de uma vez.");
            }
        }

        Participante participante = new Participante();
        participante.setNome(nome);

        int pontuacaoTotal = 0;
        double tempoTotal = 0.0;

        for (RespostaSubmitDTO resDto : dto.respostas()) {
            // Busca a pergunta oficial no banco para evitar fraudes
            Pergunta perguntaOficial = perguntaRepository.findById(resDto.perguntaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Pergunta nao encontrada: " + resDto.perguntaId()));

            boolean acertou = perguntaOficial.getRespostaCorreta().equals(resDto.respostaEscolhida());

            if (acertou) {
                pontuacaoTotal += PONTOS_POR_ACERTO;
            }
            tempoTotal += resDto.tempoGasto();

            // Cria o registro da resposta individual para o Dashboard
            Resposta resposta = new Resposta();
            resposta.setParticipante(participante);
            resposta.setPergunta(perguntaOficial);
            resposta.setResposta(resDto.respostaEscolhida());
            resposta.setCorreta(acertou);
            resposta.setTempoResposta(resDto.tempoGasto());

            participante.getRespostas().add(resposta);
        }

        // Finaliza os calculos do participante
        participante.setPontuacao(pontuacaoTotal);
        participante.setTempoTotal(tempoTotal);
        participante.setTempoMedio(tempoTotal / dto.respostas().size());

        // O CascadeType.ALL na Entidade garante que as respostas sejam salvas junto com o participante
        participanteRepository.save(participante);
    }

    public List<RankingDTO> obterRanking() {
        return participanteRepository.findAllByOrderByPontuacaoDescTempoTotalAscTempoMedioAsc()
                .stream()
                .map(RankingDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
