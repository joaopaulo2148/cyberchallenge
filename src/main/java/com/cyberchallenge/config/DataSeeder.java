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
 * O estoque original (~240 perguntas Verdadeiro/Falso, seed/perguntas-seed.json)
 * foi mantido sem nenhuma alteracao, como pedido explicitamente na
 * reformulacao. O novo formato de multipla escolha (item 8) foi adicionado
 * como um segundo arquivo de seed (seed/perguntas-multipla-escolha.json),
 * carregado logo em seguida -- assim as perguntas antigas continuam
 * exatamente como estavam, e as novas apenas se somam ao estoque.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final String CAMINHO_SEED_VF = "seed/perguntas-seed.json";
    private static final String CAMINHO_SEED_MULTIPLA = "seed/perguntas-multipla-escolha.json";

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
        perguntas.addAll(carregarPerguntasMultiplaEscolha());

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

    @SuppressWarnings("unchecked")
    private List<Pergunta> carregarPerguntasMultiplaEscolha() throws Exception {
        List<Map<String, Object>> itens = lerJsonComoLista(CAMINHO_SEED_MULTIPLA);
        List<Pergunta> perguntas = new ArrayList<>();

        for (Map<String, Object> item : itens) {
            Pergunta p = new Pergunta();
            p.setTexto((String) item.get("texto"));
            p.setTema((String) item.get("tema"));
            p.setTipo(TipoPergunta.MULTIPLA_ESCOLHA);
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
