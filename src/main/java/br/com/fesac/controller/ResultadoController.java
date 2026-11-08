package br.com.fesac.controller;

import br.com.fesac.dto.ResultadoResponse;
import br.com.fesac.service.ResultadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/resultado")
@RequiredArgsConstructor
public class ResultadoController {

    private final ResultadoService resultadoService;

    @GetMapping
    public List<ResultadoResponse> resultadoFinal() {
        return resultadoService.calcularResultadoFinal();
    }
}
