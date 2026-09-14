document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = '/api';

    // Se ja estiver logado, nao faz sentido mostrar esta pagina de novo.
    if (window.CyberConta && window.CyberConta.estaLogado()) {
        redirecionarAposAutenticar();
        return;
    }

    // --- ABAS ---
    const abas = document.querySelectorAll('.auth-tab');
    const formCadastro = document.getElementById('form-cadastro');
    const formLogin = document.getElementById('form-login');

    abas.forEach(aba => {
        aba.addEventListener('click', () => {
            abas.forEach(a => a.classList.remove('ativo'));
            aba.classList.add('ativo');
            const mostrarCadastro = aba.dataset.aba === 'cadastro';
            formCadastro.classList.toggle('hidden', !mostrarCadastro);
            formLogin.classList.toggle('hidden', mostrarCadastro);
        });
    });

    // Se a pessoa veio do botao "Jogar" (sem conta), abre direto na aba
    // de cadastro, ja com o motivo implicito -- mas se ela ja tiver conta,
    // pode trocar para "Entrar" manualmente.

    function redirecionarAposAutenticar() {
        const params = new URLSearchParams(window.location.search);
        if (params.get('next') === 'jogar') {
            window.location.href = '/index.html?jogar=1';
        } else {
            window.location.href = '/index.html';
        }
    }

    // --- CADASTRO ---
    const inputNickname = document.getElementById('cad-nickname');
    const inputIdade = document.getElementById('cad-idade');
    const inputAutoavaliacao = document.getElementById('cad-autoavaliacao');
    const autoavaliacaoValor = document.getElementById('cad-autoavaliacao-valor');
    const inputSenha = document.getElementById('cad-senha');
    const inputConfirmar = document.getElementById('cad-confirmar');
    const nicknameStatus = document.getElementById('cad-nickname-status');
    const erroCadastro = document.getElementById('erro-cadastro');
    const erroCadastroTexto = document.getElementById('erro-cadastro-texto');
    const checklist = document.getElementById('password-checklist');

    inputAutoavaliacao.addEventListener('input', () => {
        autoavaliacaoValor.innerText = inputAutoavaliacao.value;
    });

    // MELHORIA (item 5): checklist visual de senha forte em tempo real,
    // com as MESMAS regras verificadas de verdade no backend
    // (PasswordValidator) -- o frontend so da feedback rapido.
    function avaliarSenha(senha) {
        return {
            tamanho: senha.length >= 8,
            maiuscula: /[A-Z]/.test(senha),
            minuscula: /[a-z]/.test(senha),
            numero: /[0-9]/.test(senha),
            especial: /[^A-Za-z0-9]/.test(senha)
        };
    }

    inputSenha.addEventListener('input', () => {
        const resultado = avaliarSenha(inputSenha.value);
        Object.keys(resultado).forEach(regra => {
            const item = checklist.querySelector(`[data-regra="${regra}"]`);
            item.classList.toggle('atendido', resultado[regra]);
        });
    });

    let nicknameCheckTimeout = null;
    inputNickname.addEventListener('input', () => {
        erroCadastro.classList.add('hidden');
        const nickname = inputNickname.value.trim();
        nicknameStatus.textContent = '';

        if (nickname.length < 3) return;
        clearTimeout(nicknameCheckTimeout);
        nicknameCheckTimeout = setTimeout(() => verificarNickname(nickname), 400);
    });

    async function verificarNickname(nickname) {
        try {
            const resp = await fetch(`${API_BASE_URL}/participantes/disponibilidade?nickname=${encodeURIComponent(nickname)}`);
            if (!resp.ok) return;
            const dados = await resp.json();
            nicknameStatus.textContent = dados.disponivel ? 'Nickname disponível.' : 'Este nickname já está em uso.';
            nicknameStatus.style.color = dados.disponivel ? 'var(--success)' : 'var(--danger)';
        } catch (e) { /* checagem e so cortesia -- validacao real no envio */ }
    }

    function mostrarErroCadastro(mensagem) {
        erroCadastroTexto.innerText = mensagem;
        erroCadastro.classList.remove('hidden');
    }

    formCadastro.addEventListener('submit', async (e) => {
        e.preventDefault();
        erroCadastro.classList.add('hidden');

        const nickname = inputNickname.value.trim();
        const idade = parseInt(inputIdade.value, 10);
        const autoavaliacao = parseInt(inputAutoavaliacao.value, 10);
        const senha = inputSenha.value;
        const confirmar = inputConfirmar.value;

        if (nickname.length < 3) return mostrarErroCadastro('Informe um nickname com pelo menos 3 caracteres.');
        if (isNaN(idade) || idade < 1 || idade > 120) return mostrarErroCadastro('Informe uma idade válida.');
        if (senha !== confirmar) return mostrarErroCadastro('As senhas não coincidem.');

        const regras = avaliarSenha(senha);
        if (!Object.values(regras).every(Boolean)) {
            return mostrarErroCadastro('A senha ainda não atende a todos os requisitos listados acima.');
        }

        const botao = formCadastro.querySelector('button[type="submit"]');
        botao.disabled = true;
        botao.innerText = 'Criando conta...';

        try {
            const resp = await fetch(`${API_BASE_URL}/participantes`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nickname, idade, autoavaliacao, senha })
            });

            if (!resp.ok) {
                const corpo = await resp.json().catch(() => null);
                mostrarErroCadastro((corpo && corpo.mensagem) || 'Não foi possível criar a conta.');
                return;
            }

            const participante = await resp.json();
            window.CyberConta.salvarConta(participante);
            redirecionarAposAutenticar();
        } catch (erro) {
            mostrarErroCadastro('Não foi possível conectar ao servidor. Tente novamente.');
        } finally {
            botao.disabled = false;
            botao.innerText = 'Criar conta';
        }
    });

    // --- LOGIN ---
    const logNickname = document.getElementById('log-nickname');
    const logSenha = document.getElementById('log-senha');
    const erroLogin = document.getElementById('erro-login');

    formLogin.addEventListener('submit', async (e) => {
        e.preventDefault();
        erroLogin.classList.add('hidden');

        const botao = formLogin.querySelector('button[type="submit"]');
        botao.disabled = true;
        botao.innerText = 'Entrando...';

        try {
            const resp = await fetch(`${API_BASE_URL}/participantes/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nickname: logNickname.value.trim(), senha: logSenha.value })
            });

            if (!resp.ok) {
                erroLogin.classList.remove('hidden');
                return;
            }

            const participante = await resp.json();
            window.CyberConta.salvarConta(participante);
            redirecionarAposAutenticar();
        } catch (erro) {
            erroLogin.classList.remove('hidden');
        } finally {
            botao.disabled = false;
            botao.innerText = 'Entrar';
        }
    });
});
