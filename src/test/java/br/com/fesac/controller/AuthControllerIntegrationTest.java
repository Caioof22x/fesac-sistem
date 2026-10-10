package br.com.fesac.controller;

import br.com.fesac.model.Usuario;
import br.com.fesac.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    private static final String EMAIL = "prof@fesac.com";
    private static final String SENHA = "Senha@123";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void criarUsuarioDeTeste() {
        userRepository.deleteAll();
        Usuario usuario = new Usuario();
        usuario.setNome("Professor Teste");
        usuario.setEmail(EMAIL);
        usuario.setSenha(passwordEncoder.encode(SENHA)); 
        userRepository.save(usuario);
    }

    @AfterEach
    void limparBanco() {
        userRepository.deleteAll();
    }

    private String json(String email, String senha) {
        return String.format("{\"email\":\"%s\",\"senha\":\"%s\"}", email, senha);
    }

    @Test
    void loginComCredenciaisValidasRetorna200SemExporSenha() throws Exception {
        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(EMAIL, SENHA)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.nome").value("Professor Teste"))
            .andExpect(jsonPath("$.email").value(EMAIL))
            .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    void loginComSenhaIncorretaRetorna401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(EMAIL, "senhaErrada")))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    void loginComEmailNaoCadastradoRetorna401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json("naoexiste@fesac.com", SENHA)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    void loginComEmailMalFormatadoRetorna400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json("isso-nao-e-email", SENHA)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void loginComSenhaEmBrancoRetorna400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json(EMAIL, "")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void rotaProtegidaSemLoginRetorna401() throws Exception {
        mockMvc.perform(get("/api/qualquer-rota-protegida"))
            .andExpect(status().isUnauthorized());
    }
}
