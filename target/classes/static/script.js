document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = '/api';
    const CHAVE_HISTORICO_PERGUNTAS = 'cyberchallenge_perguntas_recentes';

    const NOMES_NIVEL = { 1: 'Leigo', 2: 'Usuário', 3: 'Intermediário', 4: 'Especialista' };

    const ICONE_CHECK = '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.3" stroke-linecap="round" stroke-linejoin="round"><polyline points="4,12 9,17 20,6"></polyline></svg>';
    const ICONE_X = '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.3" stroke-linecap="round" stroke-linejoin="round"><line x1="5" y1="5" x2="19" y2="19"></line><line x1="19" y1="5" x2="5" y2="19"></line></svg>';

    // --- TELAS ---
    const telas = {
        menu: document.getElementById('tela-menu'),
        identificacao: document.getElementById('tela-identificacao'),
        nivel: document.getElementById('tela-nivel'),
        partida: document.getElementById('tela-partida'),
        resultado: document.getElementById('tela-resultado')
    };

    function mudarTela(de, para) {
        de.classList.add('hidden');
        de.classList.remove('active');
        para.classList.remove('hidden');
        para.classList.add('active');
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    // --- ESTADO DA SESSAO (sem contas -- vive apenas na memoria da pagina) ---
    let participanteAtual = null; // { id, nickname, idade, autoavaliacao }
    let nivelEscolhido = null;
    let perguntasAtual = [];
    let respostasEstado = [];     // 1 posicao por pergunta: null ou { escolha, alternativaEscolhidaId, tempoGasto, acertou }
    let indiceAtual = 0;
    let tempoInicioPergunta = null;
    let intervaloCronometro = null;
    let ultimoResultado = null;

    // ===================== MENU =====================
    const btnJogar = document.getElementById('btn-jogar');
    btnJogar.addEventListener('click', () => {
        if (participanteAtual) {
            // Ja se identificou nesta sessao -- vai direto para a escolha de nivel.
            mudarTela(telas.menu, telas.nivel);
        } else {
            mudarTela(telas.menu, telas.identificacao);
            setTimeout(() => document.getElementById('input-nickname').focus(), 250);
        }
    });

    // ===================== IDENTIFICACAO =====================
    const inputNickname = document.getElementById('input-nickname');
    const inputIdade = document.getElementById('input-idade');
    const inputAutoavaliacao = document.getElementById('input-autoavaliacao');
    const autoavaliacaoValor = document.getElementById('autoavaliacao-valor');
    const nicknameStatus = document.getElementById('nickname-status');
    const erroIdentificacao = document.getElementById('erro-identificacao');
    const erroIdentificacaoTexto = document.getElementById('erro-identificacao-texto');
    const btnVoltarIdentificacao = document.getElementById('btn-voltar-identificacao');
    const btnProximoIdentificacao = document.getElementById('btn-proximo-identificacao');

    inputAutoavaliacao.addEventListener('input', () => {
        autoavaliacaoValor.innerText = inputAutoavaliacao.value;
    });

    let nicknameCheckTimeout = null;
    inputNickname.addEventListener('input', () => {
        erroIdentificacao.classList.add('hidden');
        const nickname = inputNickname.value.trim();
        nicknameStatus.textContent = '';
        nicknameStatus.style.color = '';

        if (nickname.length < 3) return;

        clearTimeout(nicknameCheckTimeout);
        nicknameCheckTimeout = setTimeout(() => verificarNickname(nickname), 400);
    });

    async function verificarNickname(nickname) {
        try {
            const resp = await fetch(`${API_BASE_URL}/participantes/disponibilidade?nickname=${encodeURIComponent(nickname)}`);
            if (!resp.ok) return;
            const dados = await resp.json();
            if (dados.disponivel) {
                nicknameStatus.textContent = 'Nickname disponível.';
                nicknameStatus.style.color = 'var(--success)';
            } else {
                nicknameStatus.textContent = 'Este nickname já está em uso. Escolha outro.';
                nicknameStatus.style.color = 'var(--danger)';
            }
        } catch (e) {
            // Feedback rapido e apenas cortesia -- se falhar, a checagem
            // definitiva ainda acontece no envio (POST /api/participantes).
        }
    }

    btnVoltarIdentificacao.addEventListener('click', () => mudarTela(telas.identificacao, telas.menu));

    btnProximoIdentificacao.addEventListener('click', async () => {
        erroIdentificacao.classList.add('hidden');

        const nickname = inputNickname.value.trim();
        const idade = parseInt(inputIdade.value, 10);
        const autoavaliacao = parseInt(inputAutoavaliacao.value, 10);

        if (nickname.length < 3) {
            mostrarErroIdentificacao('Informe um nickname com pelo menos 3 caracteres.');
            return;
        }
        if (isNaN(idade) || idade < 1 || idade > 120) {
            mostrarErroIdentificacao('Informe uma idade válida.');
            return;
        }

        btnProximoIdentificacao.disabled = true;
        btnProximoIdentificacao.innerText = 'Verificando...';

        try {
            const resp = await fetch(`${API_BASE_URL}/participantes`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nickname, idade, autoavaliacao })
            });

            if (resp.status === 409) {
                const corpo = await resp.json().catch(() => null);
                mostrarErroIdentificacao((corpo && corpo.mensagem) || 'Este nickname já está em uso. Escolha outro.');
                return;
            }
            if (!resp.ok) {
                const corpo = await resp.json().catch(() => null);
                mostrarErroIdentificacao((corpo && corpo.mensagem) || 'Não foi possível continuar. Verifique os dados informados.');
                return;
            }

            const participante = await resp.json();
            participanteAtual = participante;
            mudarTela(telas.identificacao, telas.nivel);
        } catch (erro) {
            mostrarErroIdentificacao('Não foi possível conectar ao servidor. Tente novamente.');
        } finally {
            btnProximoIdentificacao.disabled = false;
            btnProximoIdentificacao.innerText = 'Próximo';
        }
    });

    function mostrarErroIdentificacao(mensagem) {
        erroIdentificacaoTexto.innerText = mensagem;
        erroIdentificacao.classList.remove('hidden');
    }

    // ===================== NIVEL =====================
    const cardsNivel = document.querySelectorAll('.nivel-card');
    const erroNivel = document.getElementById('erro-nivel');
    const btnVoltarNivel = document.getElementById('btn-voltar-nivel');

    btnVoltarNivel.addEventListener('click', () => {
        // Se o participante ja estava identificado antes de chegar aqui
        // (ex: "jogar novamente"), volta direto pro menu; caso contrario,
        // volta para a identificacao.
        mudarTela(telas.nivel, telas.menu);
    });

    cardsNivel.forEach(card => {
        card.addEventListener('click', () => selecionarNivel(parseInt(card.dataset.nivel, 10)));
    });

    async function selecionarNivel(nivel) {
        nivelEscolhido = nivel;
        erroNivel.classList.add('hidden');
        cardsNivel.forEach(c => c.disabled = true);

        try {
            await carregarPerguntas(nivel);
            iniciarPartida();
        } catch (erro) {
            console.error('Falha ao buscar perguntas:', erro);
            erroNivel.classList.remove('hidden');
        } finally {
            cardsNivel.forEach(c => c.disabled = false);
        }
    }

    function obterPerguntasRecentes(nivel) {
        try {
            const historico = JSON.parse(localStorage.getItem(CHAVE_HISTORICO_PERGUNTAS) || '{}');
            return historico[nivel] || [];
        } catch (e) { return []; }
    }

    function salvarPerguntasRecentes(nivel, ids) {
        try {
            const historico = JSON.parse(localStorage.getItem(CHAVE_HISTORICO_PERGUNTAS) || '{}');
            historico[nivel] = ids;
            localStorage.setItem(CHAVE_HISTORICO_PERGUNTAS, JSON.stringify(historico));
        } catch (e) { /* sem problema se localStorage nao estiver disponivel */ }
    }

    async function carregarPerguntas(nivel) {
        const idsRecentes = obterPerguntasRecentes(nivel);
        const params = new URLSearchParams({ nivel: String(nivel) });
        idsRecentes.forEach(id => params.append('excluir', id));

        const resp = await fetch(`${API_BASE_URL}/partidas/iniciar?${params.toString()}`);
        if (!resp.ok) throw new Error(`status ${resp.status}`);
        const perguntas = await resp.json();
        if (!Array.isArray(perguntas) || perguntas.length === 0) throw new Error('sem perguntas');

        perguntasAtual = perguntas;
        salvarPerguntasRecentes(nivel, perguntas.map(p => p.id));
    }

    // ===================== PARTIDA =====================
    const badgeNivel = document.getElementById('badge-nivel');
    const contadorPergunta = document.getElementById('contador-pergunta');
    const cronometroDisplay = document.getElementById('cronometro');
    const progressBarFill = document.getElementById('progress-bar-fill');
    const perguntaTema = document.getElementById('pergunta-tema');
    const textoPergunta = document.getElementById('texto-pergunta');
    const opcoesVf = document.getElementById('opcoes-vf');
    const opcoesMultipla = document.getElementById('opcoes-multipla');
    const feedbackBox = document.getElementById('feedback-box');
    const feedbackTitulo = document.getElementById('feedback-titulo');
    const feedbackExplicacao = document.getElementById('feedback-explicacao');
    const btnPerguntaAnterior = document.getElementById('btn-pergunta-anterior');
    const btnProximaPergunta = document.getElementById('btn-proxima-pergunta');

    function iniciarPartida() {
        mudarTela(telas.nivel, telas.partida);
        indiceAtual = 0;
        respostasEstado = new Array(perguntasAtual.length).fill(null);
        badgeNivel.innerText = `Nível ${nivelEscolhido} · ${NOMES_NIVEL[nivelEscolhido]}`;
        renderizarPergunta();
    }

    function renderizarPergunta() {
        const pergunta = perguntasAtual[indiceAtual];
        const estado = respostasEstado[indiceAtual];

        contadorPergunta.innerText = `Pergunta ${indiceAtual + 1} de ${perguntasAtual.length}`;
        progressBarFill.style.width = `${((indiceAtual + 1) / perguntasAtual.length) * 100}%`;
        perguntaTema.innerText = pergunta.tema;
        textoPergunta.innerText = pergunta.texto;

        feedbackBox.classList.add('hidden');
        feedbackBox.classList.remove('correct', 'incorrect');

        if (pergunta.tipo === 'MULTIPLA_ESCOLHA') {
            opcoesVf.classList.add('hidden');
            opcoesMultipla.classList.remove('hidden');
            renderizarOpcoesMultipla(pergunta, estado);
        } else {
            opcoesMultipla.classList.add('hidden');
            opcoesVf.classList.remove('hidden');
            renderizarOpcoesVf(pergunta, estado);
        }

        btnPerguntaAnterior.disabled = (indiceAtual === 0);
        btnProximaPergunta.disabled = (estado === null);
        btnProximaPergunta.innerHTML = (indiceAtual === perguntasAtual.length - 1)
            ? 'Ver resultado'
            : 'Próxima <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12,5 19,12 12,19"></polyline></svg>';

        if (estado) {
            mostrarFeedback(pergunta, estado);
            pararCronometro();
            cronometroDisplay.innerText = `${estado.tempoGasto.toFixed(1)}s`;
        } else {
            iniciarCronometro();
        }
    }

    function renderizarOpcoesVf(pergunta, estado) {
        const botoes = opcoesVf.querySelectorAll('.opcao-btn');
        botoes.forEach(botao => {
            const valor = botao.dataset.valor === 'true';
            botao.className = 'opcao-btn';
            botao.disabled = false;
            botao.onclick = () => registrarResposta({ escolha: valor });

            if (estado) {
                if (valor === pergunta.respostaCorreta) botao.classList.add('correta');
                else if (valor === estado.escolha) botao.classList.add('incorreta');
                else botao.classList.add('neutra-desabilitada');
            }
        });
    }

    function renderizarOpcoesMultipla(pergunta, estado) {
        opcoesMultipla.innerHTML = '';
        pergunta.alternativas.forEach(alt => {
            const botao = document.createElement('button');
            botao.type = 'button';
            botao.className = 'opcao-btn';
            botao.innerText = alt.texto;
            botao.onclick = () => registrarResposta({ alternativaEscolhidaId: alt.id });

            if (estado) {
                if (alt.correta) botao.classList.add('correta');
                else if (estado.alternativaEscolhidaId === alt.id) botao.classList.add('incorreta');
                else botao.classList.add('neutra-desabilitada');
            }
            opcoesMultipla.appendChild(botao);
        });
    }

    function registrarResposta(escolha) {
        const pergunta = perguntasAtual[indiceAtual];
        const estadoAnterior = respostasEstado[indiceAtual];

        // Ao responder pela primeira vez, marca o tempo gasto. Ao voltar
        // numa pergunta ja respondida e trocar a escolha, mantemos o tempo
        // original (nao ha novo cronometro rodando em modo de revisao).
        let tempoGasto;
        if (estadoAnterior) {
            tempoGasto = estadoAnterior.tempoGasto;
        } else {
            tempoGasto = parseFloat(pararCronometro());
        }

        let acertou;
        if (pergunta.tipo === 'MULTIPLA_ESCOLHA') {
            const alt = pergunta.alternativas.find(a => a.id === escolha.alternativaEscolhidaId);
            acertou = !!(alt && alt.correta);
        } else {
            acertou = (escolha.escolha === pergunta.respostaCorreta);
        }

        respostasEstado[indiceAtual] = {
            escolha: escolha.escolha,
            alternativaEscolhidaId: escolha.alternativaEscolhidaId,
            tempoGasto,
            acertou
        };

        renderizarPergunta();
    }

    function mostrarFeedback(pergunta, estado) {
        feedbackBox.classList.remove('hidden');
        if (estado.acertou) {
            feedbackBox.classList.add('correct');
            feedbackTitulo.innerHTML = `${ICONE_CHECK} Resposta correta`;
        } else {
            feedbackBox.classList.add('incorrect');
            feedbackTitulo.innerHTML = `${ICONE_X} Resposta incorreta`;
        }
        feedbackExplicacao.innerText = pergunta.explicacao;
    }

    function iniciarCronometro() {
        tempoInicioPergunta = Date.now();
        cronometroDisplay.innerText = '0.0s';
        intervaloCronometro = setInterval(() => {
            cronometroDisplay.innerText = `${((Date.now() - tempoInicioPergunta) / 1000).toFixed(1)}s`;
        }, 100);
    }

    function pararCronometro() {
        clearInterval(intervaloCronometro);
        return tempoInicioPergunta ? ((Date.now() - tempoInicioPergunta) / 1000).toFixed(2) : '0.00';
    }

    btnPerguntaAnterior.addEventListener('click', () => {
        if (indiceAtual > 0) {
            indiceAtual--;
            renderizarPergunta();
        }
    });

    btnProximaPergunta.addEventListener('click', () => {
        if (indiceAtual < perguntasAtual.length - 1) {
            indiceAtual++;
            renderizarPergunta();
        } else {
            finalizarPartida();
        }
    });

    // ===================== RESULTADO =====================
    const resNickname = document.getElementById('res-nickname');
    const resNotaFinal = document.getElementById('res-nota-final');
    const resAcertos = document.getElementById('res-acertos');
    const resErros = document.getElementById('res-erros');
    const resPercentual = document.getElementById('res-percentual');
    const resTempoMedio = document.getElementById('res-tempo-medio');
    const resTempoTotal = document.getElementById('res-tempo-total');
    const resNivel = document.getElementById('res-nivel');
    const erroEnvio = document.getElementById('erro-envio');
    const btnJogarNovamente = document.getElementById('btn-jogar-novamente');
    const btnVoltarMenuResultado = document.getElementById('btn-voltar-menu-resultado');

    async function finalizarPartida() {
        mudarTela(telas.partida, telas.resultado);
        erroEnvio.classList.add('hidden');

        const total = perguntasAtual.length;
        const acertos = respostasEstado.filter(e => e && e.acertou).length;
        const erros = total - acertos;
        const tempoTotal = respostasEstado.reduce((soma, e) => soma + (e ? e.tempoGasto : 0), 0);
        const tempoMedio = tempoTotal / total;
        const percentual = (acertos / total) * 100;

        ultimoResultado = { acertos, erros, tempoTotal, tempoMedio, percentual, nivel: nivelEscolhido };

        resNickname.innerText = participanteAtual.nickname;
        resNotaFinal.innerText = String(acertos);
        resAcertos.innerText = `${acertos} / ${total}`;
        resErros.innerText = `${erros} / ${total}`;
        resPercentual.innerText = `${percentual.toFixed(0)}%`;
        resTempoMedio.innerText = `${tempoMedio.toFixed(1)}s`;
        resTempoTotal.innerText = `${tempoTotal.toFixed(1)}s`;
        resNivel.innerText = `${nivelEscolhido} · ${NOMES_NIVEL[nivelEscolhido]}`;

        const payload = {
            participanteId: participanteAtual.id,
            nivel: nivelEscolhido,
            respostas: perguntasAtual.map((p, i) => {
                const e = respostasEstado[i];
                return {
                    perguntaId: p.id,
                    respostaEscolhida: p.tipo === 'VERDADEIRO_FALSO' ? e.escolha : null,
                    alternativaEscolhidaId: p.tipo === 'MULTIPLA_ESCOLHA' ? e.alternativaEscolhidaId : null,
                    tempoGasto: e.tempoGasto
                };
            })
        };

        try {
            const resp = await fetch(`${API_BASE_URL}/partidas/finalizar`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if (!resp.ok) throw new Error(`status ${resp.status}`);
        } catch (erro) {
            console.error('Falha ao salvar o resultado:', erro);
            erroEnvio.classList.remove('hidden');
        }
    }

    btnJogarNovamente.addEventListener('click', () => {
        mudarTela(telas.resultado, telas.nivel);
    });

    btnVoltarMenuResultado.addEventListener('click', () => {
        mudarTela(telas.resultado, telas.menu);
    });
});
