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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PartidaService {

    // MELHORIA (secao 1): cada partida agora tem exatamente 10 perguntas,
    // sorteadas aleatoriamente entre o estoque (aproximadamente 60) do nivel
    // escolhido pelo jogador.
    private static final int TOTAL_PERGUNTAS_POR_PARTIDA = 10;
    private static final int PONTOS_POR_ACERTO = 2;
    private static final int NIVEL_MINIMO = 1;
    private static final int NIVEL_MAXIMO = 4;

    private final PerguntaRepository perguntaRepository;
    private final ParticipanteRepository participanteRepository;

    public PartidaService(PerguntaRepository perguntaRepository, ParticipanteRepository participanteRepository) {
        this.perguntaRepository = perguntaRepository;
        this.participanteRepository = participanteRepository;
    }

    /**
     * Retorna 10 perguntas aleatorias e ativas do nivel escolhido.
     *
     * MELHORIA (secao 1): quando o front-end informa uma lista de IDs
     * usados na partida anterior daquele mesmo nivel (parametro
     * idsRecentes), o sistema tenta evitar repeti-los imediatamente, para
     * aumentar a variedade entre partidas seguidas ("jogar novamente").
     * Se, apos excluir esses IDs, nao houver mais perguntas suficientes no
     * estoque, o sistema automaticamente cai de volta para o sorteio sem
     * exclusao — o jogo nunca fica bloqueado por falta de perguntas.
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

        // Fallback: sem lista de exclusao, ou a exclusao deixou menos
        // perguntas do que o necessario para completar a partida.
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

    @Transactional // Garante que ou salva tudo (participante + respostas) ou desfaz em caso de erro
    public void processarFinalPartida(PartidaSubmitDTO dto) {
        // BUG CORRIGIDO: nome vazio/so-espacos nao era validado no back-end.
        String nome = dto.nomeParticipante() == null ? "" : dto.nomeParticipante().trim();
        if (nome.isEmpty()) {
            throw new RegraDeNegocioException("O nome do participante e obrigatorio.");
        }

        validarNivel(dto.nivel());

        // BUG CORRIGIDO: nao havia checagem contra perguntas duplicadas na mesma
        // submissao. A partida deve conter exatamente TOTAL_PERGUNTAS_POR_PARTIDA
        // respostas, sem repetir a mesma pergunta.
        Set<Long> idsUnicos = new HashSet<>();
        for (RespostaSubmitDTO r : dto.respostas()) {
            if (!idsUnicos.add(r.perguntaId())) {
                throw new RegraDeNegocioException("A partida contem a pergunta " + r.perguntaId() + " respondida mais de uma vez.");
            }
        }

        Participante participante = new Participante();
        participante.setNome(nome);
        participante.setIdade(dto.idade());
        participante.setAutoavaliacao(dto.autoavaliacao());
        participante.setNivel(dto.nivel());

        int pontuacaoTotal = 0;
        int acertosTotal = 0;
        double tempoTotal = 0.0;

        for (RespostaSubmitDTO resDto : dto.respostas()) {
            // Busca a pergunta oficial no banco para evitar fraudes
            Pergunta perguntaOficial = perguntaRepository.findById(resDto.perguntaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Pergunta nao encontrada: " + resDto.perguntaId()));

            // MELHORIA (secao 2): garante que todas as perguntas respondidas
            // realmente pertencem ao nivel informado pelo participante,
            // evitando que uma partida misture perguntas de niveis diferentes.
            if (!dto.nivel().equals(perguntaOficial.getNivel())) {
                throw new RegraDeNegocioException(
                    "A pergunta " + perguntaOficial.getId() + " nao pertence ao nivel " + dto.nivel() + "."
                );
            }

            boolean acertou = perguntaOficial.getRespostaCorreta().equals(resDto.respostaEscolhida());

            if (acertou) {
                pontuacaoTotal += PONTOS_POR_ACERTO;
                acertosTotal++;
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

        int totalPerguntas = dto.respostas().size();

        // Finaliza os calculos do participante
        participante.setPontuacao(pontuacaoTotal);
        participante.setTempoTotal(tempoTotal);
        participante.setTempoMedio(tempoTotal / totalPerguntas);
        participante.setNotaFinal(calcularNotaFinal(acertosTotal, totalPerguntas));

        // O CascadeType.ALL na Entidade garante que as respostas sejam salvas junto com o participante
        participanteRepository.save(participante);
    }

    /**
     * MELHORIA (secao 4): calculo da pontuacao final, numa escala de 0 a 10.
     *
     * Regra (determinística, simples e documentada): a nota e a proporcao
     * de acertos sobre o total de perguntas da partida, multiplicada por 10
     * e arredondada para uma casa decimal.
     *
     *     nota = round((acertos / totalPerguntas) * 10, 1 casa decimal)
     *
     * Exemplos com 10 perguntas: 10 acertos -> nota 10.0; 7 acertos ->
     * nota 7.0; 0 acertos -> nota 0.0. Optou-se por uma escala de 0 a 10
     * (em vez de forcar um piso artificial em 1) porque zero acertos deve,
     * de fato, corresponder a nota minima da escala.
     */
    private double calcularNotaFinal(int acertos, int totalPerguntas) {
        if (totalPerguntas == 0) {
            return 0.0;
        }
        double nota = (acertos * 10.0) / totalPerguntas;
        return Math.round(nota * 10.0) / 10.0;
    }

    private void validarNivel(Integer nivel) {
        if (nivel == null || nivel < NIVEL_MINIMO || nivel > NIVEL_MAXIMO) {
            throw new RegraDeNegocioException(
                "nivel invalido: deve ser um numero entre " + NIVEL_MINIMO + " e " + NIVEL_MAXIMO + "."
            );
        }
    }

    public List<RankingDTO> obterRanking() {
        return participanteRepository.findAllByOrderByPontuacaoDescTempoTotalAscTempoMedioAsc()
                .stream()
                .map(RankingDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
