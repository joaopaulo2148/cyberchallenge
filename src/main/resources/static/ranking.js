document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = '/api';

    const abas = document.querySelectorAll('.ranking-tab');
    const carregando = document.getElementById('ranking-carregando');
    const vazio = document.getElementById('ranking-vazio');
    const erro = document.getElementById('ranking-erro');
    const tabelaWrap = document.getElementById('ranking-tabela-wrap');
    const tbody = document.getElementById('ranking-tbody');

    // Cache simples por nivel, para nao refazer a chamada toda vez que o
    // usuario troca de aba e volta.
    const cache = {};

    function mostrarApenas(elemento) {
        [carregando, vazio, erro, tabelaWrap].forEach(el => el.classList.add('hidden'));
        if (elemento) elemento.classList.remove('hidden');
    }

    async function carregarRanking(nivel) {
        mostrarApenas(carregando);

        if (cache[nivel]) {
            renderizar(cache[nivel]);
            return;
        }

        try {
            const resp = await fetch(`${API_BASE_URL}/partidas/ranking?nivel=${nivel}`);
            if (!resp.ok) throw new Error(`status ${resp.status}`);
            const dados = await resp.json();
            cache[nivel] = dados;
            renderizar(dados);
        } catch (e) {
            console.error('Falha ao carregar ranking:', e);
            mostrarApenas(erro);
        }
    }

    function renderizar(dados) {
        if (!dados || dados.length === 0) {
            mostrarApenas(vazio);
            return;
        }

        tbody.innerHTML = dados.map((item, index) => `
            <tr>
                <td class="pos">${index + 1}º</td>
                <td>${escaparHtml(item.nickname)}</td>
                <td>${item.pontuacao} / 10</td>
                <td>${item.percentualAcerto.toFixed(0)}%</td>
                <td>${item.tempoMedio != null ? item.tempoMedio.toFixed(1) + 's' : '-'}</td>
            </tr>
        `).join('');

        mostrarApenas(tabelaWrap);
    }

    function escaparHtml(texto) {
        const div = document.createElement('div');
        div.textContent = texto;
        return div.innerHTML;
    }

    abas.forEach(aba => {
        aba.addEventListener('click', () => {
            abas.forEach(a => a.classList.remove('ativo'));
            aba.classList.add('ativo');
            carregarRanking(aba.dataset.nivel);
        });
    });

    // Nivel 1 (Leigo) selecionado por padrao ao abrir a pagina.
    abas[0].classList.add('ativo');
    carregarRanking(abas[0].dataset.nivel);
});
