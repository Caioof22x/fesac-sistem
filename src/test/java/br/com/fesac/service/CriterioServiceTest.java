package br.com.fesac.service;

import br.com.fesac.dto.CriterioRequest;
import br.com.fesac.model.Criterio;
import br.com.fesac.repository.CriterioRepository;
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
class CriterioServiceTest {

    @Mock
    private CriterioRepository criterioRepository;

    @InjectMocks
    private CriterioService service;

    private Criterio criterio(Long id, String nome, Double peso) {
        Criterio criterio = new Criterio();
        criterio.setId(id);
        criterio.setNome(nome);
        criterio.setPesoMaximo(peso);
        return criterio;
    }

    @Test
    void deveListarTodosOsCriterios() {
        when(criterioRepository.findAll())
                .thenReturn(List.of(criterio(1L, "Criatividade", 10.0), criterio(2L, "Organizacao", 5.0)));

        List<Criterio> resultado = service.listar();

        assertEquals(2, resultado.size());
        assertEquals("Criatividade", resultado.get(0).getNome());
    }

    @Test
    void deveCadastrarCriterioRemovendoEspacosEGuardandoOPeso() {
        when(criterioRepository.existsByNomeIgnoreCase("Criatividade")).thenReturn(false);
        when(criterioRepository.save(any(Criterio.class))).thenAnswer(inv -> inv.getArgument(0));

        Criterio salvo = service.cadastrar(new CriterioRequest("  Criatividade  ", 10.0));

        assertEquals("Criatividade", salvo.getNome());
        assertEquals(10.0, salvo.getPesoMaximo(), 0.001);
    }

    @Test
    void naoDeveCadastrarCriterioComNomeDuplicado() {
        when(criterioRepository.existsByNomeIgnoreCase("Criatividade")).thenReturn(true);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.cadastrar(new CriterioRequest("Criatividade", 10.0)));

        assertEquals(409, erro.getStatusCode().value());
        verify(criterioRepository, never()).save(any(Criterio.class));
    }

    @Test
    void deveEditarNomeEPesoDoCriterio() {
        when(criterioRepository.findById(1L)).thenReturn(Optional.of(criterio(1L, "Criatividade", 10.0)));
        when(criterioRepository.existsByNomeIgnoreCaseAndIdNot("Originalidade", 1L)).thenReturn(false);
        when(criterioRepository.save(any(Criterio.class))).thenAnswer(inv -> inv.getArgument(0));

        Criterio editado = service.editar(1L, new CriterioRequest(" Originalidade ", 8.5));

        assertEquals("Originalidade", editado.getNome());
        assertEquals(8.5, editado.getPesoMaximo(), 0.001);
    }

    @Test
    void naoDeveEditarCriterioInexistente() {
        when(criterioRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.editar(99L, new CriterioRequest("Originalidade", 8.5)));

        assertEquals(404, erro.getStatusCode().value());
        verify(criterioRepository, never()).save(any(Criterio.class));
    }

    @Test
    void naoDeveEditarParaONomeDeOutroCriterio() {
        when(criterioRepository.findById(1L)).thenReturn(Optional.of(criterio(1L, "Criatividade", 10.0)));
        when(criterioRepository.existsByNomeIgnoreCaseAndIdNot("Organizacao", 1L)).thenReturn(true);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.editar(1L, new CriterioRequest("Organizacao", 10.0)));

        assertEquals(409, erro.getStatusCode().value());
        verify(criterioRepository, never()).save(any(Criterio.class));
    }

    @Test
    void deveExcluirCriterioExistente() {
        Criterio existente = criterio(1L, "Criatividade", 10.0);
        when(criterioRepository.findById(1L)).thenReturn(Optional.of(existente));

        service.excluir(1L);

        verify(criterioRepository).delete(existente);
    }

    @Test
    void naoDeveExcluirCriterioInexistente() {
        when(criterioRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.excluir(99L));

        assertEquals(404, erro.getStatusCode().value());
        verify(criterioRepository, never()).delete(any(Criterio.class));
    }
}
