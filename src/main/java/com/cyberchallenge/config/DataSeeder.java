package com.cyberchallenge.config;

import com.cyberchallenge.model.Pergunta;
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
 * MELHORIA (secao 1): o estoque de perguntas cresceu de 18 para
 * aproximadamente 240 (60 por nivel de dificuldade, secao 2). Por conta do
 * volume, as perguntas ficam em um arquivo JSON de dados
 * (src/main/resources/seed/perguntas-seed.json) em vez de um literal Java
 * gigante, lido aqui com o ObjectMapper que o Spring Boot ja disponibiliza
 * (dependencia Jackson, ja incluida via spring-boot-starter-web). Isso
 * evita reescrever a arquitetura existente do DataSeeder — apenas troca a
 * origem dos dados que ele insere.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final String CAMINHO_SEED = "seed/perguntas-seed.json";

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

        List<Map<String, Object>> perguntasSeed = carregarPerguntasDoJson();

        List<Pergunta> perguntas = new ArrayList<>();
        for (Map<String, Object> item : perguntasSeed) {
            Pergunta p = new Pergunta();
            p.setTexto((String) item.get("texto"));
            p.setTema((String) item.get("tema"));
            p.setRespostaCorreta((Boolean) item.get("respostaCorreta"));
            p.setExplicacao((String) item.get("explicacao"));
            p.setNivel((Integer) item.get("nivel"));
            p.setAtiva(true);
            perguntas.add(p);
        }

        perguntaRepository.saveAll(perguntas);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> carregarPerguntasDoJson() throws Exception {
        try (InputStream in = new ClassPathResource(CAMINHO_SEED).getInputStream()) {
            return objectMapper.readValue(in, List.class);
        }
    }
}
