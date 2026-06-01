package br.com.fatecads.fatecads.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fatecads.fatecads.entity.PasswordResetToken;
import br.com.fatecads.fatecads.entity.Usuario;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    Optional<PasswordResetToken> findFirstByUsuarioOrderByCreatedAtDesc(Usuario usuario);

    List<PasswordResetToken> findByUsuarioAndUsedAtIsNullAndExpiresAtAfter(Usuario usuario, LocalDateTime referenceTime);
}
