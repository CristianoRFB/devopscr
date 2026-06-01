package br.com.fatecads.fatecads;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.fatecads.fatecads.entity.PasswordResetToken;
import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.PasswordResetTokenRepository;
import br.com.fatecads.fatecads.repository.UsuarioRepository;
import br.com.fatecads.fatecads.service.EmailService;
import br.com.fatecads.fatecads.service.PasswordResetService;
import br.com.fatecads.fatecads.service.UsuarioService;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        passwordResetTokenRepository.deleteAll();
        usuarioRepository.deleteAll();
        reset(emailService);
    }

    @Test
    void shouldRequestResetForKnownEmail() throws Exception {
        createUser("ana@example.com", "Senha@123");

        mockMvc.perform(post("/forgot-password")
                        .with(csrf())
                        .param("email", "ana@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Email de recuperacao enviado com sucesso.")));

        assertThat(passwordResetTokenRepository.count()).isEqualTo(1);
        verify(emailService).sendPasswordResetEmail(eq("ana@example.com"), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void shouldShowSpecificMessageForUnknownEmail() throws Exception {
        mockMvc.perform(post("/forgot-password")
                        .with(csrf())
                        .param("email", "desconhecido@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Conta nao cadastrada para o email informado.")));

        assertThat(passwordResetTokenRepository.count()).isZero();
        verify(emailService, never()).sendPasswordResetEmail(eq("desconhecido@example.com"),
                org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void shouldResetPasswordAndInvalidateTokenAfterUse() throws Exception {
        Usuario usuario = createUser("bia@example.com", "Senha@123");
        String rawToken = requestToken("bia@example.com");

                mockMvc.perform(post("/reset-password")
                        .with(csrf())
                        .param("token", rawToken)
                        .param("password", "NovaSenha@123")
                        .param("confirmPassword", "NovaSenha@123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?resetSuccess=true"));

        Usuario updatedUser = usuarioRepository.findById(usuario.getIdUsuario()).orElseThrow();
        assertThat(passwordEncoder.matches("NovaSenha@123", updatedUser.getSenha())).isTrue();
        assertThat(passwordResetService.isTokenValid(rawToken)).isFalse();

        Optional<PasswordResetToken> token = passwordResetTokenRepository.findFirstByUsuarioOrderByCreatedAtDesc(updatedUser);
        assertThat(token).isPresent();
        assertThat(token.get().getUsedAt()).isNotNull();
    }

    @Test
    void shouldRejectWeakPassword() throws Exception {
        Usuario usuario = createUser("clara@example.com", "Senha@123");
        String rawToken = requestToken("clara@example.com");

        mockMvc.perform(post("/reset-password")
                        .with(csrf())
                        .param("token", rawToken)
                        .param("password", "fraca")
                        .param("confirmPassword", "fraca"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "A senha deve ter pelo menos 8 caracteres")));

        Usuario sameUser = usuarioRepository.findById(usuario.getIdUsuario()).orElseThrow();
        assertThat(passwordEncoder.matches("Senha@123", sameUser.getSenha())).isTrue();
        assertThat(passwordResetService.isTokenValid(rawToken)).isTrue();
    }

    @Test
    void shouldRejectTokenReuse() throws Exception {
        createUser("dora@example.com", "Senha@123");
        String rawToken = requestToken("dora@example.com");

        mockMvc.perform(post("/reset-password")
                        .with(csrf())
                        .param("token", rawToken)
                        .param("password", "NovaSenha@123")
                        .param("confirmPassword", "NovaSenha@123"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/reset-password")
                        .with(csrf())
                        .param("token", rawToken)
                        .param("password", "OutraSenha@123")
                        .param("confirmPassword", "OutraSenha@123"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "O link de redefinicao e invalido ou expirou.")));
    }

    private Usuario createUser(String email, String senha) {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuario Teste");
        usuario.setEmail(email);
        usuario.setLogin(email.split("@")[0]);
        usuario.setSenha(senha);
        usuario.setRole("ROLE_USER");
        return usuarioService.save(usuario);
    }

    private String requestToken(String email) {
        passwordResetService.requestPasswordReset(email);

        ArgumentCaptor<String> linkCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendPasswordResetEmail(eq(email), linkCaptor.capture());
        String resetLink = linkCaptor.getValue();
        return resetLink.substring(resetLink.indexOf("token=") + 6);
    }
}
