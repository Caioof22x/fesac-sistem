package br.com.fesac.service;

import br.com.fesac.model.Usuario;
import br.com.fesac.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UsuarioDetailsService service;

    @Test
    void deveCarregarUsuarioQuandoEmailExiste() {
        Usuario usuario = new Usuario();
        usuario.setNome("Professor Teste");
        usuario.setEmail("prof@fesac.com");
        usuario.setSenha("Senha@123");
        when(userRepository.findByEmail("prof@fesac.com")).thenReturn(Optional.of(usuario));

        UserDetails detalhes = service.loadUserByUsername("prof@fesac.com");

        assertEquals("prof@fesac.com", detalhes.getUsername());
        assertEquals("Senha@123", detalhes.getPassword());
    }

    @Test
    void deveLancarExcecaoQuandoEmailNaoExiste() {
        when(userRepository.findByEmail("naoexiste@fesac.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("naoexiste@fesac.com"));
    }
}
