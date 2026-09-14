package com.cyberchallenge.service;

import com.cyberchallenge.dto.dashboard.DashboardDTO;
import com.cyberchallenge.dto.dashboard.IndicadoresGeraisDTO;
import com.cyberchallenge.dto.dashboard.NivelEstatisticaDTO;
import com.cyberchallenge.dto.dashboard.PerguntaEstatisticaDTO;
import com.cyberchallenge.dto.dashboard.TemaEstatisticaDTO;
import com.cyberchallenge.model.Partida;
import com.cyberchallenge.model.Pergunta;
import com.cyberchallenge.model.Resposta;
import com.cyberchallenge.repository.PartidaRepository;
import com.cyberchallenge.repository.RespostaRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Calcula as metricas da pagina publica de Estatisticas Gerais. Publica,
 * sem nenhuma restricao de acesso -- nao existe area administrativa neste
 * projeto.
 *
 * Para o volume de dados de uma atividade extensionista (algumas
 * dezenas/centenas de partidas), agregar em memoria com streams e simples,
 * correto e facil de manter.
 */
@Service
public class DashboardService {

    private static final Map<Integer, String> NOMES_NIVEL = Map.of(
        1, "Leigo", 2, "Basico", 3, "Intermediario", 4, "Especialista"
    );

    private final PartidaRepository partidaRepository;
    private final RespostaRepository respostaRepository;

    public DashboardService(PartidaRepository partidaRepository, RespostaRepository respostaRepository) {
        this.partidaRepository = partidaRepository;
        this.respostaRepository = respostaRepository;
    }

    public DashboardDTO gerarDashboard() {
        List<Partida> partidas = partidaRepository.findAll();
        List<Resposta> respostas = respostaRepository.findAllComPergunta();

        IndicadoresGeraisDTO indicadores = calcularIndicadoresGerais(partidas);
        List<NivelEstatisticaDTO> niveis = calcularEstatisticasPorNivel(partidas);

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
            indicadores, niveis,
            perguntas, maisAcertada, maisErrada, maiorTempoMedio, menorTempoMedio,
            temas, temaMaiorAcerto, temaMaiorErro
        );
    }

    private IndicadoresGeraisDTO calcularIndicadoresGerais(List<Partida> partidas) {
        long totalPartidas = partidas.size();

        if (totalPartidas == 0) {
            return new IndicadoresGeraisDTO(0, 0, 0, 0, 0, 0, 0, 0);
        }

        // MELHORIA (simplificacao do sistema de pontuacao): pontuacao,
        // acertos e nota final agora sao sempre o mesmo numero (0 a 10,
        // 1 ponto por acerto em 10 perguntas).
        double mediaPontuacao = partidas.stream()
                .mapToInt(Partida::getPontuacao)
                .average().orElse(0);

        double mediaAcertos = mediaPontuacao;
        double taxaGeralAcerto = (mediaAcertos / 10.0) * 100.0;

        double tempoMedioPartidas = partidas.stream()
                .mapToDouble(Partida::getTempoTotal)
                .average().orElse(0);

        // "participantes" nas estatisticas gerais = numero de partidas
        // jogadas (nao o numero de contas cadastradas, ja que o mesmo
        // nickname so pode existir uma vez -- ver Participante).
        long totalParticipantes = partidas.stream()
                .map(p -> p.getParticipante().getId())
                .distinct()
                .count();

        double mediaAutoavaliacao = partidas.stream()
                .map(Partida::getParticipante)
                .filter(p -> p.getAutoavaliacao() != null)
                .mapToInt(com.cyberchallenge.model.Participante::getAutoavaliacao)
                .average().orElse(0);

        double mediaNotaFinal = partidas.stream()
                .filter(p -> p.getNotaFinal() != null)
                .mapToDouble(Partida::getNotaFinal)
                .average().orElse(0);

        return new IndicadoresGeraisDTO(
            totalParticipantes, totalPartidas, mediaPontuacao, mediaAcertos, taxaGeralAcerto,
            tempoMedioPartidas, mediaAutoavaliacao, mediaNotaFinal
        );
    }

    private List<NivelEstatisticaDTO> calcularEstatisticasPorNivel(List<Partida> partidas) {
        Map<Integer, List<Partida>> porNivel = partidas.stream()
                .filter(p -> p.getNivel() != null)
                .collect(Collectors.groupingBy(Partida::getNivel));

        return porNivel.entrySet().stream()
                .map(entry -> {
                    Integer nivel = entry.getKey();
                    List<Partida> lista = entry.getValue();
                    long total = lista.size();
                    double mediaPontuacao = lista.stream().mapToInt(Partida::getPontuacao).average().orElse(0);
                    double mediaNotaFinal = lista.stream()
                            .filter(p -> p.getNotaFinal() != null)
                            .mapToDouble(Partida::getNotaFinal)
                            .average().orElse(0);
                    double percentualAcerto = (mediaPontuacao / 10.0) * 100.0;

                    return new NivelEstatisticaDTO(
                        nivel, NOMES_NIVEL.getOrDefault(nivel, "Nivel " + nivel),
                        total, mediaPontuacao, mediaNotaFinal, percentualAcerto
                    );
                })
                .sorted(Comparator.comparing(NivelEstatisticaDTO::nivel))
                .collect(Collectors.toList());
    }

    private List<PerguntaEstatisticaDTO> calcularEstatisticasPorPergunta(List<Resposta> respostas) {
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
                    long qtdFalso = lista.stream().filter(r -> Boolean.FALSE.equals(r.getResposta())).count();

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
