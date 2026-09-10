document.addEventListener('DOMContentLoaded', () => {
    // --- CONFIGURAÇÃO DA API ---
    const API_BASE_URL = '/api';

    // Chave usada no localStorage para lembrar quais perguntas o jogador já
    // viu no nível atual, para dar mais variedade em "jogar novamente"
    // (MELHORIA, secao 1). Guardamos por nível porque cada nível tem seu
    // próprio estoque de perguntas.
    const CHAVE_HISTORICO = 'cyberchallenge_perguntas_recentes';

    // --- ELEMENTOS DAS TELAS ---
    const telaInicial = document.getElementById('tela-inicial');
    const telaCadastro = document.getElementById('tela-cadastro');
    const telaNivel = document.getElementById('tela-nivel');
    const telaPartida = document.getElementById('tela-partida');
    const telaResultado = document.getElementById('tela-resultado');
    const telaRanking = document.getElementById('tela-ranking');

    // --- ELEMENTOS MENU ---
    const btnIniciar = document.getElementById('btn-iniciar');
    const btnVerRanking = document.getElementById('btn-ver-ranking');

    // --- ELEMENTOS CADASTRO ---
    const btnVoltarCadastro = document.getElementById('btn-voltar-cadastro');
    const btnContinuar = document.getElementById('btn-continuar');
    const inputNome = document.getElementById('input-nome');
    const inputIdade = document.getElementById('input-idade');
    const inputAutoavaliacao = document.getElementById('input-autoavaliacao');
    const autoavaliacaoValor = document.getElementById('autoavaliacao-valor');
    const erroNome = document.getElementById('erro-nome');
    const erroApi = document.getElementById('erro-api');

    // --- ELEMENTOS NIVEL ---
    const btnVoltarNivel = document.getElementById('btn-voltar-nivel');
    const cardsNivel = document.querySelectorAll('.nivel-card');
    const erroNivel = document.getElementById('erro-nivel');

    // --- ELEMENTOS PARTIDA ---
    const badgeNivel = document.getElementById('badge-nivel');
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
    const resNotaFinal = document.getElementById('res-nota-final');
    const resPontuacao = document.getElementById('res-pontuacao');
    const resAcertos = document.getElementById('res-acertos');
    const resErros = document.getElementById('res-erros');
    const resTempo = document.getElementById('res-tempo');
    const resMedia = document.getElementById('res-media');
    const resNivel = document.getElementById('res-nivel');
    const resMensagem = document.getElementById('res-mensagem');
    const resMensagemBox = document.getElementById('res-mensagem-box');
    const erroEnvio = document.getElementById('erro-envio');
    const rankingList = document.getElementById('ranking-list');
    const btnJogarNovamente = document.getElementById('btn-jogar-novamente');
    const btnVoltarMenu = document.getElementById('btn-voltar-menu');

    // --- ELEMENTOS TELA DE RANKING INDEPENDENTE ---
    const btnVoltarRanking = document.getElementById('btn-voltar-ranking');
    const rankingListStandalone = document.getElementById('ranking-list-standalone');
    const rankingVazio = document.getElementById('ranking-vazio');
    const erroRanking = document.getElementById('erro-ranking');

    // Nomes de exibição de cada nível (usados na tela de partida/resultado)
    const NOMES_NIVEL = { 1: 'Leigo', 2: 'Usuário', 3: 'Intermediário', 4: 'Especialista' };

    // --- VARIÁVEIS DE JOGO E MEMÓRIA ---
    let nomeJogador = "";
    let idadeJogador = null;
    let autoavaliacaoJogador = 5;
    let nivelEscolhido = null;
    let indicePerguntaAtual = 0;
    let pontuacao = 0;
    let acertos = 0;
    let tempoInicio;
    let intervaloCronometro;
    let temposRespostas = [];

    // Perguntas da partida atual, vindas da API (GET /api/partidas/iniciar).
    // Cada item: { id, texto, tema, respostaCorreta, explicacao, nivel }
    let perguntasAtual = [];

    // Respostas que serão enviadas para POST /api/partidas/finalizar
    // Cada item: { perguntaId, respostaEscolhida, tempoGasto }
    let respostasParaEnviar = [];

    // --- FLUXO DE NAVEGAÇÃO (secao 5) ---
    // MENU -> CADASTRO -> ESCOLHA DE NÍVEL -> PARTIDA -> RESULTADO -> RANKING / JOGAR NOVAMENTE / MENU
    btnIniciar.addEventListener('click', () => {
        mudarTela(telaInicial, telaCadastro);
        setTimeout(() => inputNome.focus(), 300);
    });

    btnVerRanking.addEventListener('click', () => {
        mudarTela(telaInicial, telaRanking);
        carregarTelaRankingIndependente();
    });

    btnVoltarRanking.addEventListener('click', () => {
        mudarTela(telaRanking, telaInicial);
    });

    btnVoltarCadastro.addEventListener('click', () => {
        mudarTela(telaCadastro, telaInicial);
    });

    inputAutoavaliacao.addEventListener('input', () => {
        autoavaliacaoValor.innerText = inputAutoavaliacao.value;
    });

    btnContinuar.addEventListener('click', processarCadastro);
    inputNome.addEventListener('keypress', (e) => { if (e.key === 'Enter') processarCadastro(); });
    inputNome.addEventListener('input', limparErrosCadastro);
    inputIdade.addEventListener('input', limparErrosCadastro);

    function limparErrosCadastro() {
        erroNome.classList.add('hidden');
        erroApi.classList.add('hidden');
    }

    btnVoltarNivel.addEventListener('click', () => {
        mudarTela(telaNivel, telaCadastro);
    });

    cardsNivel.forEach(card => {
        card.addEventListener('click', () => selecionarNivel(parseInt(card.dataset.nivel, 10)));
    });

    btnJogarNovamente.addEventListener('click', () => {
        mudarTela(telaResultado, telaNivel);
    });

    btnVoltarMenu.addEventListener('click', () => {
        mudarTela(telaResultado, telaInicial);
    });

    function mudarTela(telaAtual, novaTela) {
        telaAtual.classList.add('hidden');
        telaAtual.classList.remove('active');
        novaTela.classList.remove('hidden');
        novaTela.classList.add('active');
    }

    // --- CADASTRO (secao 3) ---
    function processarCadastro() {
        const nome = inputNome.value.trim();
        const idade = parseInt(inputIdade.value, 10);
        limparErrosCadastro();

        // BUG CORRIGIDO/MELHORIA: validação de nome e idade também no
        // front-end, além da validação definitiva feita no back-end.
        if (nome === '' || isNaN(idade) || idade < 1 || idade > 120) {
            erroNome.classList.remove('hidden');
            return;
        }

        nomeJogador = nome;
        idadeJogador = idade;
        autoavaliacaoJogador = parseInt(inputAutoavaliacao.value, 10);

        mudarTela(telaCadastro, telaNivel);
    }

    // --- ESCOLHA DE NÍVEL (secao 2) ---
    async function selecionarNivel(nivel) {
        nivelEscolhido = nivel;
        erroNivel.classList.add('hidden');

        cardsNivel.forEach(c => c.classList.remove('selecionado'));
        const cardClicado = document.querySelector(`.nivel-card[data-nivel="${nivel}"]`);
        if (cardClicado) cardClicado.classList.add('selecionado');

        cardsNivel.forEach(c => c.disabled = true);

        try {
            await carregarPerguntasDaApi(nivel);
            iniciarPartida();
        } catch (erro) {
            console.error('Falha ao buscar perguntas da API:', erro);
            erroNivel.classList.remove('hidden');
        } finally {
            cardsNivel.forEach(c => c.disabled = false);
        }
    }

    // --- INTEGRAÇÃO COM A API ---

    function obterPerguntasRecentes(nivel) {
        try {
            const historico = JSON.parse(localStorage.getItem(CHAVE_HISTORICO) || '{}');
            return historico[nivel] || [];
        } catch (e) {
            return [];
        }
    }

    function salvarPerguntasRecentes(nivel, ids) {
        try {
            const historico = JSON.parse(localStorage.getItem(CHAVE_HISTORICO) || '{}');
            historico[nivel] = ids;
            localStorage.setItem(CHAVE_HISTORICO, JSON.stringify(historico));
        } catch (e) {
            // localStorage indisponível (ex: modo privado) — sem problema,
            // o jogo simplesmente não terá a otimização de variedade extra.
        }
    }

    async function carregarPerguntasDaApi(nivel) {
        // MELHORIA (secao 1): informa ao servidor quais perguntas foram
        // usadas na última partida deste nível, para tentar evitar repeti-las.
        const idsRecentes = obterPerguntasRecentes(nivel);
        const params = new URLSearchParams({ nivel: String(nivel) });
        idsRecentes.forEach(id => params.append('excluir', id));

        const resposta = await fetch(`${API_BASE_URL}/partidas/iniciar?${params.toString()}`);
        if (!resposta.ok) {
            throw new Error(`GET /partidas/iniciar retornou status ${resposta.status}`);
        }
        const perguntas = await resposta.json();
        if (!Array.isArray(perguntas) || perguntas.length === 0) {
            throw new Error('A API não retornou nenhuma pergunta.');
        }
        perguntasAtual = perguntas;
        salvarPerguntasRecentes(nivel, perguntas.map(p => p.id));
    }

    async function enviarResultadoParaApi() {
        const payload = {
            nomeParticipante: nomeJogador,
            idade: idadeJogador,
            autoavaliacao: autoavaliacaoJogador,
            nivel: nivelEscolhido,
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

    async function carregarTelaRankingIndependente() {
        erroRanking.classList.add('hidden');
        rankingVazio.classList.add('hidden');
        rankingListStandalone.innerHTML = '';

        try {
            const ranking = await carregarRankingDaApi();
            if (!ranking || ranking.length === 0) {
                rankingVazio.classList.remove('hidden');
                return;
            }
            renderizarRanking(ranking, rankingListStandalone);
        } catch (erro) {
            console.error('Falha ao carregar ranking:', erro);
            erroRanking.classList.remove('hidden');
        }
    }

    // --- LÓGICA DO JOGO ---
    function iniciarPartida() {
        mudarTela(telaNivel, telaPartida);
        indicePerguntaAtual = 0;
        pontuacao = 0;
        acertos = 0;
        temposRespostas = [];
        respostasParaEnviar = [];
        badgeNivel.innerText = `NÍVEL ${nivelEscolhido} · ${NOMES_NIVEL[nivelEscolhido].toUpperCase()}`;
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

        // MELHORIA (secao 4): nota final de 0 a 10, mesma regra determinística
        // usada no back-end (acertos / total * 10, arredondada a 1 casa decimal).
        const notaFinal = Math.round((acertos / totalPerguntas) * 10 * 10) / 10;

        resNome.innerText = `Agente: ${nomeJogador}`;
        resNotaFinal.innerText = notaFinal.toFixed(1);
        resPontuacao.innerText = `${pontuacao} / ${totalPerguntas * 2}`;
        resAcertos.innerText = `${acertos} / ${totalPerguntas}`;
        resErros.innerText = `${erros} / ${totalPerguntas}`;
        resTempo.innerText = `${tempoTotal.toFixed(2)}s`;
        resMedia.innerText = `${tempoMedio.toFixed(2)}s`;
        resNivel.innerText = `${nivelEscolhido} · ${NOMES_NIVEL[nivelEscolhido]}`;

        // Regra da Mensagem Dinâmica, agora baseada na nota final (0 a 10)
        resMensagemBox.className = 'feedback-box';
        if (notaFinal >= 9) {
            resMensagem.innerText = "Excelente! Você demonstrou ótimo conhecimento em Segurança Cibernética.";
            resMensagemBox.classList.add('correct');
        } else if (notaFinal >= 7) {
            resMensagem.innerText = "Muito bom! Você possui bons conhecimentos, mas ainda existem alguns pontos para revisar.";
            resMensagemBox.classList.add('correct');
        } else if (notaFinal >= 5) {
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
            renderizarRanking(ranking, rankingList);
        } catch (erro) {
            console.error('Falha ao salvar/consultar dados no servidor:', erro);
            erroEnvio.classList.remove('hidden');
        }
    }

    // --- LÓGICA DO RANKING (Item 11) ---
    // O ranking agora vem pronto (já ordenado pela regra de negócio) do
    // back-end via GET /api/partidas/ranking. A mesma função de renderização
    // é reaproveitada tanto na tela de resultado quanto na tela de ranking
    // independente acessível pelo menu.
    function renderizarRanking(ranking, elementoLista) {
        elementoLista.innerHTML = '';
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
            elementoLista.appendChild(li);
        });
    }
});
