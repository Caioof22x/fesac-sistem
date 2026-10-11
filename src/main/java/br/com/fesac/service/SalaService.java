package br.com.fesac.service;

import br.com.fesac.dto.SalaRequest;
import br.com.fesac.model.Sala;
import br.com.fesac.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaService {

    private final SalaRepository salaRepository;

    public List<Sala> listar() {
        return salaRepository.findAll();
    }

    @Transactional
    public Sala cadastrar(SalaRequest request) {
        String nome = request.nome().trim();
        if (salaRepository.existsByNomeIgnoreCase(nome)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma sala com esse nome");
        }
        Sala sala = new Sala();
        sala.setNome(nome);
        return salaRepository.save(sala);
    }

    @Transactional
    public Sala editar(Long id, SalaRequest request) {
        Sala sala = buscar(id);
        String nome = request.nome().trim();
        if (salaRepository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma sala com esse nome");
        }
        sala.setNome(nome);
        return salaRepository.save(sala);
    }

    @Transactional
    public void excluir(Long id) {
        salaRepository.delete(buscar(id));
    }

    private Sala buscar(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala não encontrada"));
    }
}
