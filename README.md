# 🛡️ Cyber Challenge

<p align="center">
  <strong>🎮 Aprenda Cibersegurança de forma interativa, rápida e divertida.</strong>
</p>

<p align="center">
  <a href="#-sobre-o-projeto">Sobre</a> •
  <a href="#-funcionalidades">Funcionalidades</a> •
  <a href="#-tecnologias">Tecnologias</a> •
  <a href="#-como-executar">Instalação</a> •
  <a href="#-como-jogar">Como jogar</a> •
  <a href="#-estrutura-do-projeto">Estrutura</a>
</p>

---

## 🔐 Sobre o projeto

O **Cyber Challenge** é um jogo educativo voltado para a **conscientização em segurança cibernética**.

A proposta é transformar conceitos importantes de segurança digital em uma experiência simples e interativa, permitindo que o jogador teste seus conhecimentos sobre temas presentes no dia a dia, como **phishing, senhas, engenharia social e boas práticas de segurança**.

O projeto foi desenvolvido utilizando uma arquitetura baseada em **Java + Spring Boot**, com persistência de dados em **MySQL** e uma interface web responsiva construída com **HTML, CSS e JavaScript**.

> 🎯 **Objetivo:** aprender sobre segurança cibernética enquanto joga, recebe feedback e acompanha sua evolução.

---

## 🎮 Funcionalidades

### 🧠 Sistema de perguntas

* 4 níveis de dificuldade
* 10 perguntas por nível
* Questões de:

  * ✅ Verdadeiro ou Falso
  * 🔘 Múltipla escolha
  * ✍️ Completar a frase
* Feedback educativo após as respostas

### 👤 Participantes

* Cadastro de participante
* Login
* Registro das partidas
* Armazenamento das pontuações

### 🏆 Ranking

* Ranking separado por nível
* Comparação de pontuações entre participantes
* Sistema de classificação baseado no desempenho

### 📊 Estatísticas

* Estatísticas gerais das partidas
* Informações sobre desempenho dos participantes
* Visualização dos resultados

### 🎨 Interface

* Tema claro ☀️
* Tema escuro 🌙
* Layout responsivo
* Interface pensada para diferentes tamanhos de tela

---

## 🛠️ Tecnologias utilizadas

### Backend

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge\&logo=openjdk\&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?style=for-the-badge\&logo=springboot\&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge\&logo=apachemaven\&logoColor=white)

### Banco de dados

![MySQL](https://img.shields.io/badge/MySQL-Database-4479A1?style=for-the-badge\&logo=mysql\&logoColor=white)

### Frontend

![HTML5](https://img.shields.io/badge/HTML5-Markup-E34F26?style=for-the-badge\&logo=html5\&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-Styling-1572B6?style=for-the-badge\&logo=css3\&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-Logic-F7DF1E?style=for-the-badge\&logo=javascript\&logoColor=black)

### Segurança e persistência

* Spring Data JPA
* Spring Validation
* Spring Security Crypto
* MySQL Connector/J

---

## 🏗️ Arquitetura

O projeto utiliza uma estrutura web baseada em **Spring Boot**, separando as responsabilidades entre backend, frontend e banco de dados.

```text
                         ┌──────────────────────┐
                         │      USUÁRIO         │
                         │   Navegador Web      │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │      FRONTEND        │
                         │ HTML / CSS / JS      │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │      BACKEND         │
                         │    Spring Boot       │
                         │                      │
                         │ Controllers          │
                         │ Services             │
                         │ Entities             │
                         │ Repositories         │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │       MySQL          │
                         │    Banco de Dados    │
                         └──────────────────────┘
```

---

## 🚀 Como executar

### 1️⃣ Pré-requisitos

Antes de executar o projeto, tenha instalado:

* **Java 17**
* **Maven**
* **MySQL**
* **Git**

Verifique as versões:

```bash
java -version
mvn -version
mysql --version
```

---

### 2️⃣ Clone o repositório

```bash
git clone https://github.com/joaopaulo2148/cyberchallenge.git
```

Entre no diretório:

```bash
cd cyberchallenge
```

---

### 3️⃣ Configure o banco de dados

O projeto utiliza **MySQL** para armazenamento dos participantes, perguntas, partidas e resultados.

Por padrão, a aplicação espera:

```text
Host: localhost
Porta: 3306
```

As configurações podem ser definidas por variáveis de ambiente.

Exemplo no Windows (CMD):

```cmd
set DB_PORT=3306
set DB_PASSWORD=sua_senha
```

Outras variáveis disponíveis:

```text
DB_HOST
DB_PORT
DB_NAME
DB_USER
DB_PASSWORD
SERVER_PORT
```

Consulte o arquivo `.env.example` para visualizar os valores utilizados como referência.

---

### 4️⃣ Execute o projeto

Utilizando Maven:

```bash
mvn spring-boot:run
```

Ou, caso prefira gerar o `.jar`:

```bash
mvn clean package
```

Depois:

```bash
java -jar target/cyber-challenge-1.0.0.jar
```

---

### 5️⃣ Acesse a aplicação

Após iniciar o servidor:

```text
http://localhost:8080
```

---

## 🎯 Como jogar

O fluxo principal do jogo é simples:

```text
🏠 Menu
   ↓
🎮 Jogar
   ↓
👤 Informações do participante
   ↓
📈 Escolha do nível
   ↓
❓ 10 perguntas
   ↓
📝 Respostas
   ↓
🏆 Resultado final
```

Além do modo de jogo, o menu inicial disponibiliza:

* 📚 Tutorial
* 🏆 Ranking
* 📊 Estatísticas Gerais

---

## 📁 Estrutura do projeto

```text
cyberchallenge/
│
├── 📂 src/
│   └── 📂 main/
│       ├── 📂 java/
│       │   └── 📂 com/cyberchallenge/
│       │       ├── Controllers
│       │       ├── Services
│       │       ├── Entities
│       │       ├── Repositories
│       │       └── ...
│       │
│       └── 📂 resources/
│           ├── 📂 static/
│           │   ├── HTML
│           │   ├── CSS
│           │   └── JavaScript
│           │
│           └── 📂 seed/
│               └── Perguntas
│
├── 📂 sql/
│   └── Banco de dados
│
├── 📄 .env.example
├── 📄 Dockerfile
├── 📄 pom.xml
└── 📄 README.md
```

---

## 🧩 Conceitos trabalhados

O projeto foi desenvolvido para unir **programação, banco de dados, desenvolvimento web e segurança cibernética** em uma única aplicação.

### 💻 Desenvolvimento

* Java
* Programação orientada a objetos
* Spring Boot
* API/Controllers
* Services
* Repositories
* Validação
* Maven

### 🗄️ Banco de dados

* MySQL
* JPA / Hibernate
* Persistência de dados
* Relacionamento entre entidades

### 🌐 Web

* HTML
* CSS
* JavaScript
* Design responsivo
* Interface clara e intuitiva

### 🔒 Segurança

* Conscientização sobre segurança digital
* Phishing
* Engenharia social
* Senhas seguras
* Boas práticas de proteção de dados

---

## 📈 Possíveis evoluções

O projeto pode futuramente receber novas funcionalidades, como:

* 👥 Sistema de usuários mais completo
* 🏅 Conquistas e medalhas
* 🔥 Sistema de níveis e experiência
* ⏱️ Desafio com tempo por pergunta
* 📊 Dashboard administrativo
* 📚 Mais categorias de perguntas
* 🧠 Banco de questões maior e dinâmico
* 🎯 Histórico detalhado das partidas
* 🐳 Melhorias no ambiente Docker
* ☁️ Deploy automatizado

---

## 👨‍💻 Equipe

Projeto desenvolvido por:

| Integrante           |
| -------------------- |
| Alessandro Cecilio   |
| João Luís Pedrosa    |
| João Paulo Pimenta   |
| João Victor Bonfim   |
| João Victor Dizaró   |
| Luan Pereira Barbosa |

---

## 🎓 Contexto acadêmico

O **Cyber Challenge** foi desenvolvido como projeto acadêmico com o objetivo de aplicar, de forma prática, conhecimentos de:

* Desenvolvimento de Sistemas
* Programação Java
* Engenharia de Software
* Banco de Dados
* Desenvolvimento Web
* Segurança da Informação

A proposta é unir **aprendizado técnico** com **conscientização em cibersegurança**, transformando conceitos teóricos em uma experiência interativa.

---

## 📜 Licença

Este projeto está disponível para fins **acadêmicos e educacionais**.

---

<p align="center">
  🛡️ <strong>Cyber Challenge</strong>
  <br>
  <sub>Aprender segurança também pode ser um jogo.</sub>
</p>
