package br.com.fesac.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record NotaRequest(
        @NotNull(message = "Informe a sala") Long salaId,
        @NotNull(message = "Informe o critério") Long criterioId,
        @NotNull(message = "Informe a nota") @PositiveOrZero(message = "A nota não pode ser negativa") Double valor) {
}
