document.addEventListener('DOMContentLoaded', () => {
    // Caminho relativo a partir de /admin/index.html -> /api/dashboard
    const API_BASE_URL = '../api';

    const elLoading = document.getElementById('admin-loading');
    const elErro = document.getElementById('admin-erro');
    const elVazio = document.getElementById('admin-vazio');
    const elContent = document.getElementById('admin-content');
    const btnAtualizar = document.getElementById('btn-atualizar');

    btnAtualizar.addEventListener('click', carregarDashboard);

    carregarDashboard();

    async function carregarDashboard() {
        mostrarApenas(elLoading);
        btnAtualizar.disabled = true;

        try {
            const resposta = await fetch(`${API_BASE_URL}/dashboard`);
            if (!resposta.ok) {
                throw new Error(`GET /dashboard retornou status ${resposta.status}`);
            }
            const dashboard = await resposta.json();

            if (!dashboard.indicadoresGerais || dashboard.indicadoresGerais.totalParticipantes === 0) {
                mostrarApenas(elVazio);
                return;
            }

            renderizarDashboard(dashboard);
            mostrarApenas(elContent);
        } catch (erro) {
            console.error('Falha ao carregar o dashboard:', erro);
            mostrarApenas(elErro);
        } finally {
            btnAtualizar.disabled = false;
        }
    }

    function mostrarApenas(elementoVisivel) {
        [elLoading, elErro, elVazio, elContent].forEach(el => el.classList.add('hidden'));
        elementoVisivel.classList.remove('hidden');
    }

    function renderizarDashboard(dashboard) {
        renderizarIndicadores(dashboard.indicadoresGerais);
        renderizarDestaquesPergunta(dashboard);
        renderizarTemas(dashboard);
        renderizarTabelaPerguntas(dashboard.perguntas);
    }

    function renderizarIndicadores(ind) {
        document.getElementById('kpi-total-participantes').innerText = ind.totalParticipantes;
        document.getElementById('kpi-total-partidas').innerText = ind.totalPartidas;
        document.getElementById('kpi-media-pontuacao').innerText = ind.mediaPontuacao.toFixed(1);
        document.getElementById('kpi-media-acertos').innerText = `${ind.mediaAcertos.toFixed(1)} / 5`;
        document.getElementById('kpi-taxa-acerto').innerText = `${ind.taxaGeralAcertoPercentual.toFixed(1)}%`;
        document.getElementById('kpi-tempo-medio').innerText = `${ind.tempoMedioPartidasSegundos.toFixed(1)}s`;
    }

    function renderizarDestaquesPergunta(dashboard) {
        preencherDestaque('dest-mais-acertada', dashboard.perguntaMaisAcertada, p => `${p.percentualAcerto.toFixed(1)}%`);
        preencherDestaque('dest-mais-errada', dashboard.perguntaMaisErrada, p => `${p.percentualErro.toFixed(1)}%`);
        preencherDestaque('dest-maior-tempo', dashboard.perguntaMaiorTempoMedio, p => `${p.tempoMedioSegundos.toFixed(2)}s`);
        preencherDestaque('dest-menor-tempo', dashboard.perguntaMenorTempoMedio, p => `${p.tempoMedioSegundos.toFixed(2)}s`);
    }

    function preencherDestaque(prefixoId, pergunta, formatarValor) {
        const elTexto = document.getElementById(prefixoId);
        const elValor = document.getElementById(`${prefixoId}-valor`);
        if (!pergunta) {
            elTexto.innerText = '—';
            elValor.innerText = '—';
            return;
        }
        elTexto.innerText = truncar(pergunta.texto, 90);
        elValor.innerText = formatarValor(pergunta);
    }

    function renderizarTemas(dashboard) {
        document.getElementById('tema-maior-acerto').innerText = dashboard.temaMaiorAcerto
            ? `${dashboard.temaMaiorAcerto.tema} (${dashboard.temaMaiorAcerto.percentualAcerto.toFixed(1)}%)`
            : '—';
        document.getElementById('tema-maior-erro').innerText = dashboard.temaMaiorErro
            ? `${dashboard.temaMaiorErro.tema} (${dashboard.temaMaiorErro.percentualAcerto.toFixed(1)}% de acerto)`
            : '—';

        const container = document.getElementById('tema-lista');
        container.innerHTML = '';

        const temasOrdenados = [...dashboard.temas].sort((a, b) => b.percentualAcerto - a.percentualAcerto);

        temasOrdenados.forEach(tema => {
            const linha = document.createElement('div');
            linha.className = 'tema-linha';
            linha.innerHTML = `
                <span class="tema-nome" title="${tema.tema}">${tema.tema}</span>
                <div class="tema-barra-fundo">
                    <div class="tema-barra-preenchida" style="width: ${tema.percentualAcerto.toFixed(1)}%"></div>
                </div>
                <span class="tema-percentual">${tema.percentualAcerto.toFixed(1)}%</span>
            `;
            container.appendChild(linha);
        });
    }

    function renderizarTabelaPerguntas(perguntas) {
        const tbody = document.getElementById('tabela-perguntas-body');
        tbody.innerHTML = '';

        const ordenadas = [...perguntas].sort((a, b) => a.percentualAcerto - b.percentualAcerto);

        ordenadas.forEach(p => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="celula-texto">${p.texto}</td>
                <td>${p.tema}</td>
                <td>${p.totalRespostas}</td>
                <td class="tag-acerto">${p.percentualAcerto.toFixed(1)}%</td>
                <td class="tag-erro">${p.percentualErro.toFixed(1)}%</td>
                <td>${p.quantidadeVerdadeiro} / ${p.quantidadeFalso}</td>
                <td>${p.tempoMedioSegundos.toFixed(2)}s</td>
            `;
            tbody.appendChild(tr);
        });
    }

    function truncar(texto, tamanho) {
        if (!texto) return '—';
        return texto.length > tamanho ? texto.slice(0, tamanho - 1) + '…' : texto;
    }
});
