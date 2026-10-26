package br.com.fesac.service;

import br.com.fesac.dto.NotaRequest;
import br.com.fesac.model.Criterio;
import br.com.fesac.model.Nota;
import br.com.fesac.model.Sala;
import br.com.fesac.model.Usuario;
import br.com.fesac.repository.CriterioRepository;
import br.com.fesac.repository.NotaRepository;
import br.com.fesac.repository.SalaRepository;
import br.com.fesac.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NotaService {

    private final NotaRepository notaRepository;
    private final SalaRepository salaRepository;
    private final CriterioRepository criterioRepository;
    private final UserRepository userRepository;

    @Transactional
    public Nota lancar(NotaRequest request, String emailProfessor) {
        Sala sala = salaRepository.findById(request.salaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala não encontrada"));
        Criterio criterio = criterioRepository.findById(request.criterioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Critério não encontrado"));
        Usuario professor = userRepository.findByEmail(emailProfessor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));

        if (request.valor() > criterio.getPesoMaximo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A nota não pode ser maior que o peso máximo do critério (" + criterio.getPesoMaximo() + ")");
        }

        Nota nota = new Nota();
        nota.setSala(sala);
        nota.setCriterio(criterio);
        nota.setUsuario(professor);
        nota.setValor(request.valor());
        return notaRepository.save(nota);
    }
}
