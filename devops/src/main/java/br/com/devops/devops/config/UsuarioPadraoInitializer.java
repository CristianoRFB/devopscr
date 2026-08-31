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
        // Verifica se o usuário padrão já existe
        if (usuarioRepository.findAll().isEmpty()) {
            // Usuário Admin
            Usuario usuarioAdmin = new Usuario();
            usuarioAdmin.setNomeUsuario("Administrador");
            usuarioAdmin.setEmailUsuario("admin@devops.com");
            usuarioAdmin.setLoginUsuario("admin");
            usuarioAdmin.setSenhaUsuario(passwordEncoder.encode("admin123"));
            usuarioAdmin.setRoleUsuario("ADMIN");
            usuarioRepository.save(usuarioAdmin);

            // Usuário Professor
            Usuario usuarioProfessor = new Usuario();
            usuarioProfessor.setNomeUsuario("João Silva");
            usuarioProfessor.setEmailUsuario("joao@devops.com");
            usuarioProfessor.setLoginUsuario("joao");
            usuarioProfessor.setSenhaUsuario(passwordEncoder.encode("joao123"));
            usuarioProfessor.setRoleUsuario("PROFESSOR");
            usuarioRepository.save(usuarioProfessor);

            // Usuário Secretaria
            Usuario usuarioSecretaria = new Usuario();
            usuarioSecretaria.setNomeUsuario("Maria Santos");
            usuarioSecretaria.setEmailUsuario("maria@devops.com");
            usuarioSecretaria.setLoginUsuario("maria");
            usuarioSecretaria.setSenhaUsuario(passwordEncoder.encode("maria123"));
            usuarioSecretaria.setRoleUsuario("SECRETARIA");
            usuarioRepository.save(usuarioSecretaria);

            System.out.println("✅ Usuários padrão criados com sucesso!");
            System.out.println("   - admin / admin123 (ADMIN)");
            System.out.println("   - joao / joao123 (PROFESSOR)");
            System.out.println("   - maria / maria123 (SECRETARIA)");
        }
    }
}

