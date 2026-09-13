package com.cyberchallenge.config;

import com.cyberchallenge.model.Alternativa;
import com.cyberchallenge.model.Pergunta;
import com.cyberchallenge.model.TipoPergunta;
import com.cyberchallenge.repository.PerguntaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Popula o banco de perguntas na primeira execucao.
 *
 * O seed roda apenas se a tabela estiver vazia, entao e seguro reiniciar a
 * aplicacao varias vezes sem duplicar perguntas.
 *
 * O estoque original (240 perguntas Verdadeiro/Falso, seed/perguntas-seed.json)
 * foi mantido sem nenhuma alteracao. As perguntas de multipla escolha (41,
 * seed/perguntas-multipla-escolha.json) e de completar a frase (120,
 * seed/perguntas-completar-frase.json) foram adicionadas por cima, sem
 * remover ou modificar nada do estoque original -- total de 401 perguntas.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final String CAMINHO_SEED_VF = "seed/perguntas-seed.json";
    private static final String CAMINHO_SEED_MULTIPLA = "seed/perguntas-multipla-escolha.json";
    private static final String CAMINHO_SEED_COMPLETAR = "seed/perguntas-completar-frase.json";

    private final PerguntaRepository perguntaRepository;
    private final ObjectMapper objectMapper;

    public DataSeeder(PerguntaRepository perguntaRepository, ObjectMapper objectMapper) {
        this.perguntaRepository = perguntaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (perguntaRepository.count() > 0) {
            return;
        }

        List<Pergunta> perguntas = new ArrayList<>();
        perguntas.addAll(carregarPerguntasVerdadeiroFalso());
        perguntas.addAll(carregarPerguntasComAlternativas(CAMINHO_SEED_MULTIPLA, TipoPergunta.MULTIPLA_ESCOLHA));
        perguntas.addAll(carregarPerguntasComAlternativas(CAMINHO_SEED_COMPLETAR, TipoPergunta.COMPLETAR_FRASE));

        perguntaRepository.saveAll(perguntas);
    }

    private List<Pergunta> carregarPerguntasVerdadeiroFalso() throws Exception {
        List<Map<String, Object>> itens = lerJsonComoLista(CAMINHO_SEED_VF);
        List<Pergunta> perguntas = new ArrayList<>();

        for (Map<String, Object> item : itens) {
            Pergunta p = new Pergunta();
            p.setTexto((String) item.get("texto"));
            p.setTema((String) item.get("tema"));
            p.setTipo(TipoPergunta.VERDADEIRO_FALSO);
            p.setRespostaCorreta((Boolean) item.get("respostaCorreta"));
            p.setExplicacao((String) item.get("explicacao"));
            p.setNivel((Integer) item.get("nivel"));
            p.setAtiva(true);
            perguntas.add(p);
        }
        return perguntas;
    }

    // MULTIPLA_ESCOLHA e COMPLETAR_FRASE usam exatamente o mesmo formato de
    // arquivo (texto/tema/nivel/explicacao/alternativas), entao carregamos
    // as duas com o mesmo metodo, so trocando o tipo atribuido a pergunta.
    @SuppressWarnings("unchecked")
    private List<Pergunta> carregarPerguntasComAlternativas(String caminhoClasspath, TipoPergunta tipo) throws Exception {
        List<Map<String, Object>> itens = lerJsonComoLista(caminhoClasspath);
        List<Pergunta> perguntas = new ArrayList<>();

        for (Map<String, Object> item : itens) {
            Pergunta p = new Pergunta();
            p.setTexto((String) item.get("texto"));
            p.setTema((String) item.get("tema"));
            p.setTipo(tipo);
            p.setRespostaCorreta(null);
            p.setExplicacao((String) item.get("explicacao"));
            p.setNivel((Integer) item.get("nivel"));
            p.setAtiva(true);

            List<Map<String, Object>> alternativasJson = (List<Map<String, Object>>) item.get("alternativas");
            List<Alternativa> alternativas = new ArrayList<>();
            for (Map<String, Object> altJson : alternativasJson) {
                Alternativa alt = new Alternativa((String) altJson.get("texto"), (Boolean) altJson.get("correta"));
                alt.setPergunta(p);
                alternativas.add(alt);
            }
            p.setAlternativas(alternativas);

            perguntas.add(p);
        }
        return perguntas;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> lerJsonComoLista(String caminhoClasspath) throws Exception {
        try (InputStream in = new ClassPathResource(caminhoClasspath).getInputStream()) {
            return objectMapper.readValue(in, List.class);
        }
    }
}
