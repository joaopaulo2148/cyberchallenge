# Cyber Challenge

Jogo educativo de conscientização em segurança cibernética, com back-end em Spring Boot, banco de dados MySQL e front-end estático responsivo.

## Sobre o projeto

O Cyber Challenge testa, em poucos minutos, o conhecimento do jogador sobre segurança digital. Grande parte dos incidentes de segurança (phishing, senhas fracas, engenharia social) explora a falta de conhecimento básico do usuário comum, e não apenas falhas técnicas sofisticadas. O objetivo do projeto é oferecer uma forma leve e rápida de testar e reforçar esse conhecimento, com feedback educativo imediato após cada resposta.

## Funcionalidades

- Jogo educativo de cibersegurança;
- Quatro níveis de dificuldade;
- Partidas com 10 perguntas por nível;
- Três tipos de questão (Verdadeiro/Falso, múltipla escolha e completar a frase);
- Cadastro e login simples de participante;
- Ranking separado por nível;
- Estatísticas gerais;
- Tema claro e escuro;
- Interface responsiva.

## Tecnologias

- Java
- Spring Boot
- MySQL
- HTML
- CSS
- JavaScript
- Maven

## Como executar

### Pré-requisitos

- Java 17
- Maven
- MySQL em execução

### Banco de dados

O projeto se conecta ao MySQL usando variáveis de ambiente, sem precisar editar nenhum arquivo. Se o seu MySQL usa a porta padrão (`3306`), nenhuma configuração é necessária. Caso contrário, defina a porta e a senha antes de iniciar a aplicação:

```cmd
set DB_PORT=3308
set DB_PASSWORD=sua_senha
```

- `DB_PORT` define a porta do MySQL;
- `DB_PASSWORD` define a senha do usuário do MySQL;
- `set` mantém a variável somente na sessão atual do CMD (em Linux/macOS, use `export`).

Outras variáveis aceitas: `DB_HOST`, `DB_NAME`, `DB_USER`, `SERVER_PORT`. Veja `.env.example` para a lista completa com os valores padrão.

### Executar

```cmd
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

## Como jogar

Menu → Jogar → Informações do participante → Nível → 10 perguntas → Resultado.

"Tutorial", "Ranking" e "Estatísticas Gerais" podem ser acessados a qualquer momento pelo Menu Inicial.

## Estrutura do projeto

```
cyberchallenge/
├── src/main/java/com/cyberchallenge/   # Código-fonte (controllers, services, entidades, etc.)
├── src/main/resources/static/          # Front-end (HTML, CSS, JS)
├── src/main/resources/seed/            # Perguntas usadas para popular o banco
├── sql/                                 # Dump de referência do banco de dados
└── pom.xml
```

## Equipe

- Alessandro Cecilio
- João Luís Pedrosa
- João Paulo Borges
- João Victor Bonfim
- João Victor Dizaró
- Luan Pereira Barbosa
