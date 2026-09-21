package br.com.devops.devops.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.devops.devops.entity.Usuario;
import br.com.devops.devops.repository.UsuarioRepository;

@Component
public class UsuarioPadraoInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        criarOuAtualizarUsuarioPadrao("admin", "Administrador", "admin@devops.com", "admin123", "ADMIN");
        criarOuAtualizarUsuarioPadrao("joao", "Joao Silva", "joao@devops.com", "joao123", "USER");
        normalizarRolesAntigas();

        System.out.println("Usuarios padrao conferidos:");
        System.out.println("   - admin / admin123 (ADMIN)");
        System.out.println("   - joao / joao123 (USER)");
    }

    private void criarOuAtualizarUsuarioPadrao(String login, String nome, String email, String senha, String role) {
        Usuario usuario = usuarioRepository.findByLoginUsuario(login).orElseGet(Usuario::new);
        usuario.setNomeUsuario(nome);
        usuario.setEmailUsuario(email);
        usuario.setLoginUsuario(login);
        usuario.setRoleUsuario(role);

        if (usuario.getIdUsuario() == null) {
            usuario.setSenhaUsuario(passwordEncoder.encode(senha));
        }

        usuarioRepository.save(usuario);
    }

    private void normalizarRolesAntigas() {
        usuarioRepository.findAll().stream()
                .filter(usuario -> !"ADMIN".equals(usuario.getRoleUsuario()) && !"USER".equals(usuario.getRoleUsuario()))
                .forEach(usuario -> {
                    usuario.setRoleUsuario("USER");
                    usuarioRepository.save(usuario);
                });
    }
}

