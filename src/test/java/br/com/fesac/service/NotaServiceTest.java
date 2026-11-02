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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotaServiceTest {

    private static final String EMAIL = "prof@fesac.com";

    @Mock
    private NotaRepository notaRepository;
    @Mock
    private SalaRepository salaRepository;
    @Mock
    private CriterioRepository criterioRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotaService service;

    private Sala sala;
    private Criterio criterio;
    private Usuario professor;

    @BeforeEach
    void criarObjetos() {
        sala = new Sala();
        sala.setId(1L);
        sala.setNome("Sala A");

        criterio = new Criterio();
        criterio.setId(2L);
        criterio.setNome("Criatividade");
        criterio.setPesoMaximo(10.0);

        professor = new Usuario();
        professor.setId(3L);
        professor.setNome("Professor Teste");
        professor.setEmail(EMAIL);
    }

    private void prepararBuscasValidas() {
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
        when(criterioRepository.findById(2L)).thenReturn(Optional.of(criterio));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(professor));
    }

    @Test
    void deveLancarNotaValida() {
        prepararBuscasValidas();
        when(notaRepository.save(any(Nota.class))).thenAnswer(inv -> inv.getArgument(0));

        Nota nota = service.lancar(new NotaRequest(1L, 2L, 8.5), EMAIL);

        assertSame(sala, nota.getSala());
        assertSame(criterio, nota.getCriterio());
        assertEquals(8.5, nota.getValor(), 0.001);
    }

    @Test
    void deveRegistrarOProfessorQueLancouANota() {
        prepararBuscasValidas();
        when(notaRepository.save(any(Nota.class))).thenAnswer(inv -> inv.getArgument(0));

        service.lancar(new NotaRequest(1L, 2L, 8.5), EMAIL);

        ArgumentCaptor<Nota> captor = ArgumentCaptor.forClass(Nota.class);
        verify(notaRepository).save(captor.capture());
        assertSame(professor, captor.getValue().getUsuario());
    }

    @Test
    void devePermitirNotaIgualAoPesoMaximo() {
        prepararBuscasValidas();
        when(notaRepository.save(any(Nota.class))).thenAnswer(inv -> inv.getArgument(0));

        Nota nota = service.lancar(new NotaRequest(1L, 2L, 10.0), EMAIL);

        assertEquals(10.0, nota.getValor(), 0.001);
    }

    @Test
    void devePermitirNotaZero() {
        prepararBuscasValidas();
        when(notaRepository.save(any(Nota.class))).thenAnswer(inv -> inv.getArgument(0));

        Nota nota = service.lancar(new NotaRequest(1L, 2L, 0.0), EMAIL);

        assertEquals(0.0, nota.getValor(), 0.001);
    }

    @Test
    void naoDeveLancarNotaMaiorQueOPesoMaximo() {
        prepararBuscasValidas();

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
            () -> service.lancar(new NotaRequest(1L, 2L, 10.5), EMAIL));

        assertEquals(400, erro.getStatusCode().value());
        assertTrue(erro.getReason().contains("10.0"));
        verify(notaRepository, never()).save(any(Nota.class));
    }

    @Test
    void naoDeveLancarNotaQuandoASalaNaoExiste() {
        when(salaRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
            () -> service.lancar(new NotaRequest(99L, 2L, 8.0), EMAIL));

        assertEquals(404, erro.getStatusCode().value());
        verifyNoInteractions(criterioRepository, userRepository, notaRepository);
    }

    @Test
    void naoDeveLancarNotaQuandoOCriterioNaoExiste() {
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
        when(criterioRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
            () -> service.lancar(new NotaRequest(1L, 99L, 8.0), EMAIL));

        assertEquals(404, erro.getStatusCode().value());
        verifyNoInteractions(userRepository, notaRepository);
    }

    @Test
    void naoDeveLancarNotaQuandoOProfessorNaoExiste() {
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
        when(criterioRepository.findById(2L)).thenReturn(Optional.of(criterio));
        when(userRepository.findByEmail("fantasma@fesac.com")).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
            () -> service.lancar(new NotaRequest(1L, 2L, 8.0), "fantasma@fesac.com"));

        assertEquals(401, erro.getStatusCode().value());
        verifyNoInteractions(notaRepository);
    }
}
