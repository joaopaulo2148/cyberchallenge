# Cyber Challenge

Jogo educativo de conscientização em segurança cibernética, com back-end em Spring Boot, banco de dados MySQL e front-end estático responsivo. O jogo possui um sistema de contas simples (nickname único, idade, autoavaliação e senha), sem complexidade de autenticação corporativa.

---

## Sumário

- [Descrição](#descrição)
- [Objetivo e problema abordado](#objetivo-e-problema-abordado)
- [Público-alvo](#público-alvo)
- [Funcionalidades](#funcionalidades)
- [Como funciona: fluxo do jogo](#como-funciona-fluxo-do-jogo)
- [Sistema de contas e autenticação](#sistema-de-contas-e-autenticação)
- [Requisitos e segurança da senha](#requisitos-e-segurança-da-senha)
- [Níveis de dificuldade](#níveis-de-dificuldade)
- [Sistema de perguntas](#sistema-de-perguntas)
- [Sistema de pontuação](#sistema-de-pontuação)
- [Ranking por nível](#ranking-por-nível)
- [Estatísticas gerais](#estatísticas-gerais)
- [Identidade visual, temas e barra superior](#identidade-visual-temas-e-barra-superior)
- [Tecnologias utilizadas](#tecnologias-utilizadas)
- [Arquitetura](#arquitetura)
- [Estrutura de pastas](#estrutura-de-pastas)
- [Banco de dados](#banco-de-dados)
- [Principais entidades](#principais-entidades)
- [API](#api)
- [Requisitos](#requisitos)
- [Instalação](#instalação)
- [Configuração do MySQL e variáveis de ambiente](#configuração-do-mysql-e-variáveis-de-ambiente)
- [Execução](#execução)
- [Como acessar e utilizar](#como-acessar-e-utilizar)
- [Validação e testes realizados](#validação-e-testes-realizados)
- [Responsividade](#responsividade)
- [Autores](#autores)
- [Trabalhos futuros](#trabalhos-futuros)
- [Licença](#licença)

---

## Descrição

O Cyber Challenge apresenta 10 perguntas sobre segurança cibernética por partida, em três formatos (Verdadeiro/Falso, múltipla escolha e completar a frase), sorteadas de um banco de 401 questões distribuídas em quatro níveis de dificuldade. Para jogar, o participante cria uma conta simples (nickname, idade, autoavaliação e senha) e, das próximas vezes, apenas entra com nickname e senha. Ao final de cada partida, o participante recebe uma nota de 0 a 10 e pode consultar sua posição no ranking do nível jogado. Uma página pública de estatísticas gerais reúne indicadores agregados de todas as partidas já realizadas.

## Objetivo e problema abordado

Grande parte dos incidentes de segurança digital (phishing, senhas fracas, golpes de engenharia social) explora a falta de conhecimento básico do usuário comum, e não apenas falhas técnicas sofisticadas. O Cyber Challenge propõe uma forma leve e rápida de testar e reforçar esse conhecimento, com feedback educativo imediato após cada resposta, e com um cadastro simples o suficiente para não ser uma barreira de entrada.

## Público-alvo

Estudantes, participantes de atividades de extensão e oficinas de conscientização em segurança da informação, e qualquer pessoa interessada em avaliar seu próprio conhecimento sobre boas práticas de segurança digital.

## Funcionalidades

- Menu inicial simples, com quatro opções: Jogar, Tutorial, Ranking e Estatísticas Gerais.
- Conta de participante: cadastro (nickname único, idade, autoavaliação de 1 a 10 e senha forte) e login (nickname e senha).
- Escolha entre quatro níveis de dificuldade antes de cada partida.
- Partidas com exatamente 10 perguntas sorteadas aleatoriamente do nível escolhido, com variedade entre partidas seguidas.
- Perguntas em três formatos: Verdadeiro/Falso, múltipla escolha e completar a frase.
- Feedback educativo imediato após cada resposta (correta/incorreta + explicação).
- Navegação entre perguntas durante a partida, incluindo o botão "Pergunta anterior".
- Tela de resultado com nota final (0 a 10), acertos, erros, percentual de aproveitamento e tempo médio de resposta.
- Ranking separado por nível de dificuldade.
- Página pública de Estatísticas Gerais, com indicadores agregados, desempenho por nível e por tema, e comparação entre autoavaliação e desempenho real.
- Tema claro (fundo em tom creme) e tema escuro, alternáveis por um ícone na barra superior, com a preferência lembrada no navegador.
- Barra superior minimalista, que se oculta suavemente ao rolar a página para baixo e reaparece ao rolar para cima.
- Interface responsiva para desktop, notebook, tablet e celular.

## Como funciona: fluxo do jogo

### Primeiro acesso

```
Menu Inicial -> Jogar -> Cadastrar-se -> Conta criada -> Escolha do nível -> Partida -> Resultado -> Voltar ao Menu
```

### Já com conta

```
Menu Inicial -> Jogar -> Entrar -> Escolha do nível -> Partida -> Resultado -> Voltar ao Menu
```

Se o participante já estiver com a sessão do navegador autenticada, o clique em "Jogar" pula direto para a escolha de nível, sem pedir os dados novamente. "Tutorial", "Ranking" e "Estatísticas Gerais" podem ser acessados a qualquer momento pelo Menu Inicial, com ou sem conta.

## Sistema de contas e autenticação

O sistema de contas foi projetado para ser propositalmente simples, sem a complexidade de um mecanismo corporativo de autenticação:

- **Cadastro:** nickname (único), idade, autoavaliação de conhecimento (1 a 10) e senha.
- **Login:** nickname e senha.
- **Senha:** nunca armazenada em texto puro -- apenas o hash BCrypt é gravado no banco (`participantes.senha_hash`). Nenhum endpoint da API jamais retorna a senha ou seu hash.
- **Mensagem de erro de login:** deliberadamente genérica ("Nickname ou senha incorretos."), sem indicar se o problema foi o nickname ou a senha.
- **Sessão:** após um cadastro ou login bem-sucedido, o backend confirma as credenciais e devolve os dados públicos da conta (id, nickname, idade, autoavaliação). O front-end guarda esse resultado em `sessionStorage` do navegador, válido apenas enquanto a aba estiver aberta.

**Limitação assumida conscientemente:** este mecanismo não usa cookies de sessão, tokens renováveis ou qualquer forma de revogação no servidor -- a "sessão" existe inteiramente no navegador, na aba atual. Para o escopo de um jogo educativo, sem dados sensíveis além do progresso no próprio jogo, essa simplicidade é uma escolha deliberada e proporcional ao pedido explícito de "não um sistema complexo de autenticação". Não seria adequado para uma aplicação com dados sensíveis reais, que exigiria um mecanismo de sessão de verdade (cookies HttpOnly, tokens com expiração e revogação).

## Requisitos e segurança da senha

A senha deve atender a requisitos inspirados em boas práticas modernas (como as usadas por grandes serviços), sem exigências artificialmente excessivas:

- Mínimo de 8 caracteres;
- Ao menos uma letra maiúscula;
- Ao menos uma letra minúscula;
- Ao menos um número;
- Ao menos um caractere especial;
- Não pode ser uma senha extremamente comum (existe uma lista de bloqueio para casos óbvios, como "12345678" ou "password1").

O formulário de cadastro mostra um checklist visual em tempo real, indicando quais requisitos já foram atendidos enquanto a pessoa digita. As mesmas regras são verificadas de novo no backend (`PasswordValidator`) antes de qualquer conta ser criada -- o front-end nunca é a única camada de validação.

## Níveis de dificuldade

| Nível | Nome | Público-alvo |
|---|---|---|
| 1 | Leigo | Pouco ou nenhum conhecimento sobre segurança cibernética |
| 2 | Básico | Situações comuns do dia a dia, acima do nível Leigo |
| 3 | Intermediário | Conhecimento mais aprofundado sobre segurança digital |
| 4 | Especialista | Questões avançadas de cibersegurança |

## Sistema de perguntas

O banco de perguntas tem 401 questões ativas, com aproximadamente 100 por nível, em três formatos:

- **Verdadeiro/Falso** (240 perguntas, estoque original do projeto, preservado integralmente);
- **Múltipla escolha** (41 perguntas, 4 alternativas cada, exatamente uma correta);
- **Completar a frase** (120 perguntas): o enunciado apresenta uma frase com uma lacuna, e o participante escolhe, entre alternativas, qual palavra ou expressão completa corretamente a frase. Tecnicamente funciona como uma pergunta de múltipla escolha (o participante nunca digita texto livre) -- a diferença é apenas na apresentação visual.

Cada partida sorteia 10 perguntas do nível escolhido, podendo misturar os três formatos. O sistema tenta evitar repetir, na partida seguinte do mesmo nível, as mesmas 10 perguntas usadas na partida anterior; se o estoque restante não for suficiente, o sorteio volta a considerar o conjunto completo do nível.

## Sistema de pontuação

```
nota_final = número de respostas corretas (0 a 10)
```

Cada uma das 10 perguntas vale exatamente 1 ponto, sem pesos diferentes entre elas. A tela de resultado exibe: nota final, acertos, erros, percentual de aproveitamento, tempo médio de resposta e o nível escolhido.

## Ranking por nível

Existem quatro rankings independentes (Leigo, Básico, Intermediário, Especialista), apresentados por abas na página de Ranking. O ranking usa as partidas associadas às contas dos participantes.

Critério de ordenação (documentado também em `PartidaRepository`): maior pontuação e, em caso de empate, menor tempo médio de resposta. (O critério de "maior percentual de acerto" é matematicamente equivalente ao de pontuação nesta implementação, já que toda partida tem sempre 10 perguntas.)

## Estatísticas gerais

Página pública, sem qualquer restrição de acesso, com indicadores gerais (total de partidas, total de participantes, pontuação média, taxa geral de acerto, tempo médio por partida, autoavaliação média), comparação entre autoavaliação e desempenho real, gráficos de barras por nível e por tema, e destaques (questão mais e menos acertada, tema com melhor e pior desempenho).

## Identidade visual, temas e barra superior

A interface segue uma linha minimalista, com cores pastel, tema claro (fundo em tom creme) e tema escuro relacionado (não um preto puro), sem uso de emojis em nenhum ponto -- todos os ícones são SVGs neutros.

A barra superior é deliberadamente simples:

- **Lado esquerdo:** ícone para alternar entre tema claro e escuro.
- **Lado direito:** indicação da conta (nickname e opção de sair, quando autenticado, ou "Cadastrar-se / Entrar", quando não) e a marca (logo mais o nome "Cyber Challenge").

Ela nunca contém botões de Jogar, Tutorial, Ranking ou Estatísticas -- essas opções ficam concentradas no Menu Inicial. Ao rolar a página para baixo, a barra desaparece suavemente para liberar espaço de leitura; ao rolar para cima, ela reaparece. Em páginas curtas, sem rolagem, ela permanece sempre visível.

## Tecnologias utilizadas

- Java 17 com Spring Boot 3 (Web, Data JPA, Validation).
- Spring Security Crypto (`BCryptPasswordEncoder`) para hash de senhas -- usado isoladamente, sem o restante do framework Spring Security, para manter a arquitetura simples.
- MySQL 8 como banco de dados relacional.
- Maven como gerenciador de dependências e build.
- HTML5, CSS3 e JavaScript puro no front-end (sem frameworks ou bibliotecas externas).

## Arquitetura

Aplicação monolítica Spring Boot, servindo tanto a API REST (`/api/**`) quanto os arquivos estáticos do front-end, em camadas convencionais:

```
Controller -> Service -> Repository (Spring Data JPA) -> MySQL
```

Não há filtros de segurança nem sessão gerenciada no servidor -- a autenticação é verificada sob demanda (`POST /api/participantes/login`), e o estado de "logado" é mantido inteiramente no navegador (ver [Sistema de contas e autenticação](#sistema-de-contas-e-autenticação)).

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
│   │   └── service/         # ParticipanteService, PasswordValidator, PartidaService, DashboardService
│   └── resources/
│       ├── application.properties        # perfil padrão/desenvolvimento
│       ├── application-prod.properties   # perfil de produção
│       ├── seed/                          # Perguntas iniciais (JSON), carregadas pelo DataSeeder
│       └── static/
│           ├── index.html, script.js      # Menu inicial + fluxo completo do jogo
│           ├── auth.html, auth.js         # Cadastro e login
│           ├── tutorial.html
│           ├── ranking.html, ranking.js
│           ├── estatisticas.html, estatisticas.js
│           ├── theme.js                   # Tema claro/escuro + comportamento da barra ao rolar
│           ├── conta.js                   # Estado da conta (sessionStorage) + controle na barra superior
│           └── style.css
```

## Banco de dados

O schema é gerenciado automaticamente pelo Hibernate (`spring.jpa.hibernate.ddl-auto=update`). A conta do participante (`participantes`) fica separada do resultado de cada jogada (`partidas`), relacionadas por chave estrangeira.

| Tabela | Descrição |
|---|---|
| `participantes` | Conta: nickname (único), idade, autoavaliação, hash da senha |
| `perguntas` | Estoque de perguntas: texto, tema, nível, tipo (Verdadeiro/Falso, múltipla escolha ou completar a frase) |
| `alternativas` | Opções de perguntas de múltipla escolha/completar a frase, com chave estrangeira para `perguntas` |
| `partidas` | Uma jogada completa (10 perguntas): nível, pontuação, acertos, erros, tempos, nota final, chave estrangeira para `participantes` |
| `respostas` | Cada resposta individual, com chaves estrangeiras para `partidas`, `perguntas` e, quando aplicável, `alternativas` |

Restrições de integridade aplicadas e validadas com uma importação real em MySQL 8.0 durante o desenvolvimento:

- `participantes.nickname` possui restrição `UNIQUE` no banco, além da validação em nível de aplicação e de interface -- a regra é garantida em três camadas.
- `partidas.participante_id` é chave estrangeira para `participantes.id`.
- `respostas.partida_id`, `respostas.pergunta_id` e `respostas.alternativa_escolhida_id` são chaves estrangeiras para `partidas`, `perguntas` e `alternativas`.
- Todas as tabelas usam identificadores numéricos auto-incrementais (`id`) como chave primária; nenhum relacionamento usa nickname ou qualquer campo textual como chave.

Um dump completo (schema mais as 401 perguntas) está disponível em `sql/cyberchallenge_dump.sql`. Ele nunca contém nenhuma conta de participante pronta (isso exigiria gravar um hash de senha real em um arquivo versionado); as tabelas `participantes`, `partidas`, `alternativas` (referentes a contas/jogos) e `respostas` começam sempre vazias.

## Principais entidades

```
Participante                       Partida
    id (PK)                            id (PK)
    nickname (unique)                  participante_id (FK -> Participante)
    idade                              nivel
    autoavaliacao                      pontuacao
    senha_hash                         acertos
    data_cadastro                      erros
    1 --- N                            tempo_total
                                        tempo_medio
                                        nota_final
                                        data_partida

Pergunta                           Alternativa
    id (PK)                            id (PK)
    texto                              pergunta_id (FK -> Pergunta)
    tema                               texto
    nivel                              correta
    tipo (VF | MULTIPLA_ESCOLHA | COMPLETAR_FRASE)
    resposta_correta (só para VF)
    explicacao
    ativa

Resposta
    id (PK)
    partida_id (FK -> Partida)
    pergunta_id (FK -> Pergunta)
    alternativa_escolhida_id (FK -> Alternativa, para múltipla escolha/completar a frase)
    resposta (Verdadeiro/Falso, só para esse tipo)
    correta
    tempo_resposta
    data_resposta
```

## API

Todos os endpoints ficam sob o prefixo `/api`.

### Participante e autenticação

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/participantes` | Cria a conta (nickname, idade, autoavaliação, senha). Retorna 409 se o nickname já estiver em uso |
| `POST` | `/api/participantes/login` | Autentica com nickname e senha. Retorna 401 com mensagem genérica em caso de erro |
| `GET` | `/api/participantes/disponibilidade?nickname=` | Verifica se um nickname está disponível (feedback rápido no front-end) |

### Jogo

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/partidas/iniciar?nivel={1-4}&excluir={ids}` | Retorna 10 perguntas aleatórias do nível informado |
| `POST` | `/api/partidas/finalizar` | Registra o resultado de uma partida (participanteId, nível, respostas) |
| `GET` | `/api/partidas/ranking?nivel={1-4}` | Ranking de um único nível |

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

Para deixar o banco já populado com as 401 perguntas antes mesmo do primeiro start (opcional -- a aplicação também faz isso sozinha):

```bash
mysql -u root -p < sql/cyberchallenge_dump.sql
```

## Configuração do MySQL e variáveis de ambiente

A conexão com o MySQL é definida em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:cyberchallenge}?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:}
```

A sintaxe `${DB_PORT:3306}` significa: "use o valor da variável de ambiente `DB_PORT`; se ela não existir, use `3306` como padrão". O mesmo vale para `DB_HOST` (padrão `localhost`), `DB_NAME` (padrão `cyberchallenge`) e `DB_USER` (padrão `root`). Já `${DB_PASSWORD:}` tem um padrão vazio -- se você não definir `DB_PASSWORD`, a aplicação tentará conectar sem senha.

### Descobrindo a porta do seu MySQL

A porta padrão do MySQL é `3306`. Se você instalou o MySQL com as opções padrão, não precisa configurar nada -- o valor padrão do projeto já é `3306`. Porém, algumas instalações (principalmente quando há mais de um MySQL na mesma máquina, ou em versões do MySQL Workbench/XAMPP configuradas de forma diferente) usam outra porta, como `3307` ou `3308`. Para descobrir qual porta o seu MySQL está usando, você pode:

- Verificar no MySQL Workbench, na tela de conexão (o campo "Port");
- Ou, com o servidor rodando, executar `SHOW VARIABLES LIKE 'port';` em um cliente MySQL conectado.

### Configurando porta e senha por variável de ambiente (recomendado)

Em vez de editar `application.properties` e arriscar deixar uma senha real gravada no código (e, por descuido, enviá-la para o GitHub), defina as variáveis de ambiente antes de rodar a aplicação.

**Windows (CMD):**

```cmd
set DB_PORT=3308
set DB_PASSWORD=sua_senha
mvn spring-boot:run
```

O que cada linha faz:

- `set DB_PORT=3308` define a porta que o projeto vai usar para conectar ao MySQL (troque `3308` pela porta real do seu MySQL; se for a padrão, use `set DB_PORT=3306` ou simplesmente não defina essa variável).
- `set DB_PASSWORD=sua_senha` define a senha do usuário do MySQL (troque `sua_senha` pela senha real configurada no seu MySQL).
- `mvn spring-boot:run` inicia a aplicação, que lê essas variáveis automaticamente através do `application.properties`.

O `set` do CMD só vale **para aquela janela do terminal**: ao fechar o CMD, as variáveis deixam de existir, e da próxima vez será necessário defini-las de novo (ou usar o `setx`, abaixo).

**Tornando a configuração permanente (`setx`):**

```cmd
setx DB_PORT "3308"
setx DB_PASSWORD "sua_senha"
```

Diferente do `set`, o `setx` grava a variável de forma permanente no perfil do usuário do Windows. Depois de usar `setx`, é necessário **fechar e abrir novamente o CMD** (ou o terminal/IDE) para que a variável fique disponível -- a janela em que o `setx` foi executado não enxerga a variável imediatamente.

**Linux/macOS (equivalente):**

```bash
export DB_PORT=3308
export DB_PASSWORD=sua_senha
mvn spring-boot:run
```

### Duas formas possíveis de configurar a senha -- e por que uma é preferível

1. **Direto no `application.properties`:** tecnicamente possível (bastaria trocar `${DB_PASSWORD:}` por um valor fixo), mas isso grava a senha real do banco em um arquivo que normalmente é versionado no Git -- um risco de segurança caso o repositório seja compartilhado ou publicado (mesmo que "privado" hoje).
2. **Via variável de ambiente `DB_PASSWORD` (recomendado):** a senha nunca aparece em nenhum arquivo do projeto. É a abordagem já usada por este projeto e a preferida para desenvolvimento compartilhado em equipe e para evitar vazar credenciais reais no GitHub.

O arquivo `.env.example`, na raiz do projeto, documenta todas as variáveis de ambiente aceitas (sem nenhum valor real). Copie-o para `.env` como referência pessoal, ou defina as mesmas variáveis diretamente no seu terminal/IDE/servidor -- o projeto não lê automaticamente um arquivo `.env` (isso exigiria uma biblioteca adicional), então em desenvolvimento local o caminho mais direto é `set`/`export` como mostrado acima.

### Demais variáveis de ambiente

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

Em produção (`SPRING_PROFILES_ACTIVE=prod`, veja `application-prod.properties`), não há valores padrão para as variáveis de banco: a aplicação falha ao iniciar caso alguma não esteja definida.

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

1. Acesse `http://localhost:8080` -- o Menu Inicial é exibido diretamente.
2. Clique em "Jogar". Se ainda não tiver conta, escolha "Cadastrar-se" (nickname, idade, autoavaliação e senha); se já tiver, escolha "Entrar".
3. Escolha um nível e responda as 10 perguntas.
4. Veja seu resultado e, se quiser, confira sua posição no ranking do nível jogado.
5. "Tutorial", "Ranking" e "Estatísticas Gerais" podem ser acessados a qualquer momento pelo Menu Inicial.

## Validação e testes realizados

O ambiente usado para esta implementação não tem acesso ao repositório Maven Central, o que impede compilar e rodar o Spring Boot de ponta a ponta nesse ambiente específico. A validação foi feita em duas frentes:

- Revisão manual, linha a linha, de todo o código Java alterado ou criado.
- Validação real da camada de banco de dados: um servidor MySQL 8 foi usado para importar o schema completo do zero (`sql/cyberchallenge_dump.sql`) e testar, com dados reais:
  - criação das cinco tabelas sem erros, incluindo a nova coluna `senha_hash` e todas as chaves estrangeiras;
  - inserção das 401 perguntas (240 Verdadeiro/Falso + 41 múltipla escolha + 120 completar a frase), com verificação de que toda pergunta de múltipla escolha/completar a frase tem exatamente 4 alternativas e exatamente 1 correta, e de que a distribuição por nível fica próxima de 100 cada;
  - restrição `UNIQUE` em `participantes.nickname` rejeitando duplicatas;
  - formato e verificação de hash BCrypt (tamanho compatível com a coluna `VARCHAR(255)`, verificação de senha correta e incorreta);
  - fluxo completo simulado (criação de conta com senha, partida vinculada à conta, leitura do ranking por nível).

Recomenda-se rodar `mvn clean spring-boot:run` no ambiente de destino como primeiro passo, seguido de uma verificação manual do fluxo completo (cadastro, login, logout, escolha de nível, os três formatos de pergunta, resultado, ranking de cada nível, estatísticas gerais, temas claro e escuro, e o comportamento da barra superior ao rolar a página).

## Responsividade

A interface usa unidades relativas, grids flexíveis e pontos de quebra em 768px (tablet) e 560px (celular). Botões e áreas clicáveis mantêm altura mínima de 44px, adequada para toque. Campos de formulário (incluindo cadastro e login) usam tamanho de fonte de 16px para evitar zoom automático em iOS. Tabelas (ranking) ficam envolvidas em contêineres com rolagem horizontal própria.

## Autores

- Alessandro Cecilio
- João Luís Pedrosa
- João Paulo Borges
- João Victor Bonfim
- João Victor Dizaró
- Luan Pereira Barbosa

## Trabalhos futuros

- Migrar de `ddl-auto=update` para uma ferramenta de migration versionada (Flyway ou Liquibase).
- Adicionar testes automatizados (unitários e de integração).
- Evoluir o mecanismo de sessão atual (baseado em `sessionStorage`) para um mecanismo de sessão de verdade no servidor, caso o projeto passe a lidar com dados mais sensíveis.
- Exibir uma página de histórico individual de partidas para o participante autenticado (a estrutura do banco, com `partidas.participante_id`, já permite essa evolução sem alterações de schema).
- Configurar HTTPS e um domínio real antes de qualquer publicação para o público geral.

## Licença

O repositório original não possuía uma licença definida. Nenhuma foi adicionada nesta revisão por não ter sido solicitado -- a decisão de licenciamento fica a critério da equipe.
