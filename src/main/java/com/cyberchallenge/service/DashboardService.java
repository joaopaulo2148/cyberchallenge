package com.cyberchallenge.service;

import com.cyberchallenge.dto.dashboard.DashboardDTO;
import com.cyberchallenge.dto.dashboard.IndicadoresGeraisDTO;
import com.cyberchallenge.dto.dashboard.PerguntaEstatisticaDTO;
import com.cyberchallenge.dto.dashboard.TemaEstatisticaDTO;
import com.cyberchallenge.model.Participante;
import com.cyberchallenge.model.Pergunta;
import com.cyberchallenge.model.Resposta;
import com.cyberchallenge.repository.ParticipanteRepository;
import com.cyberchallenge.repository.RespostaRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Calcula as metricas descritas na secao 12 do briefing (Dashboard).
 *
 * Para o volume de dados de uma atividade extensionista presencial (algumas
 * dezenas/centenas de partidas), agregar em memoria com streams e simples,
 * correto e facil de manter — nao ha necessidade de queries agregadas
 * complexas no banco para esse caso de uso.
 */
@Service
public class DashboardService {

    private final ParticipanteRepository participanteRepository;
    private final RespostaRepository respostaRepository;

    public DashboardService(ParticipanteRepository participanteRepository, RespostaRepository respostaRepository) {
        this.participanteRepository = participanteRepository;
        this.respostaRepository = respostaRepository;
    }

    public DashboardDTO gerarDashboard() {
        List<Participante> participantes = participanteRepository.findAll();
        List<Resposta> respostas = respostaRepository.findAllComPergunta();

        IndicadoresGeraisDTO indicadores = calcularIndicadoresGerais(participantes);

        List<PerguntaEstatisticaDTO> perguntas = calcularEstatisticasPorPergunta(respostas);
        List<TemaEstatisticaDTO> temas = calcularEstatisticasPorTema(respostas);

        PerguntaEstatisticaDTO maisAcertada = perguntas.stream()
                .max(Comparator.comparingDouble(PerguntaEstatisticaDTO::percentualAcerto))
                .orElse(null);
        PerguntaEstatisticaDTO maisErrada = perguntas.stream()
                .max(Comparator.comparingDouble(PerguntaEstatisticaDTO::percentualErro))
                .orElse(null);
        PerguntaEstatisticaDTO maiorTempoMedio = perguntas.stream()
                .max(Comparator.comparingDouble(PerguntaEstatisticaDTO::tempoMedioSegundos))
                .orElse(null);
        PerguntaEstatisticaDTO menorTempoMedio = perguntas.stream()
                .min(Comparator.comparingDouble(PerguntaEstatisticaDTO::tempoMedioSegundos))
                .orElse(null);

        TemaEstatisticaDTO temaMaiorAcerto = temas.stream()
                .max(Comparator.comparingDouble(TemaEstatisticaDTO::percentualAcerto))
                .orElse(null);
        TemaEstatisticaDTO temaMaiorErro = temas.stream()
                .min(Comparator.comparingDouble(TemaEstatisticaDTO::percentualAcerto))
                .orElse(null);

        return new DashboardDTO(
            indicadores,
            perguntas, maisAcertada, maisErrada, maiorTempoMedio, menorTempoMedio,
            temas, temaMaiorAcerto, temaMaiorErro
        );
    }

    private IndicadoresGeraisDTO calcularIndicadoresGerais(List<Participante> participantes) {
        long totalParticipantes = participantes.size();

        if (totalParticipantes == 0) {
            return new IndicadoresGeraisDTO(0, 0, 0, 0, 0, 0);
        }

        double mediaPontuacao = participantes.stream()
                .mapToInt(Participante::getPontuacao)
                .average().orElse(0);

        // Cada participante joga exatamente 5 perguntas (regra fixa do jogo)
        double mediaAcertos = mediaPontuacao / 2.0;
        double taxaGeralAcerto = (mediaAcertos / 5.0) * 100.0;

        double tempoMedioPartidas = participantes.stream()
                .mapToDouble(Participante::getTempoTotal)
                .average().orElse(0);

        // No modelo atual, 1 registro de Participante = 1 partida jogada
        long totalPartidas = totalParticipantes;

        return new IndicadoresGeraisDTO(
            totalParticipantes, totalPartidas, mediaPontuacao, mediaAcertos, taxaGeralAcerto, tempoMedioPartidas
        );
    }

    private List<PerguntaEstatisticaDTO> calcularEstatisticasPorPergunta(List<Resposta> respostas) {
        // Agrupamos pelo ID (nao pela entidade em si) para nao depender de
        // equals()/hashCode() de entidade JPA, que por padrao e por identidade
        // de objeto e pode se comportar de forma inesperada fora de um unico
        // contexto de persistencia.
        Map<Long, List<Resposta>> porPergunta = respostas.stream()
                .collect(Collectors.groupingBy(r -> r.getPergunta().getId()));

        return porPergunta.entrySet().stream()
                .map(entry -> {
                    List<Resposta> lista = entry.getValue();
                    Pergunta pergunta = lista.get(0).getPergunta();

                    long total = lista.size();
                    long acertos = lista.stream().filter(Resposta::getCorreta).count();
                    long erros = total - acertos;
                    long qtdVerdadeiro = lista.stream().filter(r -> Boolean.TRUE.equals(r.getResposta())).count();
                    long qtdFalso = total - qtdVerdadeiro;
                    double tempoMedio = lista.stream().mapToDouble(Resposta::getTempoResposta).average().orElse(0);

                    double percentualAcerto = total == 0 ? 0 : (acertos * 100.0) / total;
                    double percentualErro = total == 0 ? 0 : (erros * 100.0) / total;

                    return new PerguntaEstatisticaDTO(
                        pergunta.getId(), pergunta.getTexto(), pergunta.getTema(),
                        total, acertos, erros, percentualAcerto, percentualErro,
                        qtdVerdadeiro, qtdFalso, tempoMedio
                    );
                })
                .sorted(Comparator.comparing(PerguntaEstatisticaDTO::perguntaId))
                .collect(Collectors.toList());
    }

    private List<TemaEstatisticaDTO> calcularEstatisticasPorTema(List<Resposta> respostas) {
        Map<String, List<Resposta>> porTema = respostas.stream()
                .collect(Collectors.groupingBy(r -> r.getPergunta().getTema()));

        return porTema.entrySet().stream()
                .map(entry -> {
                    String tema = entry.getKey();
                    List<Resposta> lista = entry.getValue();
                    long total = lista.size();
                    long acertos = lista.stream().filter(Resposta::getCorreta).count();
                    double percentualAcerto = total == 0 ? 0 : (acertos * 100.0) / total;

                    return new TemaEstatisticaDTO(tema, total, acertos, percentualAcerto);
                })
                .sorted(Comparator.comparing(TemaEstatisticaDTO::tema))
                .collect(Collectors.toList());
    }
}
