package br.com.fesac.controller;

import br.com.fesac.dto.CriterioRequest;
import br.com.fesac.model.Criterio;
import br.com.fesac.service.CriterioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/criterios")
@RequiredArgsConstructor
public class CriterioController {

    private final CriterioService criterioService;

    @GetMapping
    public List<Criterio> listar() {
        return criterioService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Criterio cadastrar(@Valid @RequestBody CriterioRequest request) {
        return criterioService.cadastrar(request);
    }

    @PutMapping("/{id}")
    public Criterio editar(@PathVariable Long id, @Valid @RequestBody CriterioRequest request) {
        return criterioService.editar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        criterioService.excluir(id);
    }
}
