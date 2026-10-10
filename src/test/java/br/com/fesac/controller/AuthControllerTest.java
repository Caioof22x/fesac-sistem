package br.com.fesac.controller;

import br.com.fesac.dto.LoginRequest;
import br.com.fesac.dto.UsuarioResponse;
import br.com.fesac.model.Usuario;
import br.com.fesac.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private SecurityContextRepository securityContextRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthController controller;

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    private Usuario usuarioPadrao() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Professor Teste");
        usuario.setEmail("prof@fesac.com");
        return usuario;
    }

    @Test
    void deveRetornar200ComDadosDoUsuarioQuandoCredenciaisSaoValidas() {
        Authentication autenticado =
                UsernamePasswordAuthenticationToken.authenticated("prof@fesac.com", null, List.of());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(autenticado);
        when(userRepository.findByEmail("prof@fesac.com")).thenReturn(Optional.of(usuarioPadrao()));

        ResponseEntity<?> resposta = controller.login(
                new LoginRequest("prof@fesac.com", "Senha@123"),
                new MockHttpServletRequest(), new MockHttpServletResponse());

        assertEquals(200, resposta.getStatusCode().value());
        UsuarioResponse corpo = (UsuarioResponse) resposta.getBody();
        assertEquals("prof@fesac.com", corpo.email());
        assertEquals("Professor Teste", corpo.nome());

        verify(securityContextRepository).saveContext(any(SecurityContext.class), any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    void deveRetornar401QuandoCredenciaisSaoInvalidas() {
        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        ResponseEntity<?> resposta = controller.login(
                new LoginRequest("prof@fesac.com", "senhaErrada"),
                new MockHttpServletRequest(), new MockHttpServletResponse());

        assertEquals(401, resposta.getStatusCode().value());
        assertEquals(Map.of("erro", "E-mail ou senha inválidos"), resposta.getBody());
  
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(securityContextRepository, userRepository);
    }

    @Test
    void deveInvalidarSessaoAnteriorAoLogar() {
        Authentication autenticado =
                UsernamePasswordAuthenticationToken.authenticated("prof@fesac.com", null, List.of());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(autenticado);
        when(userRepository.findByEmail("prof@fesac.com")).thenReturn(Optional.of(usuarioPadrao()));

        MockHttpServletRequest requisicao = new MockHttpServletRequest();
        MockHttpSession sessaoAntiga = (MockHttpSession) requisicao.getSession();

        controller.login(new LoginRequest("prof@fesac.com", "Senha@123"),
        requisicao, new MockHttpServletResponse());

        assertTrue(sessaoAntiga.isInvalid());
    }
}
