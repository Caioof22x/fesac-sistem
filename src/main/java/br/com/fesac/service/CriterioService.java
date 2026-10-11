package br.com.fesac.service;

import br.com.fesac.dto.CriterioRequest;
import br.com.fesac.model.Criterio;
import br.com.fesac.repository.CriterioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CriterioService {

    private final CriterioRepository criterioRepository;

    public List<Criterio> listar() {
        return criterioRepository.findAll();
    }

    @Transactional
    public Criterio cadastrar(CriterioRequest request) {
        String nome = request.nome().trim();
        if (criterioRepository.existsByNomeIgnoreCase(nome)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um critério com esse nome");
        }
        Criterio criterio = new Criterio();
        criterio.setNome(nome);
        criterio.setPesoMaximo(request.pesoMaximo());
        return criterioRepository.save(criterio);
    }

    @Transactional
    public Criterio editar(Long id, CriterioRequest request) {
        Criterio criterio = buscar(id);
        String nome = request.nome().trim();
        if (criterioRepository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um critério com esse nome");
        }
        criterio.setNome(nome);
        criterio.setPesoMaximo(request.pesoMaximo());
        return criterioRepository.save(criterio);
    }

    @Transactional
    public void excluir(Long id) {
        criterioRepository.delete(buscar(id));
    }

    private Criterio buscar(Long id) {
        return criterioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Critério não encontrado"));
    }
}
