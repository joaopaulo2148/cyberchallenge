# CYBER CHALLENGE — Full-stack (Spring Boot + Front-end integrado)

Jogo educativo de conscientização em Segurança Cibernética: back-end corrigido
e reestruturado, com o front-end (`index.html` / `script.js` / `style.css`)
já integrado e consumindo a API de verdade.

## Como rodar

Pré-requisitos: Java 17+ e Maven (ou use o wrapper da sua IDE).

```bash
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080` — **é só abrir essa URL no
navegador que o próprio jogo já aparece**, servido pelo Spring Boot
(`src/main/resources/static`). Não precisa rodar um servidor de front-end
separado.

Na primeira execução, o `DataSeeder` popula automaticamente o banco com 18
perguntas cobrindo os 10 temas do briefing. O banco é um H2 em arquivo
(`./data/cyberchallenge.mv.db`), então os dados persistem entre reinícios —
importante para não perder o ranking/dashboard durante a atividade presencial.

Console do H2 (para inspecionar o banco manualmente, se precisar):
`http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/cyberchallenge`, usuário `sa`, senha em branco).

## Endpoints

### Jogo
- `GET  /api/partidas/iniciar` → sorteia 5 perguntas ativas
- `POST /api/partidas/finalizar` → envia o resultado da partida
  ```json
  {
    "nomeParticipante": "Maria",
    "respostas": [
      { "perguntaId": 1, "respostaEscolhida": true, "tempoGasto": 7.82 }
    ]
  }
  ```
- `GET  /api/partidas/ranking` → ranking geral

### Dashboard
- `GET /api/dashboard` → indicadores gerais, estatísticas por pergunta, por tema e por tempo

### Administração
- `GET    /api/admin/perguntas` — listar todas
- `GET    /api/admin/perguntas/{id}`
- `POST   /api/admin/perguntas` — criar
- `PUT    /api/admin/perguntas/{id}` — editar
- `DELETE /api/admin/perguntas/{id}` — excluir
- `PATCH  /api/admin/perguntas/{id}/status` — `{ "ativa": false }`
- `GET    /api/admin/participantes` — listar participantes
- `GET    /api/admin/participantes/{id}/respostas` — respostas de um participante

> A área administrativa ainda não tem autenticação. Antes de expor isso fora
> de um notebook controlado durante o evento, vale colocar um login simples
> (ex: Spring Security com usuário/senha básicos) na frente de `/api/admin/**`.

## Bugs corrigidos em relação ao código enviado

1. **Pacote errado** em `Pergunta.java` e `Resposta.java` (`cyberchallenge.model`
   em vez de `com.cyberchallenge.model`) — impedia a compilação de todo o projeto.
2. **Chaves soltas** sobrando no fim de `PerguntaRepository.java` — erro de sintaxe.
3. **Faltava o projeto Spring Boot inteiro**: sem `pom.xml`, sem classe
   `@SpringBootApplication`, sem `application.properties`, e as classes fora
   da estrutura `src/main/java/...`. Nada disso rodava.
4. **Sem validação de nome vazio** no cadastro do participante.
5. **Sem validação de quantidade/duplicidade de respostas** (a partida podia
   ser finalizada com qualquer número de respostas, inclusive repetidas).
6. **Sem tratamento de erro**: qualquer problema virava HTTP 500 genérico.
   Agora há um `GlobalExceptionHandler` retornando JSON claro com status
   apropriado (400/404/500).
7. **Sem nenhuma pergunta cadastrada**: `/iniciar` sempre retornava lista
   vazia. Agora há um `DataSeeder` com 18 perguntas nos 10 temas do briefing.
8. **Faltava Dashboard e Área Administrativa** por completo — implementados
   do zero (`DashboardService`/`DashboardController`,
   `PerguntaAdminService`/`AdminPerguntaController`,
   `ParticipanteAdminService`/`AdminParticipanteController`).

## Dashboard administrativo (`/admin`)

Acesse `http://localhost:8080/admin` para ver o Dashboard (seção 12 do
briefing) direto no navegador: indicadores gerais, destaques de pergunta
(mais acertada/errada, maior/menor tempo médio), desempenho por tema com
barras de progresso, e uma tabela detalhada por pergunta. Os dados vêm de
`GET /api/dashboard` e são atualizados via botão "ATUALIZAR" na tela.

Se ainda não houver nenhuma partida jogada, a tela mostra um aviso em vez de
uma tabela vazia. Se a API estiver fora do ar, mostra um erro claro em vez de
travar.

> Assim como o restante da área administrativa, esta tela ainda não tem
> autenticação — é só uma URL "escondida", não uma URL protegida. Se for
> usar fora de um notebook controlado, vale colocar Spring Security na frente.

## Integração do front-end com a API

O `script.js` original usava um banco de perguntas mockado em memória e nunca
conversava com o back-end. Isso foi reescrito para:

- `GET /api/partidas/iniciar` — busca as 5 perguntas reais do banco ao clicar em "CONTINUAR" no cadastro;
- Durante a partida, cada resposta é guardada localmente (`perguntaId`, `respostaEscolhida`, `tempoGasto`);
- `POST /api/partidas/finalizar` — envia tudo de uma vez ao final da 5ª pergunta;
- `GET /api/partidas/ranking` — busca o ranking atualizado (compartilhado entre todos os participantes da atividade) para exibir na tela de resultado.

Se a API estiver fora do ar, o front mostra um aviso (`⚠️`) em vez de travar
silenciosamente — tanto no cadastro (falha ao buscar perguntas) quanto no
resultado final (falha ao salvar/consultar o ranking).

## Observação de design (não é bug, mas vale registrar)

O endpoint `/api/partidas/iniciar` retorna `respostaCorreta` e `explicacao`
junto com cada pergunta, porque o front-end atual avalia a resposta e mostra
o feedback educativo localmente, sem round-trip ao servidor a cada pergunta.
Isso é aceitável para uma atividade presencial e supervisionada. Se no futuro
isso for exposto publicamente na internet com valor competitivo real no
ranking, o ideal é criar um endpoint que avalia uma pergunta por vez no
servidor, sem nunca expor a resposta correta antes do envio.
