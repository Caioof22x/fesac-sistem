package br.com.fesac.dto;

import br.com.fesac.model.Nota;

public record NotaResponse(Long id, Long salaId, String sala, Long criterioId, String criterio,
                           Double valor, String professor) {

    public static NotaResponse de(Nota nota) {
        return new NotaResponse(nota.getId(),
                nota.getSala().getId(), nota.getSala().getNome(),
                nota.getCriterio().getId(), nota.getCriterio().getNome(),
                nota.getValor(), nota.getUsuario().getNome());
    }
}
