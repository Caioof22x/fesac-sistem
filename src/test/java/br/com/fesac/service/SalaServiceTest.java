package br.com.fesac.service;

import br.com.fesac.dto.SalaRequest;
import br.com.fesac.model.Sala;
import br.com.fesac.repository.SalaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalaServiceTest {

    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private SalaService service;

    private Sala sala(Long id, String nome) {
        Sala sala = new Sala();
        sala.setId(id);
        sala.setNome(nome);
        return sala;
    }

    @Test
    void deveListarTodasAsSalas() {
        when(salaRepository.findAll()).thenReturn(List.of(sala(1L, "Sala A"), sala(2L, "Sala B")));

        List<Sala> resultado = service.listar();

        assertEquals(2, resultado.size());
        assertEquals("Sala A", resultado.get(0).getNome());
    }

    @Test
    void deveCadastrarSalaRemovendoEspacosDoNome() {
        when(salaRepository.existsByNomeIgnoreCase("Sala A")).thenReturn(false);
        when(salaRepository.save(any(Sala.class))).thenAnswer(inv -> inv.getArgument(0));

        Sala salva = service.cadastrar(new SalaRequest("  Sala A  "));

        assertEquals("Sala A", salva.getNome());
        verify(salaRepository).save(any(Sala.class));
    }

    @Test
    void naoDeveCadastrarSalaComNomeDuplicado() {
        when(salaRepository.existsByNomeIgnoreCase("Sala A")).thenReturn(true);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
            () -> service.cadastrar(new SalaRequest("Sala A")));

        assertEquals(409, erro.getStatusCode().value());
        verify(salaRepository, never()).save(any(Sala.class));
    }

    @Test
    void deveEditarONomeDaSala() {
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala(1L, "Sala A")));
        when(salaRepository.existsByNomeIgnoreCaseAndIdNot("Sala Nova", 1L)).thenReturn(false);
        when(salaRepository.save(any(Sala.class))).thenAnswer(inv -> inv.getArgument(0));

        Sala editada = service.editar(1L, new SalaRequest(" Sala Nova "));

        assertEquals("Sala Nova", editada.getNome());
    }

    @Test
    void naoDeveEditarSalaInexistente() {
        when(salaRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
            () -> service.editar(99L, new SalaRequest("Sala Nova")));

        assertEquals(404, erro.getStatusCode().value());
        verify(salaRepository, never()).save(any(Sala.class));
    }

    @Test
    void naoDeveEditarParaONomeDeOutraSala() {
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala(1L, "Sala A")));
        when(salaRepository.existsByNomeIgnoreCaseAndIdNot("Sala B", 1L)).thenReturn(true);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
            () -> service.editar(1L, new SalaRequest("Sala B")));

        assertEquals(409, erro.getStatusCode().value());
        verify(salaRepository, never()).save(any(Sala.class));
    }

    @Test
    void deveExcluirSalaExistente() {
        Sala existente = sala(1L, "Sala A");
        when(salaRepository.findById(1L)).thenReturn(Optional.of(existente));

        service.excluir(1L);

        verify(salaRepository).delete(existente);
    }

    @Test
    void naoDeveExcluirSalaInexistente() {
        when(salaRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class, () -> service.excluir(99L));

        assertEquals(404, erro.getStatusCode().value());
        verify(salaRepository, never()).delete(any(Sala.class));
    }
}
