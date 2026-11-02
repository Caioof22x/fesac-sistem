package br.com.fesac.controller;

import br.com.fesac.model.Criterio;
import br.com.fesac.model.Nota;
import br.com.fesac.model.Sala;
import br.com.fesac.model.Usuario;
import br.com.fesac.repository.CriterioRepository;
import br.com.fesac.repository.NotaRepository;
import br.com.fesac.repository.SalaRepository;
import br.com.fesac.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NotaControllerIntegrationTest {

    private static final String EMAIL = "avaliador@fesac.com";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private NotaRepository notaRepository;
    @Autowired
    private SalaRepository salaRepository;
    @Autowired
    private CriterioRepository criterioRepository;
    @Autowired
    private UserRepository userRepository;

    private Sala sala;
    private Criterio criterio;

    @BeforeEach
    void prepararDados() {
        limparBanco();

        Usuario professor = new Usuario();
        professor.setNome("Professor Teste");
        professor.setEmail(EMAIL);
        professor.setSenha("$2a$10$hashqualquer");
        userRepository.save(professor);

        sala = new Sala();
        sala.setNome("Sala A");
        sala = salaRepository.save(sala);

        criterio = new Criterio();
        criterio.setNome("Criatividade");
        criterio.setPesoMaximo(10.0);
        criterio = criterioRepository.save(criterio);
    }

    @AfterEach
    void limparDepois() {
        limparBanco();
    }

    private void limparBanco() {
        notaRepository.deleteAll();
        salaRepository.deleteAll();
        criterioRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String json(Long salaId, Long criterioId, String valor) {
        return String.format("{\"salaId\":%d,\"criterioId\":%d,\"valor\":%s}", salaId, criterioId, valor);
    }

    @Test
    void lancarSemLoginRetorna401() throws Exception {
        mockMvc.perform(post("/api/notas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(sala.getId(), criterio.getId(), "8.0")))
            .andExpect(status().isUnauthorized());
        assertEquals(0, notaRepository.count());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void lancarNotaValidaRetorna201ComOsDados() throws Exception {
        mockMvc.perform(post("/api/notas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(sala.getId(), criterio.getId(), "8.5")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.sala").value("Sala A"))
            .andExpect(jsonPath("$.criterio").value("Criatividade"))
            .andExpect(jsonPath("$.valor").value(8.5))
            .andExpect(jsonPath("$.professor").value("Professor Teste"));
        assertEquals(1, notaRepository.count());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void notaLancadaFicaRegistradaComOProfessorLogado() throws Exception {
        mockMvc.perform(post("/api/notas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(sala.getId(), criterio.getId(), "7.0")))
            .andExpect(status().isCreated());

        List<Nota> notas = notaRepository.findAll();
        assertEquals(1, notas.size());
        assertEquals(EMAIL, notas.get(0).getUsuario().getEmail());
        assertEquals(sala.getId(), notas.get(0).getSala().getId());
        assertEquals(criterio.getId(), notas.get(0).getCriterio().getId());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void notaIgualAoPesoMaximoRetorna201() throws Exception {
        mockMvc.perform(post("/api/notas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(sala.getId(), criterio.getId(), "10.0")))
            .andExpect(status().isCreated());
        assertEquals(1, notaRepository.count());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void notaMaiorQueOPesoMaximoRetorna400() throws Exception {
        mockMvc.perform(post("/api/notas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(sala.getId(), criterio.getId(), "10.5")))
            .andExpect(status().isBadRequest());
        assertEquals(0, notaRepository.count());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void notaNegativaRetorna400() throws Exception {
        mockMvc.perform(post("/api/notas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(sala.getId(), criterio.getId(), "-1")))
            .andExpect(status().isBadRequest());
        assertEquals(0, notaRepository.count());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void notaSemValorRetorna400() throws Exception {
        String corpo = String.format("{\"salaId\":%d,\"criterioId\":%d}", sala.getId(), criterio.getId());

        mockMvc.perform(post("/api/notas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(corpo))
            .andExpect(status().isBadRequest());
        assertEquals(0, notaRepository.count());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void notaSemSalaRetorna400() throws Exception {
        String corpo = String.format("{\"criterioId\":%d,\"valor\":8.0}", criterio.getId());

        mockMvc.perform(post("/api/notas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(corpo))
            .andExpect(status().isBadRequest());
        assertEquals(0, notaRepository.count());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void notaSemCriterioRetorna400() throws Exception {
        String corpo = String.format("{\"salaId\":%d,\"valor\":8.0}", sala.getId());

        mockMvc.perform(post("/api/notas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(corpo))
            .andExpect(status().isBadRequest());
        assertEquals(0, notaRepository.count());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void salaInexistenteRetorna404() throws Exception {
        mockMvc.perform(post("/api/notas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(9999L, criterio.getId(), "8.0")))
            .andExpect(status().isNotFound());
        assertEquals(0, notaRepository.count());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void criterioInexistenteRetorna404() throws Exception {
        mockMvc.perform(post("/api/notas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(sala.getId(), 9999L, "8.0")))
            .andExpect(status().isNotFound());
        assertEquals(0, notaRepository.count());
    }

    @Test
    @WithMockUser(username = "fantasma@fesac.com")
    void professorLogadoMasNaoCadastradoRetorna401() throws Exception {
        mockMvc.perform(post("/api/notas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(sala.getId(), criterio.getId(), "8.0")))
            .andExpect(status().isUnauthorized());
        assertEquals(0, notaRepository.count());
    }
}
