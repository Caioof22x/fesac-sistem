package br.com.fesac.service;

import br.com.fesac.dto.ResultadoResponse;
import br.com.fesac.model.Criterio;
import br.com.fesac.model.Nota;
import br.com.fesac.model.Sala;
import br.com.fesac.repository.CriterioRepository;
import br.com.fesac.repository.NotaRepository;
import br.com.fesac.repository.SalaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultadoServiceTest {

    @Mock
    private SalaRepository salaRepository;
    @Mock
    private CriterioRepository criterioRepository;
    @Mock
    private NotaRepository notaRepository;

    @InjectMocks
    private ResultadoService service;

    private Sala salaA;
    private Sala salaB;
    private Sala salaC;
    private Criterio criatividade;
    private Criterio organizacao;

    @BeforeEach
    void criarObjetos() {
        salaA = sala(1L, "Sala A");
        salaB = sala(2L, "Sala B");
        salaC = sala(3L, "Sala C");
        criatividade = criterio(10L, "Criatividade", 10.0);
        organizacao = criterio(20L, "Organização", 5.0);
    }

    private Sala sala(Long id, String nome) {
        Sala sala = new Sala();
        sala.setId(id);
        sala.setNome(nome);
        return sala;
    }

    private Criterio criterio(Long id, String nome, Double pesoMaximo) {
        Criterio criterio = new Criterio();
        criterio.setId(id);
        criterio.setNome(nome);
        criterio.setPesoMaximo(pesoMaximo);
        return criterio;
    }

    private Nota nota(Sala sala, Criterio criterio, Double valor) {
        Nota nota = new Nota();
        nota.setSala(sala);
        nota.setCriterio(criterio);
        nota.setValor(valor);
        return nota;
    }

    private void prepararDados(List<Sala> salas, List<Nota> notas) {
        when(salaRepository.findAll()).thenReturn(salas);
        when(criterioRepository.findAll()).thenReturn(List.of(criatividade, organizacao));
        when(notaRepository.findAll()).thenReturn(notas);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaSalas() {
        prepararDados(List.of(), List.of());

        assertTrue(service.calcularResultadoFinal().isEmpty());
    }

    @Test
    void deveSomarAsNotasDeCadaSala() {
        prepararDados(List.of(salaA), List.of(
                nota(salaA, criatividade, 8.0),
                nota(salaA, organizacao, 4.5)));

        List<ResultadoResponse> ranking = service.calcularResultadoFinal();

        assertEquals(12.5, ranking.get(0).pontuacao(), 0.001);
    }

    @Test
    void deveOrdenarDaMaiorParaAMenorPontuacao() {
        prepararDados(List.of(salaA, salaB, salaC), List.of(
                nota(salaA, criatividade, 5.0),
                nota(salaB, criatividade, 9.0),
                nota(salaC, criatividade, 7.0)));

        List<ResultadoResponse> ranking = service.calcularResultadoFinal();

        assertEquals("Sala B", ranking.get(0).sala());
        assertEquals("Sala C", ranking.get(1).sala());
        assertEquals("Sala A", ranking.get(2).sala());
        assertEquals(1, ranking.get(0).posicao());
        assertEquals(3, ranking.get(2).posicao());
    }

    @Test
    void deveColocarSalaSemNotaComZeroPontos() {
        prepararDados(List.of(salaA, salaB), List.of(nota(salaA, criatividade, 6.0)));

        List<ResultadoResponse> ranking = service.calcularResultadoFinal();

        assertEquals("Sala B", ranking.get(1).sala());
        assertEquals(0.0, ranking.get(1).pontuacao(), 0.001);
    }

    @Test
    void deveDesempatarPeloCriterioDeMaiorPeso() {
        prepararDados(List.of(salaA, salaB), List.of(
                nota(salaA, criatividade, 6.0),
                nota(salaA, organizacao, 4.0),
                nota(salaB, criatividade, 8.0),
                nota(salaB, organizacao, 2.0)));

        List<ResultadoResponse> ranking = service.calcularResultadoFinal();

        assertEquals("Sala B", ranking.get(0).sala());
        assertEquals(1, ranking.get(0).posicao());
        assertEquals(2, ranking.get(1).posicao());
    }

    @Test
    void deveManterMesmaPosicaoQuandoEmpataTudo() {
        prepararDados(List.of(salaA, salaB, salaC), List.of(
                nota(salaA, criatividade, 7.0),
                nota(salaB, criatividade, 7.0),
                nota(salaC, criatividade, 3.0)));

        List<ResultadoResponse> ranking = service.calcularResultadoFinal();

        assertEquals(1, ranking.get(0).posicao());
        assertEquals(1, ranking.get(1).posicao());
        assertEquals(3, ranking.get(2).posicao());
    }

    @Test
    void deveEmpatarTodasQuandoNaoHaNotas() {
        prepararDados(List.of(salaA, salaB), List.of());

        List<ResultadoResponse> ranking = service.calcularResultadoFinal();

        assertEquals(1, ranking.get(0).posicao());
        assertEquals(1, ranking.get(1).posicao());
    }
}
