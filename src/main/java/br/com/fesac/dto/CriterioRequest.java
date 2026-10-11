package br.com.fesac.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CriterioRequest(
        @NotBlank(message = "Informe o nome do critério") @Size(max = 100) String nome,
        @NotNull(message = "Informe o peso máximo") @Positive(message = "O peso máximo deve ser maior que zero") Double pesoMaximo) {
}
