/**
 * theme.js - alternancia entre tema claro e escuro, compartilhado por
 * todas as paginas do site.
 *
 * A preferencia e lembrada em localStorage enquanto o usuario estiver
 * usando a aplicacao neste navegador (item 15: "se isso puder ser feito
 * de forma simples" -- nao ha conta de usuario para guardar isso no
 * servidor, entao localStorage e a solucao simples e adequada aqui).
 */
(function () {
    const CHAVE_TEMA = 'cyberchallenge-theme';

    const ICONE_SOL =
        '<svg class="icon-svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">' +
        '<circle cx="12" cy="12" r="4"></circle>' +
        '<path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"></path>' +
        '</svg>';

    const ICONE_LUA =
        '<svg class="icon-svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">' +
        '<path d="M21 12.8A9 9 0 1 1 11.2 3a7 7 0 0 0 9.8 9.8z"></path>' +
        '</svg>';

    function obterTemaPreferido() {
        const salvo = localStorage.getItem(CHAVE_TEMA);
        if (salvo === 'light' || salvo === 'dark') return salvo;
        return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }

    function aplicarTema(tema) {
        document.documentElement.setAttribute('data-theme', tema);
        const botao = document.getElementById('theme-toggle');
        if (botao) {
            // O icone mostrado representa o tema para o qual o clique vai
            // mudar (convencao comum): no tema claro, mostra a lua
            // (\"mudar para escuro\"); no tema escuro, mostra o sol.
            botao.innerHTML = tema === 'dark' ? ICONE_SOL : ICONE_LUA;
            botao.setAttribute('aria-label', tema === 'dark' ? 'Mudar para tema claro' : 'Mudar para tema escuro');
        }
    }

    function alternarTema() {
        const atual = document.documentElement.getAttribute('data-theme');
        const novo = atual === 'dark' ? 'light' : 'dark';
        localStorage.setItem(CHAVE_TEMA, novo);
        aplicarTema(novo);
    }

    aplicarTema(obterTemaPreferido());

    document.addEventListener('DOMContentLoaded', () => {
        const botao = document.getElementById('theme-toggle');
        if (botao) {
            aplicarTema(document.documentElement.getAttribute('data-theme'));
            botao.addEventListener('click', alternarTema);
        }
    });
})();
