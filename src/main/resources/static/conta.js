/**
 * conta.js - estado da "conta" do participante, compartilhado por todas
 * as paginas.
 *
 * Sistema de contas deliberadamente simples (item 3 do briefing): sem
 * sessao no servidor, sem cookies, sem token. Apos um cadastro ou login
 * bem-sucedido, o backend confirma as credenciais (nickname + senha, com
 * a senha verificada via hash BCrypt) e devolve os dados publicos da
 * conta (id, nickname, idade, autoavaliacao -- nunca a senha). O
 * front-end guarda esse resultado em sessionStorage, que dura apenas
 * enquanto a aba do navegador estiver aberta.
 *
 * Isso e uma trocas consciente de simplicidade (documentada tambem no
 * README): nao ha um mecanismo de sessao renovavel/revogavel no servidor,
 * entao qualquer pessoa com acesso ao navegador enquanto a aba estiver
 * aberta esta "logada". Para o escopo de um jogo educativo sem dados
 * sensiveis alem do proprio jogo, esse compromisso e aceitavel; nao seria
 * adequado para um sistema com informacoes sensiveis reais.
 */
(function () {
    const CHAVE_CONTA = 'cyberchallenge_conta';

    const ICONE_PESSOA =
        '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="4"></circle><path d="M4 21c0-4 4-6 8-6s8 2 8 6"></path></svg>';

    const ICONE_SAIR =
        '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16,17 21,12 16,7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>';

    function obterConta() {
        try {
            const bruto = sessionStorage.getItem(CHAVE_CONTA);
            return bruto ? JSON.parse(bruto) : null;
        } catch (e) {
            return null;
        }
    }

    function salvarConta(participante) {
        try {
            sessionStorage.setItem(CHAVE_CONTA, JSON.stringify(participante));
        } catch (e) { /* sem problema se sessionStorage nao estiver disponivel */ }
    }

    function sair() {
        try { sessionStorage.removeItem(CHAVE_CONTA); } catch (e) { /* noop */ }
        window.location.href = '/index.html';
    }

    function renderizarControleDeConta() {
        const container = document.getElementById('account-control');
        if (!container) return;

        const conta = obterConta();
        if (conta) {
            container.innerHTML = `
                <span class="conta-logada">
                    ${ICONE_PESSOA}
                    <span class="conta-nickname">${escaparHtml(conta.nickname)}</span>
                    <button type="button" class="btn-sair" id="btn-sair-conta" aria-label="Sair da conta">${ICONE_SAIR}</button>
                </span>
            `;
            document.getElementById('btn-sair-conta').addEventListener('click', sair);
        } else {
            container.innerHTML = `<a href="/auth.html">Cadastrar-se / Entrar</a>`;
        }
    }

    function escaparHtml(texto) {
        const div = document.createElement('div');
        div.textContent = texto;
        return div.innerHTML;
    }

    document.addEventListener('DOMContentLoaded', renderizarControleDeConta);

    window.CyberConta = {
        obterConta,
        salvarConta,
        sair,
        estaLogado: () => obterConta() !== null
    };
})();
