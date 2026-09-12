# Cyber Challenge

Jogo educativo de conscientização em segurança cibernética, com back-end em Spring Boot, banco de dados MySQL e front-end estático responsivo. O projeto não possui sistema de contas: o participante informa apenas um nickname, uma idade e uma autoavaliação de conhecimento antes de cada partida.

---

## Sumário

- [Descrição](#descrição)
- [Objetivo e problema abordado](#objetivo-e-problema-abordado)
- [Público-alvo](#público-alvo)
- [Funcionalidades](#funcionalidades)
- [Como funciona: fluxo do jogo](#como-funciona-fluxo-do-jogo)
- [Níveis de dificuldade](#níveis-de-dificuldade)
- [Sistema de perguntas](#sistema-de-perguntas)
- [Sistema de pontuação](#sistema-de-pontuação)
- [Ranking por nível](#ranking-por-nível)
- [Estatísticas gerais](#estatísticas-gerais)
- [Identidade visual e temas](#identidade-visual-e-temas)
- [Tecnologias utilizadas](#tecnologias-utilizadas)
- [Arquitetura](#arquitetura)
- [Estrutura de pastas](#estrutura-de-pastas)
- [Banco de dados](#banco-de-dados)
- [Principais entidades](#principais-entidades)
- [API](#api)
- [Requisitos](#requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Execução](#execução)
- [Como acessar e utilizar](#como-acessar-e-utilizar)
- [Validação e testes realizados](#validação-e-testes-realizados)
- [Responsividade](#responsividade)
- [Autores](#autores)
- [Trabalhos futuros](#trabalhos-futuros)
- [Licença](#licença)

---

## Descrição

O Cyber Challenge apresenta ao participante 10 perguntas sobre segurança cibernética, no formato Verdadeiro/Falso ou de múltipla escolha, sorteadas de um banco de 281 questões distribuídas em quatro níveis de dificuldade. Ao final de cada partida, o participante recebe uma nota de 0 a 10 e pode consultar sua posição no ranking do nível jogado. Uma página pública de estatísticas gerais reúne indicadores agregados de todas as partidas já realizadas.

## Objetivo e problema abordado

Grande parte dos incidentes de segurança digital (phishing, senhas fracas, golpes de engenharia social) explora a falta de conhecimento básico do usuário comum, e não apenas falhas técnicas sofisticadas. O Cyber Challenge propõe uma forma leve, rápida e sem barreiras de entrada (sem necessidade de criar conta) para testar e reforçar esse conhecimento, com feedback educativo imediato após cada resposta.

## Público-alvo

Estudantes, participantes de atividades de extensão e oficinas de conscientização em segurança da informação, e qualquer pessoa interessada em avaliar seu próprio conhecimento sobre boas práticas de segurança digital, independentemente do nível técnico prévio.

## Funcionalidades

- Menu inicial simples, com quatro opções: Jogar, Tutorial, Ranking e Estatísticas Gerais.
- Identificação do participante por nickname (único), idade e autoavaliação de conhecimento (1 a 10), sem necessidade de conta, senha ou e-mail.
- Escolha entre quatro níveis de dificuldade antes de cada partida.
- Partidas com exatamente 10 perguntas sorteadas aleatoriamente do nível escolhido, com variedade entre partidas seguidas.
- Perguntas em dois formatos: Verdadeiro/Falso e múltipla escolha.
- Feedback educativo imediato após cada resposta (correta/incorreta + explicação).
- Navegação entre perguntas durante a partida, incluindo o botão "Pergunta anterior" para revisar ou corrigir uma resposta já dada.
- Tela de resultado com nota final (0 a 10), acertos, erros, percentual de aproveitamento e tempo médio de resposta.
- Ranking separado por nível de dificuldade (nunca mistura participantes de níveis diferentes).
- Página pública de Estatísticas Gerais, com indicadores agregados, desempenho por nível e por tema, e comparação entre autoavaliação e desempenho real.
- Tema claro (fundo em tom creme) e tema escuro, alternáveis por um ícone na barra superior, com a preferência lembrada no navegador.
- Interface responsiva para desktop, notebook, tablet e celular.

## Como funciona: fluxo do jogo

```
Menu Inicial
    |
    |-- Tutorial -----------------> Voltar ao Menu
    |-- Ranking (por nível) ------> Voltar ao Menu
    |-- Estatísticas Gerais ------> Voltar ao Menu
    |
    +-- Jogar
          |
          v
    Identificação (nickname, idade, autoavaliação 1-10)
          |
          v
       Próximo
          |
          v
    Escolha do nível (Leigo, Usuário, Intermediário, Especialista)
          |
          v
    Partida (10 perguntas, com feedback e "Pergunta anterior")
          |
          v
       Resultado (nota 0-10, acertos, erros, aproveitamento, tempo médio)
          |
          v
       Voltar ao Menu  /  Jogar novamente  /  Ver ranking
```

O participante permanece identificado apenas durante o uso da página (não há sessão persistida no servidor); ao clicar em "Jogar" novamente dentro da mesma visita, o nickname já cadastrado é reaproveitado automaticamente para a nova partida.

## Níveis de dificuldade

| Nível | Nome | Público-alvo |
|---|---|---|
| 1 | Leigo | Pouco ou nenhum conhecimento sobre segurança cibernética |
| 2 | Usuário | Situações comuns do dia a dia |
| 3 | Intermediário | Conhecimento mais aprofundado sobre segurança digital |
| 4 | Especialista | Questões avançadas de cibersegurança |

## Sistema de perguntas

O banco de perguntas tem 281 questões ativas, distribuídas entre os quatro níveis:

- 240 perguntas no formato Verdadeiro/Falso (estoque original do projeto, preservado integralmente).
- 41 perguntas no formato de múltipla escolha (4 alternativas cada, com exatamente uma correta), adicionadas para diversificar o formato do jogo.

Cada partida sorteia 10 perguntas do nível escolhido, podendo misturar os dois formatos. O sistema tenta evitar repetir, na partida seguinte do mesmo nível, as mesmas 10 perguntas usadas na partida anterior; se o estoque restante não for suficiente para isso, o sorteio cai de volta para o conjunto completo do nível, garantindo que o jogo nunca fique bloqueado por falta de perguntas.

## Sistema de pontuação

A pontuação foi simplificada: cada uma das 10 perguntas vale exatamente 1 ponto, sem pesos diferentes entre elas.

```
nota_final = número de respostas corretas (0 a 10)
```

Exemplo: 8 acertos em 10 perguntas resultam em pontuação 8 e nota final 8.0. A pontuação máxima possível em uma partida é 10.

Além da nota final, a tela de resultado exibe: acertos, erros, percentual de aproveitamento, tempo médio de resposta e o nível escolhido.

## Ranking por nível

Existem quatro rankings independentes, um por nível de dificuldade -- participantes de níveis diferentes nunca aparecem na mesma lista. A página de Ranking apresenta os quatro conjuntos através de abas, deixando sempre claro qual nível está sendo exibido.

Critério de ordenação, documentado também no código (`PartidaRepository`):

1. Maior pontuação;
2. Em caso de empate, menor tempo médio de resposta.

O critério adicional de "maior percentual de acerto", citado como segundo desempate, é matematicamente equivalente ao da pontuação nesta implementação: como toda partida tem sempre 10 perguntas, o percentual de acerto é sempre a pontuação multiplicada por 10 -- ou seja, dois participantes empatados em pontuação estão necessariamente empatados também em percentual de acerto. Por isso, o desempate que efetivamente diferencia os participantes é o tempo médio de resposta.

## Estatísticas gerais

Página pública (sem qualquer restrição de acesso ou conta de administrador), com:

- Indicadores gerais: total de partidas, total de participantes, pontuação média, taxa geral de acerto, tempo médio por partida e autoavaliação média.
- Comparação textual entre a autoavaliação média informada pelos participantes e a nota média realmente obtida.
- Gráfico de barras com o percentual de acerto por nível de dificuldade.
- Gráfico de barras com o percentual de acerto por tema (phishing, senhas, malware, engenharia social, entre outros).
- Destaques: questão com maior índice de erro, questão mais acertada, tema com melhor e pior desempenho.

## Identidade visual e temas

A interface segue uma linha minimalista, inspirada na simplicidade de navegação de jogos como o Termo: poucas opções por tela, foco no conteúdo, sem excesso de elementos. A paleta usa cores pastel, com um tema claro de fundo em tom creme e um tema escuro relacionado (não apenas um preto puro), alternáveis por um ícone neutro (sol/lua) na barra superior -- nunca por emojis, que não são usados em nenhum ponto da interface. A barra superior é deliberadamente simples: o ícone de tema à esquerda e a marca (logo mais o nome "Cyber Challenge") à direita, sem nenhum botão de navegação; toda a navegação principal fica concentrada no Menu Inicial.

## Tecnologias utilizadas

- Java 17 com Spring Boot 3 (Web, Data JPA, Validation).
- MySQL 8 como banco de dados relacional.
- Maven como gerenciador de dependências e build.
- HTML5, CSS3 e JavaScript puro no front-end (sem frameworks ou bibliotecas externas), consumindo a API REST.
- Jackson (já incluso via `spring-boot-starter-web`) para carregar o estoque inicial de perguntas a partir de arquivos JSON.

## Arquitetura

Aplicação monolítica Spring Boot, servindo tanto a API REST (`/api/**`) quanto os arquivos estáticos do front-end, em camadas convencionais:

```
Controller  ->  Service  ->  Repository (Spring Data JPA)  ->  MySQL
```

Não há nenhuma dependência de autenticação, sessão ou papel de usuário em qualquer camada -- todas as rotas são públicas, refletindo a decisão de projeto de não ter contas.

## Estrutura de pastas

```
cyberchallenge/
├── pom.xml
├── README.md
├── .env.example
├── .gitignore
├── sql/
│   └── cyberchallenge_dump.sql
├── src/main/
│   ├── java/com/cyberchallenge/
│   │   ├── CyberChallengeApplication.java
│   │   ├── config/          # DataSeeder (estoque de perguntas), WebConfig (CORS)
│   │   ├── controller/      # ParticipanteController, PartidaController, DashboardController
│   │   ├── dto/             # DTOs de entrada/saída da API (inclui dto/dashboard)
│   │   ├── exception/       # GlobalExceptionHandler e exceções de negócio
│   │   ├── model/           # Participante, Partida, Pergunta, Alternativa, Resposta, TipoPergunta
│   │   ├── repository/      # Repositórios Spring Data JPA
│   │   └── service/         # ParticipanteService, PartidaService, DashboardService
│   └── resources/
│       ├── application.properties        # perfil padrão/desenvolvimento
│       ├── application-prod.properties   # perfil de produção
│       ├── seed/                          # Perguntas iniciais (JSON), carregadas pelo DataSeeder
│       └── static/
│           ├── index.html, script.js      # Menu inicial + fluxo completo do jogo
│           ├── tutorial.html
│           ├── ranking.html, ranking.js
│           ├── estatisticas.html, estatisticas.js
│           ├── theme.js                   # Alternância de tema claro/escuro
│           └── style.css
```

## Banco de dados

O schema é gerenciado automaticamente pelo Hibernate (`spring.jpa.hibernate.ddl-auto=update`). A estrutura foi revisada para usar chaves primárias e estrangeiras de forma consistente, evitando duplicar dados: a identidade do participante (`participantes`) fica separada do resultado de cada jogada (`partidas`), relacionadas por chave estrangeira -- nenhuma informação de nickname/idade/autoavaliação é repetida a cada partida.

Tabelas principais:

| Tabela | Descrição |
|---|---|
| `participantes` | Identidade do participante: nickname (único), idade, autoavaliação |
| `perguntas` | Estoque de perguntas: texto, tema, nível, tipo (Verdadeiro/Falso ou múltipla escolha) |
| `alternativas` | Opções de perguntas de múltipla escolha, com chave estrangeira para `perguntas` |
| `partidas` | Uma jogada completa (10 perguntas): nível, pontuação, acertos, erros, tempos, nota final, chave estrangeira para `participantes` |
| `respostas` | Cada resposta individual de uma partida, com chaves estrangeiras para `partidas`, `perguntas` e, quando aplicável, `alternativas` |

Restrições de integridade aplicadas e validadas com uma importação real em MySQL 8.0 durante o desenvolvimento:

- `participantes.nickname` possui restrição `UNIQUE` no banco, além da validação em nível de aplicação -- a regra é garantida em três camadas (interface, backend, banco de dados).
- `partidas.participante_id` é chave estrangeira para `participantes.id`; não é possível excluir um participante com partidas associadas sem antes tratar essas partidas.
- `respostas.partida_id`, `respostas.pergunta_id` e `respostas.alternativa_escolhida_id` são chaves estrangeiras para `partidas`, `perguntas` e `alternativas`, respectivamente.
- Todas as tabelas usam identificadores numéricos auto-incrementais (`id`) como chave primária; nenhum relacionamento usa nickname, texto ou qualquer outro campo textual como chave.

Um dump completo (schema mais as 281 perguntas) está disponível em `sql/cyberchallenge_dump.sql`, permitindo iniciar o banco já populado. Também é possível simplesmente rodar a aplicação: o `DataSeeder` verifica se a tabela `perguntas` está vazia e, se estiver, insere o mesmo conteúdo automaticamente. As tabelas `participantes`, `partidas`, `alternativas` e `respostas` começam sempre vazias e são preenchidas organicamente pelo uso do jogo.

## Principais entidades

```
Participante                       Partida
    id (PK)                            id (PK)
    nickname (unique)                  participante_id (FK -> Participante)
    idade                              nivel
    autoavaliacao                      pontuacao
    data_cadastro                      acertos
    1 --- N                            erros
                                        tempo_total
                                        tempo_medio
                                        nota_final
                                        data_partida

Pergunta                           Alternativa
    id (PK)                            id (PK)
    texto                              pergunta_id (FK -> Pergunta)
    tema                               texto
    nivel                              correta
    tipo (VF | MULTIPLA_ESCOLHA)
    resposta_correta (só para VF)
    explicacao
    ativa

Resposta
    id (PK)
    partida_id (FK -> Partida)
    pergunta_id (FK -> Pergunta)
    alternativa_escolhida_id (FK -> Alternativa, só para múltipla escolha)
    resposta (Verdadeiro/Falso, só para esse tipo)
    correta
    tempo_resposta
    data_resposta
```

## API

Todos os endpoints ficam sob o prefixo `/api` e são públicos -- não há nenhuma rota que exija autenticação.

### Participante

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/participantes` | Cadastra nickname, idade e autoavaliação. Retorna 409 se o nickname já estiver em uso |
| `GET` | `/api/participantes/disponibilidade?nickname=` | Verifica se um nickname está disponível (feedback rápido no front-end) |

### Jogo

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/partidas/iniciar?nivel={1-4}&excluir={ids}` | Retorna 10 perguntas aleatórias do nível informado |
| `POST` | `/api/partidas/finalizar` | Registra o resultado de uma partida (participanteId, nível, respostas) |
| `GET` | `/api/partidas/ranking?nivel={1-4}` | Ranking de um único nível, ordenado por pontuação e tempo médio |

### Estatísticas

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/estatisticas` | Indicadores gerais, desempenho por nível, por tema e destaques |

## Requisitos

- Java JDK 17 ou superior;
- Maven 3.8 ou superior;
- MySQL Server 8.0 ou superior, em execução (local ou em contêiner);
- Um navegador web moderno.

## Instalação

```bash
cd cyberchallenge
mvn clean install
```

Para deixar o banco já populado com as 281 perguntas antes mesmo do primeiro start (opcional -- a aplicação também faz isso sozinha):

```bash
mysql -u root -p < sql/cyberchallenge_dump.sql
```

## Configuração

Nenhuma credencial fica hardcoded no código. Copie `.env.example` para `.env` (ou configure as mesmas variáveis diretamente no ambiente/servidor):

| Variável | Padrão (desenvolvimento) | Descrição |
|---|---|---|
| `SERVER_PORT` | `8080` | Porta HTTP da aplicação |
| `APP_BASE_URL` | `http://localhost:8080` | URL pública, usada na configuração de CORS |
| `SPRING_PROFILES_ACTIVE` | *(vazio)* | Defina como `prod` para o perfil de produção |
| `DB_HOST` | `localhost` | Host do MySQL |
| `DB_PORT` | `3306` | Porta do MySQL |
| `DB_NAME` | `cyberchallenge` | Nome do banco (criado automaticamente se não existir, em desenvolvimento) |
| `DB_USER` | `root` | Usuário do MySQL |
| `DB_PASSWORD` | *(vazio)* | Senha do MySQL |

Em produção (`SPRING_PROFILES_ACTIVE=prod`), não há valores padrão para as variáveis de banco: a aplicação falha ao iniciar caso alguma não esteja definida, em vez de usar um valor previsível.

## Execução

```bash
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

Para gerar um artefato executável:

```bash
mvn clean package
java -jar target/cyber-challenge-1.0.0.jar
```

## Como acessar e utilizar

1. Acesse `http://localhost:8080` no navegador -- o Menu Inicial é exibido diretamente.
2. Clique em "Jogar", informe nickname, idade e autoavaliação, escolha um nível e responda as 10 perguntas.
3. Ao final, veja sua nota e, se quiser, confira sua posição no ranking do nível jogado.
4. "Tutorial" explica a mecânica do jogo com um exemplo interativo; "Ranking" e "Estatísticas Gerais" podem ser acessados a qualquer momento pelo Menu Inicial, sem necessidade de jogar antes.

## Validação e testes realizados

O ambiente usado para esta implementação não tem acesso ao repositório Maven Central, o que impede compilar e rodar o Spring Boot de ponta a ponta nesse ambiente específico. Diante disso, a validação foi feita em duas frentes:

- Revisão manual, linha a linha, de todo o código Java alterado ou criado (tipos, assinaturas, imports, referências cruzadas entre entidades, DTOs, services e controllers).
- Validação real da camada de banco de dados: um servidor MySQL 8 foi instalado no ambiente de desenvolvimento e o schema completo (`sql/cyberchallenge_dump.sql`) foi importado do zero, com testes efetivos de:
  - criação das cinco tabelas sem erros, incluindo todas as chaves estrangeiras;
  - inserção das 281 perguntas e verificação de que cada pergunta de múltipla escolha tem exatamente 4 alternativas e exatamente 1 marcada como correta;
  - restrição `UNIQUE` em `participantes.nickname` rejeitando duplicatas;
  - fluxo completo simulado (cadastro de participante, sorteio de 10 perguntas de um nível, registro de partida e respostas, leitura do ranking);
  - confirmação de que o ranking de um nível nunca retorna participantes de outro nível;
  - a regra de desempate do ranking (pontuação, depois tempo médio) aplicada a um caso de empate real.

Recomenda-se rodar `mvn clean spring-boot:run` no ambiente de destino como primeiro passo, seguido de uma verificação manual do fluxo completo (menu, tutorial, identificação com nickname duplicado e novo, os quatro níveis, perguntas de ambos os formatos, resultado, ranking de cada nível, estatísticas gerais, temas claro e escuro).

## Responsividade

A interface usa unidades relativas, grids flexíveis (`grid-template-columns` com colunas que se reorganizam em telas menores) e pontos de quebra em 768px (tablet) e 560px (celular), cobrindo o menu, a tela de perguntas, botões, cards de nível, tabelas de ranking e os gráficos de estatísticas. Botões e áreas clicáveis mantêm altura mínima de 44px, adequada para toque em telas sensíveis. Campos de formulário usam tamanho de fonte de 16px para evitar zoom automático em iOS. Tabelas (ranking) ficam envolvidas em contêineres com rolagem horizontal própria, para nunca forçar rolagem horizontal da página inteira nem cortar conteúdo.

## Autores

- Alessandro Cecilio
- João Luís Pedrosa
- João Paulo Borges
- João Victor Bonfim
- João Victor Dizaró
- Luan Pereira Barbosa

## Trabalhos futuros

- Migrar de `ddl-auto=update` para uma ferramenta de migration versionada (Flyway ou Liquibase), para um controle de schema mais auditável em produção.
- Adicionar testes automatizados (unitários e de integração) ao projeto, hoje validado manualmente e via testes diretos no banco de dados.
- Considerar paginação no endpoint de ranking caso o volume de partidas por nível cresça substancialmente.
- Avaliar a adição de um mecanismo simples de moderação de nicknames (lista de termos bloqueados), já que qualquer nickname disponível pode ser usado livremente.
- Configurar HTTPS e um domínio real antes de qualquer publicação para o público geral.

## Licença

O repositório original não possuía uma licença definida. Nenhuma foi adicionada nesta revisão por não ter sido solicitado -- a decisão de licenciamento fica a critério da equipe.
