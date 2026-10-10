package br.com.fesac.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SalaRequest(
        @NotBlank(message = "Informe o nome da sala") @Size(max = 100) String nome) {
}
