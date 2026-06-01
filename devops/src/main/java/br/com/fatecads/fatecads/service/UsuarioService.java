package br.com.fatecads.fatecads.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private static final String BCRYPT_PREFIX = "$2";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario save(Usuario usuario) {
        validateNewUser(usuario);
        usuario.setSenha(encodePasswordIfNeeded(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public void deleteById(Integer id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario update(Usuario usuario) {
        if (usuario.getIdUsuario() != null && usuarioRepository.existsById(usuario.getIdUsuario())) {
            usuario.setSenha(encodePasswordIfNeeded(usuario.getSenha()));
            return usuarioRepository.save(usuario);
        }
        return null;
    }

    public void updatePassword(Usuario usuario, String rawPassword) {
        usuario.setSenha(passwordEncoder.encode(rawPassword));
        usuarioRepository.save(usuario);
    }

    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecialChar = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }

    public boolean emailExists(String email) {
        return email != null && usuarioRepository.existsByEmailIgnoreCase(email.trim());
    }

    public boolean loginExists(String login) {
        return login != null && usuarioRepository.existsByLogin(login.trim());
    }

    private void validateNewUser(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Dados do usuario nao informados.");
        }
        if (emailExists(usuario.getEmail())) {
            throw new DataIntegrityViolationException("Ja existe uma conta cadastrada com este email.");
        }
        if (loginExists(usuario.getLogin())) {
            throw new DataIntegrityViolationException("Ja existe uma conta cadastrada com este login.");
        }
        if (!isPasswordStrong(usuario.getSenha())) {
            throw new IllegalArgumentException(
                    "A senha deve ter pelo menos 8 caracteres, com letras maiusculas, minusculas, numero e simbolo.");
        }
    }

    private String encodePasswordIfNeeded(String password) {
        if (password == null || password.isBlank()) {
            return password;
        }
        if (password.startsWith(BCRYPT_PREFIX)) {
            return password;
        }
        return passwordEncoder.encode(password);
    }
}
