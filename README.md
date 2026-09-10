# 🛡️ Cyber Challenge

Jogo educativo de conscientização em **Segurança Cibernética**, no formato Verdadeiro/Falso, com back-end em **Spring Boot** e front-end integrado consumindo a API real. Desenvolvido como atividade de extensão para ensinar, de forma leve e interativa, boas práticas de segurança digital.

---

## Sumário

- [Descrição](#descrição)
- [Funcionalidades](#funcionalidades)
- [Tecnologias](#tecnologias)
- [Requisitos](#requisitos)
- [Instalação](#instalação)
- [Execução](#execução)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Banco de dados](#banco-de-dados)
- [API](#api)
- [Como jogar](#como-jogar)
- [Níveis de dificuldade](#níveis-de-dificuldade)
- [Sistema de pontuação](#sistema-de-pontuação)
- [Dashboard administrativo](#dashboard-administrativo)
- [Autores](#autores)
- [Licença](#licença)

---

## Descrição

O Cyber Challenge apresenta ao jogador **10 afirmações aleatórias** sobre segurança cibernética (phishing, senhas, malware, engenharia social, privacidade, entre outros temas), que devem ser classificadas como **Verdadeiro** ou **Falso**. Ao final, o jogador recebe uma nota de desempenho e pode conferir sua posição em um ranking geral, compartilhado entre todos os participantes da atividade.

O projeto também conta com uma área de **dashboard administrativo** para acompanhar, em tempo real, o desempenho dos participantes, as perguntas com mais acertos/erros e o engajamento por tema.

## Funcionalidades

- Menu inicial com acesso ao jogo, ao ranking geral e ao dashboard administrativo.
- Cadastro do participante (nome, idade e autoavaliação de conhecimento em cibersegurança de 1 a 10).
- Escolha do nível de dificuldade antes de cada partida.
- Partidas com exatamente **10 perguntas**, sorteadas aleatoriamente do nível escolhido, com variedade entre partidas seguidas.
- Cronômetro por pergunta e feedback educativo imediato (explicação) após cada resposta.
- Cálculo de uma **nota final de 0 a 10**, além de acertos, erros, tempo total e tempo médio.
- Ranking geral persistente, acessível tanto ao final de uma partida quanto direto pelo menu.
- Estoque de aproximadamente **240 perguntas** (60 por nível), cobrindo phishing, engenharia social, senhas, autenticação multifator, malware, ransomware, redes Wi-Fi, dispositivos móveis, privacidade, vazamento de dados, golpes digitais, e-mail, navegação segura, dados pessoais, segurança corporativa, incidentes de segurança e boas práticas gerais.
- Área administrativa (dashboard + API) para consultar indicadores gerais, estatísticas por pergunta/tema e gerenciar (CRUD) o banco de perguntas.

## Tecnologias

- **Java 17** + **Spring Boot 3** (Web, Data JPA, Validation)
- **MySQL 8** (banco de dados relacional)
- **Maven** (gerenciador de dependências e build)
- **HTML5, CSS3 e JavaScript puro** (front-end estático, sem frameworks) consumindo a API REST
- **Jackson** (já incluso via `spring-boot-starter-web`) para carregar o estoque inicial de perguntas a partir de um arquivo JSON

## Requisitos

- **Java JDK 17** ou superior
- **Maven 3.8+** (ou utilize o Maven do seu ambiente/IDE)
- **MySQL Server 8.0+** instalado e em execução (local ou em um contêiner)
- Um navegador web moderno (Chrome, Firefox, Edge etc.)

## Instalação

```bash
# 1. Extraia o projeto e entre na pasta
cd cyberchallenge

# 2. Baixe as dependências e compile
mvn clean install
```

> Se preferir, use o Maven já configurado na sua IDE (IntelliJ, Eclipse, VS Code) para importar o projeto como um projeto Maven existente.

### Configurando o MySQL

Por padrão, a aplicação se conecta em `localhost:3306`, banco `cyberchallenge`, usuário `root`, sem senha. Não é necessário criar o banco manualmente: o parâmetro `createDatabaseIfNotExist=true` faz o driver criá-lo automaticamente na primeira conexão (o usuário informado precisa ter privilégio para criar bancos).

Se sua instalação do MySQL usa outro host, porta, usuário ou senha, configure por variáveis de ambiente antes de rodar a aplicação (sem precisar editar nenhum arquivo):

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=cyberchallenge
export DB_USER=root
export DB_PASSWORD=suasenha
```

**Banco já populado (opcional, mas recomendado):** este pacote inclui um dump pronto em `sql/cyberchallenge_dump.sql`, com o schema completo e as 240 perguntas já inseridas. Para deixar o banco pronto antes mesmo de rodar a aplicação:

```bash
mysql -u root -p < sql/cyberchallenge_dump.sql
```

Isso já foi testado e validado durante o desenvolvimento (importação sem erros, 240 perguntas conferidas, incluindo acentuação, e as consultas de sorteio de perguntas por nível rodando corretamente contra o banco importado).

> Se você pular esse passo, não tem problema: a aplicação também popula o banco sozinha, automaticamente, na primeira execução (o `DataSeeder` verifica se a tabela `perguntas` está vazia e, se estiver, insere as 240 perguntas a partir de `src/main/resources/seed/perguntas-seed.json`). O dump SQL é só um atalho para quem quer o banco pronto sem depender do primeiro start da aplicação.

## Execução

```bash
mvn spring-boot:run
```

A aplicação sobe em:

```
http://localhost:8080
```

Ao abrir essa URL no navegador, o jogo é exibido diretamente (menu inicial).

Para gerar um `.jar` executável (por exemplo, para rodar sem o Maven instalado):

```bash
mvn clean package
java -jar target/cyber-challenge-1.0.0.jar
```

## Estrutura do projeto

```text
cyberchallenge/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       ├── java/com/cyberchallenge/
│       │   ├── CyberChallengeApplication.java
│       │   ├── config/          # CORS, redirecionamentos e carga inicial de perguntas (DataSeeder)
│       │   ├── controller/      # Endpoints REST (jogo, dashboard e área administrativa)
│       │   ├── dto/             # Objetos de entrada/saída da API (inclui dto/admin e dto/dashboard)
│       │   ├── exception/       # Tratamento central de erros (GlobalExceptionHandler)
│       │   ├── model/           # Entidades JPA: Pergunta, Participante, Resposta
│       │   ├── repository/      # Repositórios Spring Data JPA
│       │   └── service/         # Regras de negócio (partida, dashboard, CRUD administrativo)
│       └── resources/
│           ├── application.properties
│           ├── seed/
│           │   └── perguntas-seed.json   # Estoque de ~240 perguntas usado pelo DataSeeder (auto-seed)
│           └── static/
│               ├── index.html   # Front-end do jogo (menu, cadastro, níveis, partida, resultado, ranking)
│               ├── script.js
│               ├── style.css
│               └── admin/       # Dashboard administrativo (HTML/CSS/JS)
└── sql/
    └── cyberchallenge_dump.sql  # Dump MySQL opcional: schema + as 240 perguntas já inseridas
```

## Banco de dados

O projeto usa **MySQL** como banco de dados relacional. A conexão é configurada em `src/main/resources/application.properties`, com valores padrão (`localhost:3306`, banco `cyberchallenge`, usuário `root`, sem senha) que podem ser sobrescritos por variáveis de ambiente (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`) sem editar nenhum arquivo — veja a seção [Configurando o MySQL](#configurando-o-mysql).

O schema é gerenciado automaticamente pelo Hibernate (`spring.jpa.hibernate.ddl-auto=update`): as tabelas `perguntas`, `participantes` e `respostas` são criadas/atualizadas automaticamente, seja num banco totalmente vazio, seja num banco já criado a partir do dump SQL incluso.

Duas formas de começar com o banco populado:

1. **Automática (padrão):** basta rodar a aplicação. O `DataSeeder` verifica se a tabela `perguntas` está vazia e, se estiver, insere as 240 perguntas a partir do JSON de seed.
2. **Via dump SQL (mais rápido, opcional):** importe `sql/cyberchallenge_dump.sql` antes do primeiro `mvn spring-boot:run` — o banco já sobe com as 240 perguntas prontas, sem depender do `DataSeeder`. Esse dump foi gerado a partir do mesmo JSON de seed e **testado com uma importação real em MySQL 8** durante o desenvolvimento deste projeto (schema criado corretamente, 240 linhas inseridas, acentuação preservada, e as consultas de sorteio de perguntas por nível validadas contra os dados importados).

Os dois caminhos são compatíveis entre si: se você importar o dump primeiro, o `DataSeeder` detecta que a tabela já tem dados e não insere nada de novo (sem duplicar perguntas).

## API

Todos os endpoints ficam sob o prefixo `/api`.

### Jogo (público)

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/partidas/iniciar?nivel={1-4}&excluir={id1,id2,...}` | Retorna 10 perguntas aleatórias e ativas do nível informado. `excluir` é opcional: lista de IDs de perguntas já usadas recentemente, para dar mais variedade. |
| `POST` | `/api/partidas/finalizar` | Recebe nome, idade, autoavaliação, nível e as 10 respostas da partida; calcula pontuação, acertos e nota final; salva o participante. |
| `GET` | `/api/partidas/ranking` | Retorna o ranking geral, ordenado por pontuação (desempate por tempo). |

### Dashboard (leitura)

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/dashboard` | Indicadores gerais, estatísticas por pergunta e por tema. |

### Administração (CRUD de perguntas e consulta de participantes)

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/admin/perguntas` | Lista todas as perguntas (todos os níveis, ativas e inativas). |
| `GET` | `/api/admin/perguntas/{id}` | Detalhe de uma pergunta. |
| `POST` | `/api/admin/perguntas` | Cria uma nova pergunta (exige `texto`, `tema`, `respostaCorreta`, `explicacao`, `nivel`). |
| `PUT` | `/api/admin/perguntas/{id}` | Atualiza uma pergunta existente. |
| `PATCH` | `/api/admin/perguntas/{id}/status` | Ativa/desativa uma pergunta (`{ "ativa": true/false }`). |
| `DELETE` | `/api/admin/perguntas/{id}` | Remove uma pergunta. |
| `GET` | `/api/admin/participantes` | Lista todos os participantes já registrados. |
| `GET` | `/api/admin/participantes/{id}` | Detalhe de um participante. |
| `GET` | `/api/admin/participantes/{id}/respostas` | Respostas detalhadas de um participante. |

> A área administrativa não possui autenticação própria neste projeto (é voltada para uso em atividade presencial e controlada). Para uso fora desse contexto, recomenda-se adicionar Spring Security antes de expor a aplicação publicamente.

## Como jogar

```text
MENU INICIAL
    ↓
INICIAR DESAFIO
    ↓
CADASTRO (nome, idade, autoavaliação de 1 a 10)
    ↓
ESCOLHA DO NÍVEL (Leigo, Usuário, Intermediário, Especialista)
    ↓
PARTIDA (10 perguntas Verdadeiro/Falso, com cronômetro e explicação após cada resposta)
    ↓
RESULTADO (nota final, pontuação, acertos, erros, tempo, ranking)
    ↓
RANKING / JOGAR NOVAMENTE / VOLTAR AO MENU
```

O ranking geral também pode ser consultado a qualquer momento direto do menu inicial, sem precisar jogar uma partida.

## Níveis de dificuldade

| Nível | Nome | Público-alvo |
|---|---|---|
| 1 | **Leigo** | Pessoas com pouco ou nenhum conhecimento sobre segurança cibernética |
| 2 | **Usuário** | Situações comuns enfrentadas por usuários no dia a dia |
| 3 | **Intermediário** | Exige conhecimento maior sobre segurança digital |
| 4 | **Especialista** | Perguntas mais avançadas sobre segurança cibernética |

Cada nível possui um estoque próprio de aproximadamente 60 perguntas ativas. Uma partida sempre sorteia 10 delas, pertencentes exclusivamente ao nível escolhido. Ao jogar novamente, o sistema tenta evitar repetir as mesmas 10 perguntas da partida anterior daquele nível (com fallback automático caso o estoque restante seja insuficiente).

## Sistema de pontuação

Ao final de cada partida, o sistema calcula:

- **Acertos** e **erros** (de 0 a 10);
- **Pontuação bruta**: 2 pontos por acerto (0 a 20 pontos);
- **Tempo total** e **tempo médio por pergunta**;
- **Nota final, numa escala de 0 a 10** — a métrica de destaque na tela de resultado.

A regra da nota final é **determinística e documentada no código** (`PartidaService.calcularNotaFinal`):

```text
nota_final = round( (acertos / total_de_perguntas) * 10 , 1 casa decimal )
```

Exemplos (partida com 10 perguntas): 10 acertos → nota 10.0; 7 acertos → nota 7.0; 0 acertos → nota 0.0.

## Dashboard administrativo

Disponível em `http://localhost:8080/admin/`, mostra:

- Indicadores gerais: total de participantes, total de partidas, pontuação e acertos médios, taxa geral de acerto e tempo médio por partida;
- Estatísticas por pergunta: percentual de acerto/erro, distribuição de respostas (Verdadeiro x Falso) e tempo médio de resposta;
- Estatísticas por tema: percentual de acerto agregado por assunto (phishing, senhas, malware etc.);
- Pergunta mais acertada, mais errada, com maior e menor tempo médio de resposta;
- Tema com maior e menor percentual de acerto.

## Autores

- Alessandro Cecilio
- João Luís Pedrosa
- João Paulo Borges
- João Victor Bonfim
- João Victor Dizaró
- Luan Pereira Barbosa

## Licença

Este projeto não possuía uma licença definida no repositório original. Por se tratar de material educativo produzido em atividade de extensão universitária, sem indicação prévia de licença pelos autores, nenhuma licença foi adicionada nesta entrega — a definição de licenciamento (ex: MIT, caso os autores optem por tornar o uso e a redistribuição explicitamente livres) fica a critério da equipe.
