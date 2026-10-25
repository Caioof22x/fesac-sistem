package br.com.fesac.controller;

import br.com.fesac.model.Sala;
import br.com.fesac.repository.SalaRepository;
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
class SalaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SalaRepository salaRepository;

    @BeforeEach
    void limparAntes() {
        salaRepository.deleteAll();
    }

    @AfterEach
    void limparDepois() {
        salaRepository.deleteAll();
    }

    private String json(String nome) {
        return String.format("{\"nome\":\"%s\"}", nome);
    }

    private Sala criarSala(String nome) {
        Sala sala = new Sala();
        sala.setNome(nome);
        return salaRepository.save(sala);
    }

    @Test
    void listarSemLoginRetorna401() throws Exception {
        mockMvc.perform(get("/api/salas"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void cadastrarSemLoginRetorna401() throws Exception {
        mockMvc.perform(post("/api/salas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json("Sala A")))
            .andExpect(status().isUnauthorized());
        assertEquals(0, salaRepository.count());
    }

    @Test
    @WithMockUser
    void listarRetornaAsSalasCadastradas() throws Exception {
        criarSala("Sala A");
        criarSala("Sala B");

        mockMvc.perform(get("/api/salas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser
    void cadastrarSalaValidaRetorna201() throws Exception {
        mockMvc.perform(post("/api/salas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json("Sala A")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.nome").value("Sala A"));
        assertEquals(1, salaRepository.count());
    }

    @Test
    @WithMockUser
    void cadastrarSalaSemNomeRetorna400() throws Exception {
        mockMvc.perform(post("/api/salas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json("")))
            .andExpect(status().isBadRequest());
        assertEquals(0, salaRepository.count());
    }

    @Test
    @WithMockUser
    void cadastrarSalaDuplicadaRetorna409() throws Exception {
        criarSala("Sala A");

        mockMvc.perform(post("/api/salas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json("sala a")))
            .andExpect(status().isConflict());
        assertEquals(1, salaRepository.count());
    }

    @Test
    @WithMockUser
    void editarSalaRetorna200ComONovoNome() throws Exception {
        Sala sala = criarSala("Sala A");

        mockMvc.perform(put("/api/salas/{id}", sala.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json("Sala Nova")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("Sala Nova"));
        assertEquals("Sala Nova", salaRepository.findById(sala.getId()).orElseThrow().getNome());
    }

    @Test
    @WithMockUser
    void editarSalaInexistenteRetorna404() throws Exception {
        mockMvc.perform(put("/api/salas/{id}", 9999)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json("Sala Nova")))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void excluirSalaRetorna204() throws Exception {
        Sala sala = criarSala("Sala A");

        mockMvc.perform(delete("/api/salas/{id}", sala.getId()))
            .andExpect(status().isNoContent());
        assertEquals(0, salaRepository.count());
    }

    @Test
    @WithMockUser
    void excluirSalaInexistenteRetorna404() throws Exception {
        mockMvc.perform(delete("/api/salas/{id}", 9999))
            .andExpect(status().isNotFound());
    }
}
