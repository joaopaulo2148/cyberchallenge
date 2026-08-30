package com.cyberchallenge.dto.dashboard;

import java.util.List;

public record DashboardDTO(
    IndicadoresGeraisDTO indicadoresGerais,

    List<PerguntaEstatisticaDTO> perguntas,
    PerguntaEstatisticaDTO perguntaMaisAcertada,
    PerguntaEstatisticaDTO perguntaMaisErrada,
    PerguntaEstatisticaDTO perguntaMaiorTempoMedio,
    PerguntaEstatisticaDTO perguntaMenorTempoMedio,

    List<TemaEstatisticaDTO> temas,
    TemaEstatisticaDTO temaMaiorAcerto,
    TemaEstatisticaDTO temaMaiorErro
) {}
