document.addEventListener('DOMContentLoaded', () => {
    // --- CONFIGURAÇÃO DA API ---
    // Caminho relativo: funciona tanto quando o front é servido pelo próprio
    // Spring Boot (src/main/resources/static) quanto quando aberto via um
    // proxy no mesmo domínio. Se for hospedar o front em outro domínio,
    // troque por uma URL absoluta (ex: 'https://meu-backend:8080/api').
    const API_BASE_URL = '/api';

    // --- ELEMENTOS DAS TELAS ---
    const telaInicial = document.getElementById('tela-inicial');
    const telaCadastro = document.getElementById('tela-cadastro');
    const telaPartida = document.getElementById('tela-partida');
    const telaResultado = document.getElementById('tela-resultado');

    // --- ELEMENTOS CADASTRO ---
    const btnIniciar = document.getElementById('btn-iniciar');
    const btnContinuar = document.getElementById('btn-continuar');
    const inputNome = document.getElementById('input-nome');
    const erroNome = document.getElementById('erro-nome');
    const erroApi = document.getElementById('erro-api');

    // --- ELEMENTOS PARTIDA ---
    const contadorPergunta = document.getElementById('contador-pergunta');
    const cronometroDisplay = document.getElementById('cronometro');
    const textoPergunta = document.getElementById('texto-pergunta');
    const btnVerdadeiro = document.getElementById('btn-verdadeiro');
    const btnFalso = document.getElementById('btn-falso');
    const feedbackBox = document.getElementById('feedback-box');
    const feedbackTitulo = document.getElementById('feedback-titulo');
    const feedbackExplicacao = document.getElementById('feedback-explicacao');
    const btnAvancar = document.getElementById('btn-avancar');

    // --- ELEMENTOS RESULTADO & RANKING ---
    const resNome = document.getElementById('res-nome');
    const resPontuacao = document.getElementById('res-pontuacao');
    const resAcertos = document.getElementById('res-acertos');
    const resErros = document.getElementById('res-erros');
    const resTempo = document.getElementById('res-tempo');
    const resMedia = document.getElementById('res-media');
    const resMensagem = document.getElementById('res-mensagem');
    const resMensagemBox = document.getElementById('res-mensagem-box');
    const erroEnvio = document.getElementById('erro-envio');
    const rankingList = document.getElementById('ranking-list');
    const btnNovoJogador = document.getElementById('btn-novo-jogador');

    // --- VARIÁVEIS DE JOGO E MEMÓRIA ---
    let nomeJogador = "";
    let indicePerguntaAtual = 0;
    let pontuacao = 0;
    let acertos = 0;
    let tempoInicio;
    let intervaloCronometro;
    let temposRespostas = [];

    // Perguntas da partida atual, vindas da API (GET /api/partidas/iniciar).
    // Cada item: { id, texto, tema, respostaCorreta, explicacao }
    let perguntasAtual = [];

    // Respostas que serão enviadas para POST /api/partidas/finalizar
    // Cada item: { perguntaId, respostaEscolhida, tempoGasto }
    let respostasParaEnviar = [];

    // --- FLUXOS DE NAVEGAÇÃO ---
    btnIniciar.addEventListener('click', () => {
        mudarTela(telaInicial, telaCadastro);
        setTimeout(() => inputNome.focus(), 300);
    });

    btnContinuar.addEventListener('click', processarCadastro);
    inputNome.addEventListener('keypress', (e) => { if (e.key === 'Enter') processarCadastro(); });
    inputNome.addEventListener('input', () => {
        erroNome.classList.add('hidden');
        erroApi.classList.add('hidden');
    });

    btnNovoJogador.addEventListener('click', () => {
        // Zera o input e volta direto para o cadastro sem perder o ranking
        inputNome.value = '';
        mudarTela(telaResultado, telaCadastro);
        setTimeout(() => inputNome.focus(), 300);
    });

    function mudarTela(telaAtual, novaTela) {
        telaAtual.classList.add('hidden');
        telaAtual.classList.remove('active');
        novaTela.classList.remove('hidden');
        novaTela.classList.add('active');
    }

    async function processarCadastro() {
        const nome = inputNome.value.trim();
        erroNome.classList.add('hidden');
        erroApi.classList.add('hidden');

        if (nome === '') {
            erroNome.classList.remove('hidden');
            return;
        }

        nomeJogador = nome;

        btnContinuar.disabled = true;
        btnContinuar.innerText = 'CARREGANDO...';

        try {
            await carregarPerguntasDaApi();
            iniciarPartida();
        } catch (erro) {
            console.error('Falha ao buscar perguntas da API:', erro);
            erroApi.classList.remove('hidden');
        } finally {
            btnContinuar.disabled = false;
            btnContinuar.innerText = 'CONTINUAR';
        }
    }

    // --- INTEGRAÇÃO COM A API ---

    async function carregarPerguntasDaApi() {
        const resposta = await fetch(`${API_BASE_URL}/partidas/iniciar`);
        if (!resposta.ok) {
            throw new Error(`GET /partidas/iniciar retornou status ${resposta.status}`);
        }
        const perguntas = await resposta.json();
        if (!Array.isArray(perguntas) || perguntas.length === 0) {
            throw new Error('A API não retornou nenhuma pergunta.');
        }
        perguntasAtual = perguntas;
    }

    async function enviarResultadoParaApi() {
        const payload = {
            nomeParticipante: nomeJogador,
            respostas: respostasParaEnviar
        };

        const resposta = await fetch(`${API_BASE_URL}/partidas/finalizar`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!resposta.ok) {
            const corpo = await resposta.json().catch(() => null);
            const mensagem = corpo && corpo.mensagem ? corpo.mensagem : `status ${resposta.status}`;
            throw new Error(`POST /partidas/finalizar falhou: ${mensagem}`);
        }
    }

    async function carregarRankingDaApi() {
        const resposta = await fetch(`${API_BASE_URL}/partidas/ranking`);
        if (!resposta.ok) {
            throw new Error(`GET /partidas/ranking retornou status ${resposta.status}`);
        }
        return resposta.json();
    }

    // --- LÓGICA DO JOGO ---
    function iniciarPartida() {
        mudarTela(telaCadastro, telaPartida);
        indicePerguntaAtual = 0;
        pontuacao = 0;
        acertos = 0;
        temposRespostas = [];
        respostasParaEnviar = [];
        carregarPergunta();
    }

    function carregarPergunta() {
        feedbackBox.classList.add('hidden');
        btnAvancar.classList.add('hidden');
        btnVerdadeiro.disabled = false;
        btnFalso.disabled = false;
        btnVerdadeiro.classList.remove('selected-true', 'selected-false');
        btnFalso.classList.remove('selected-true', 'selected-false');
        feedbackBox.classList.remove('correct', 'incorrect');

        const pergunta = perguntasAtual[indicePerguntaAtual];
        contadorPergunta.innerText = `PERGUNTA ${indicePerguntaAtual + 1} DE ${perguntasAtual.length}`;
        textoPergunta.innerText = pergunta.texto;

        iniciarCronometro();
    }

    function iniciarCronometro() {
        tempoInicio = Date.now();
        cronometroDisplay.innerText = "0.00s";
        intervaloCronometro = setInterval(() => {
            const tempoDecorrido = (Date.now() - tempoInicio) / 1000;
            cronometroDisplay.innerText = tempoDecorrido.toFixed(2) + "s";
        }, 50);
    }

    function pararCronometro() {
        clearInterval(intervaloCronometro);
        return ((Date.now() - tempoInicio) / 1000).toFixed(2);
    }

    btnVerdadeiro.addEventListener('click', () => registrarResposta(true, btnVerdadeiro));
    btnFalso.addEventListener('click', () => registrarResposta(false, btnFalso));

    function registrarResposta(escolha, botaoClicado) {
        const tempoGasto = parseFloat(pararCronometro());
        temposRespostas.push(tempoGasto);

        btnVerdadeiro.disabled = true;
        btnFalso.disabled = true;

        const pergunta = perguntasAtual[indicePerguntaAtual];
        const acertou = (escolha === pergunta.respostaCorreta);

        // Guarda a resposta para enviar ao back-end no final da partida.
        // O servidor recalcula o acerto e a pontuação a partir do banco de
        // perguntas oficial — o valor calculado aqui é só para feedback
        // imediato na tela.
        respostasParaEnviar.push({
            perguntaId: pergunta.id,
            respostaEscolhida: escolha,
            tempoGasto: tempoGasto
        });

        botaoClicado.classList.add(acertou ? 'selected-true' : 'selected-false');

        if (acertou) {
            pontuacao += 2;
            acertos++;
            feedbackTitulo.innerText = `[ ACESSO PERMITIDO ] RESPOSTA CORRETA`;
            feedbackBox.classList.add('correct');
        } else {
            feedbackTitulo.innerText = `[ ALERTA ] RESPOSTA INCORRETA`;
            feedbackBox.classList.add('incorrect');
        }

        feedbackExplicacao.innerHTML = `<strong>A afirmação era ${pergunta.respostaCorreta ? 'Verdadeira' : 'Falsa'}.</strong><br><br>${pergunta.explicacao}`;
        feedbackBox.classList.remove('hidden');
        btnAvancar.classList.remove('hidden');
    }

    btnAvancar.addEventListener('click', () => {
        indicePerguntaAtual++;
        if (indicePerguntaAtual < perguntasAtual.length) {
            carregarPergunta();
        } else {
            finalizarPartida();
        }
    });

    // --- CÁLCULO, ENVIO E TELA DE RESULTADOS ---
    async function finalizarPartida() {
        mudarTela(telaPartida, telaResultado);
        erroEnvio.classList.add('hidden');

        // Cálculos locais para exibição imediata (não dependem de round-trip
        // com o servidor). O servidor recalcula os mesmos valores a partir
        // do banco de perguntas oficial ao salvar o participante.
        const totalPerguntas = perguntasAtual.length;
        const erros = totalPerguntas - acertos;
        const tempoTotal = temposRespostas.reduce((a, b) => a + b, 0);
        const tempoMedio = tempoTotal / totalPerguntas;

        resNome.innerText = `Agente: ${nomeJogador}`;
        resPontuacao.innerText = `${pontuacao} / ${totalPerguntas * 2}`;
        resAcertos.innerText = `${acertos} / ${totalPerguntas}`;
        resErros.innerText = `${erros} / ${totalPerguntas}`;
        resTempo.innerText = `${tempoTotal.toFixed(2)}s`;
        resMedia.innerText = `${tempoMedio.toFixed(2)}s`;

        // Regra da Mensagem Dinâmica (Item 10)
        resMensagemBox.className = 'feedback-box';
        if (pontuacao === 10) {
            resMensagem.innerText = "Excelente! Você demonstrou ótimo conhecimento em Segurança Cibernética.";
            resMensagemBox.classList.add('correct');
        } else if (pontuacao >= 8) {
            resMensagem.innerText = "Muito bom! Você possui bons conhecimentos, mas ainda existem alguns pontos para revisar.";
            resMensagemBox.classList.add('correct');
        } else if (pontuacao >= 6) {
            resMensagem.innerText = "Bom começo! Algumas práticas de segurança ainda merecem atenção.";
            resMensagemBox.classList.add('incorrect');
        } else {
            resMensagem.innerText = "Você identificou alguns conceitos importantes. Aproveite as explicações para melhorar seus conhecimentos.";
            resMensagemBox.classList.add('incorrect');
        }

        // Envia o resultado para persistência e, se der certo, busca o
        // ranking atualizado direto do servidor (compartilhado entre todos
        // os participantes da atividade).
        try {
            await enviarResultadoParaApi();
            const ranking = await carregarRankingDaApi();
            renderizarRanking(ranking);
        } catch (erro) {
            console.error('Falha ao salvar/consultar dados no servidor:', erro);
            erroEnvio.classList.remove('hidden');
        }
    }

    // --- LÓGICA DO RANKING (Item 11) ---
    // O ranking agora vem pronto (já ordenado pela regra de negócio) do
    // back-end via GET /api/partidas/ranking, então aqui só renderizamos.
    function renderizarRanking(ranking) {
        rankingList.innerHTML = '';
        ranking.forEach((jogador, index) => {
            const li = document.createElement('li');
            li.className = 'ranking-item';

            // Destaca os 3 primeiros colocados
            if (index === 0) li.classList.add('top-1');
            else if (index === 1) li.classList.add('top-2');
            else if (index === 2) li.classList.add('top-3');

            li.innerHTML = `
                <span>${index + 1}º</span>
                <span>${jogador.nome}</span>
                <span>${jogador.pontuacao} pts</span>
                <span>${jogador.tempoTotal.toFixed(2)}s</span>
            `;
            rankingList.appendChild(li);
        });
    }
});
