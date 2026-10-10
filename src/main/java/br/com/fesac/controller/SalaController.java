package br.com.fesac.controller;

import br.com.fesac.dto.SalaRequest;
import br.com.fesac.model.Sala;
import br.com.fesac.service.SalaService;
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
@RequestMapping("/api/salas")
@RequiredArgsConstructor
public class SalaController {

    private final SalaService salaService;

    @GetMapping
    public List<Sala> listar() {
        return salaService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sala cadastrar(@Valid @RequestBody SalaRequest request) {
        return salaService.cadastrar(request);
    }

    @PutMapping("/{id}")
    public Sala editar(@PathVariable Long id, @Valid @RequestBody SalaRequest request) {
        return salaService.editar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        salaService.excluir(id);
    }
}
