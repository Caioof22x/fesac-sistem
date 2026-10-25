package br.com.fesac.controller;

import br.com.fesac.model.Criterio;
import br.com.fesac.repository.CriterioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CriterioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CriterioRepository criterioRepository;

    @BeforeEach
    void limparAntes() {
        criterioRepository.deleteAll();
    }

    @AfterEach
    void limparDepois() {
        criterioRepository.deleteAll();
    }

    private String json(String nome, String peso) {
        return String.format("{\"nome\":\"%s\",\"pesoMaximo\":%s}", nome, peso);
    }

    private Criterio criarCriterio(String nome, double peso) {
        Criterio criterio = new Criterio();
        criterio.setNome(nome);
        criterio.setPesoMaximo(peso);
        return criterioRepository.save(criterio);
    }

    @Test
    void listarSemLoginRetorna401() throws Exception {
        mockMvc.perform(get("/api/criterios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void listarRetornaOsCriteriosCadastrados() throws Exception {
        criarCriterio("Criatividade", 10.0);
        criarCriterio("Organizacao", 5.0);

        mockMvc.perform(get("/api/criterios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser
    void cadastrarCriterioValidoRetorna201() throws Exception {
        mockMvc.perform(post("/api/criterios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Criatividade", "10.0")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Criatividade"))
                .andExpect(jsonPath("$.pesoMaximo").value(10.0));
        assertEquals(1, criterioRepository.count());
    }

    @Test
    @WithMockUser
    void cadastrarCriterioComPesoZeroRetorna400() throws Exception {
        mockMvc.perform(post("/api/criterios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Criatividade", "0")))
                .andExpect(status().isBadRequest());
        assertEquals(0, criterioRepository.count());
    }

    @Test
    @WithMockUser
    void cadastrarCriterioComPesoNegativoRetorna400() throws Exception {
        mockMvc.perform(post("/api/criterios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Criatividade", "-3")))
                .andExpect(status().isBadRequest());
        assertEquals(0, criterioRepository.count());
    }

    @Test
    @WithMockUser
    void cadastrarCriterioSemPesoRetorna400() throws Exception {
        mockMvc.perform(post("/api/criterios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Criatividade\"}"))
                .andExpect(status().isBadRequest());
        assertEquals(0, criterioRepository.count());
    }

    @Test
    @WithMockUser
    void cadastrarCriterioSemNomeRetorna400() throws Exception {
        mockMvc.perform(post("/api/criterios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json("", "10.0")))
            .andExpect(status().isBadRequest());
        assertEquals(0, criterioRepository.count());
    }

    @Test
    @WithMockUser
    void cadastrarCriterioDuplicadoRetorna409() throws Exception {
        criarCriterio("Criatividade", 10.0);

        mockMvc.perform(post("/api/criterios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json("criatividade", "7.0")))
            .andExpect(status().isConflict());
        assertEquals(1, criterioRepository.count());
    }

    @Test
    @WithMockUser
    void editarCriterioRetorna200ComOsNovosDados() throws Exception {
        Criterio criterio = criarCriterio("Criatividade", 10.0);

        mockMvc.perform(put("/api/criterios/{id}", criterio.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json("Originalidade", "8.5")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("Originalidade"))
            .andExpect(jsonPath("$.pesoMaximo").value(8.5));
        Criterio salvo = criterioRepository.findById(criterio.getId()).orElseThrow();
        assertEquals("Originalidade", salvo.getNome());
        assertEquals(8.5, salvo.getPesoMaximo(), 0.001);
    }

    @Test
    @WithMockUser
    void editarCriterioInexistenteRetorna404() throws Exception {
        mockMvc.perform(put("/api/criterios/{id}", 9999)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json("Originalidade", "8.5")))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void excluirCriterioRetorna204() throws Exception {
        Criterio criterio = criarCriterio("Criatividade", 10.0);

        mockMvc.perform(delete("/api/criterios/{id}", criterio.getId()))
            .andExpect(status().isNoContent());
        assertEquals(0, criterioRepository.count());
    }

    @Test
    @WithMockUser
    void excluirCriterioInexistenteRetorna404() throws Exception {
        mockMvc.perform(delete("/api/criterios/{id}", 9999))
            .andExpect(status().isNotFound());
    }
}
