document.addEventListener('DOMContentLoaded', async () => {
    const API_BASE_URL = '/api';

    const carregando = document.getElementById('estatisticas-carregando');
    const vazio = document.getElementById('estatisticas-vazio');
    const erro = document.getElementById('estatisticas-erro');
    const conteudo = document.getElementById('estatisticas-conteudo');

    function mostrarApenas(elemento) {
        [carregando, vazio, erro, conteudo].forEach(el => el.classList.add('hidden'));
        if (elemento) elemento.classList.remove('hidden');
    }

    function escaparHtml(texto) {
        const div = document.createElement('div');
        div.textContent = texto == null ? '' : texto;
        return div.innerHTML;
    }

    function truncar(texto, tamanho) {
        if (!texto) return '';
        return texto.length > tamanho ? texto.slice(0, tamanho - 1) + '…' : texto;
    }

    function linhaBarra(rotulo, percentual, perigo) {
        const pct = Math.max(0, Math.min(100, percentual));
        return `
            <div class="bar-row">
                <span class="bar-label" title="${escaparHtml(rotulo)}">${escaparHtml(truncar(rotulo, 16))}</span>
                <div class="bar-track"><div class="bar-fill${perigo ? ' danger' : ''}" style="width:${pct}%;"></div></div>
                <span class="bar-value">${pct.toFixed(0)}%</span>
            </div>
        `;
    }

    try {
        const resp = await fetch(`${API_BASE_URL}/estatisticas`);
        if (!resp.ok) throw new Error(`status ${resp.status}`);
        const dados = await resp.json();

        if (!dados || !dados.indicadoresGerais || dados.indicadoresGerais.totalPartidas === 0) {
            mostrarApenas(vazio);
            return;
        }

        const ind = dados.indicadoresGerais;
        document.getElementById('kpi-total-partidas').innerText = ind.totalPartidas;
        document.getElementById('kpi-total-participantes').innerText = ind.totalParticipantes;
        document.getElementById('kpi-media-pontuacao').innerText = ind.mediaPontuacao.toFixed(1);
        document.getElementById('kpi-taxa-acerto').innerText = `${ind.taxaGeralAcertoPercentual.toFixed(0)}%`;
        document.getElementById('kpi-tempo-medio').innerText = `${ind.tempoMedioPartidasSegundos.toFixed(1)}s`;
        document.getElementById('kpi-autoavaliacao').innerText = `${ind.mediaAutoavaliacao.toFixed(1)} / 10`;

        // Comparacao autoavaliacao x desempenho real
        const comparacaoEl = document.getElementById('comparacao-autoavaliacao');
        const diferenca = ind.mediaNotaFinal - ind.mediaAutoavaliacao;
        let textoComparacao;
        if (Math.abs(diferenca) < 0.5) {
            textoComparacao = `Em média, a autoavaliação dos participantes (${ind.mediaAutoavaliacao.toFixed(1)}) está bem alinhada com o desempenho real (nota média ${ind.mediaNotaFinal.toFixed(1)}).`;
        } else if (diferenca > 0) {
            textoComparacao = `Em média, os participantes tiveram desempenho real (nota média ${ind.mediaNotaFinal.toFixed(1)}) melhor do que a própria autoavaliação (${ind.mediaAutoavaliacao.toFixed(1)}) sugeria.`;
        } else {
            textoComparacao = `Em média, os participantes se autoavaliaram (${ind.mediaAutoavaliacao.toFixed(1)}) acima do desempenho real obtido (nota média ${ind.mediaNotaFinal.toFixed(1)}).`;
        }
        comparacaoEl.innerText = textoComparacao;

        // Desempenho por nivel
        const graficoNiveis = document.getElementById('grafico-niveis');
        graficoNiveis.innerHTML = (dados.niveis || [])
            .map(n => linhaBarra(`${n.nivel} · ${n.nomeNivel}`, n.percentualAcerto, false))
            .join('') || '<p>Sem dados suficientes.</p>';

        // Desempenho por tema (ordenado do maior para o menor percentual)
        const graficoTemas = document.getElementById('grafico-temas');
        const temasOrdenados = [...(dados.temas || [])].sort((a, b) => b.percentualAcerto - a.percentualAcerto);
        graficoTemas.innerHTML = temasOrdenados
            .map(t => linhaBarra(t.tema, t.percentualAcerto, t.percentualAcerto < 50))
            .join('') || '<p>Sem dados suficientes.</p>';

        // Destaques
        const destaques = [];
        if (dados.perguntaMaisErrada) {
            destaques.push({
                titulo: 'Questão com maior índice de erro',
                texto: `"${truncar(dados.perguntaMaisErrada.texto, 90)}" — ${dados.perguntaMaisErrada.percentualErro.toFixed(0)}% de erro`
            });
        }
        if (dados.perguntaMaisAcertada) {
            destaques.push({
                titulo: 'Questão mais acertada',
                texto: `"${truncar(dados.perguntaMaisAcertada.texto, 90)}" — ${dados.perguntaMaisAcertada.percentualAcerto.toFixed(0)}% de acerto`
            });
        }
        if (dados.temaMaiorErro) {
            destaques.push({
                titulo: 'Tema com maior dificuldade',
                texto: `${dados.temaMaiorErro.tema} — ${dados.temaMaiorErro.percentualAcerto.toFixed(0)}% de acerto`
            });
        }
        if (dados.temaMaiorAcerto) {
            destaques.push({
                titulo: 'Tema com melhor desempenho',
                texto: `${dados.temaMaiorAcerto.tema} — ${dados.temaMaiorAcerto.percentualAcerto.toFixed(0)}% de acerto`
            });
        }

        document.getElementById('lista-destaques').innerHTML = destaques
            .map(d => `<li><strong>${escaparHtml(d.titulo)}</strong>${escaparHtml(d.texto)}</li>`)
            .join('') || '<li>Sem destaques suficientes ainda.</li>';

        mostrarApenas(conteudo);
    } catch (e) {
        console.error('Falha ao carregar estatísticas:', e);
        mostrarApenas(erro);
    }
});
