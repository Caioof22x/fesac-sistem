package br.com.fesac.controller;

import br.com.fesac.dto.NotaRequest;
import br.com.fesac.dto.NotaResponse;
import br.com.fesac.service.NotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notas")
@RequiredArgsConstructor
public class NotaController {

    private final NotaService notaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotaResponse lancar(@Valid @RequestBody NotaRequest request, Authentication authentication) {
        return NotaResponse.de(notaService.lancar(request, authentication.getName()));
    }
}
