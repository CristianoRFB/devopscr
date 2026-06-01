package br.com.fatecads.fatecads.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fatecads.fatecads.entity.PasswordResetToken;
import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.PasswordResetTokenRepository;
import br.com.fatecads.fatecads.repository.UsuarioRepository;

@Service
public class PasswordResetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetService.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final EmailService emailService;
    private final int expirationMinutes;
    private final int cooldownSeconds;
    private final String appBaseUrl;

    public PasswordResetService(
            PasswordResetTokenRepository passwordResetTokenRepository,
            UsuarioRepository usuarioRepository,
            UsuarioService usuarioService,
            EmailService emailService,
            @Value("${app.security.password-reset.expiration-minutes:30}") int expirationMinutes,
            @Value("${app.security.password-reset.cooldown-seconds:60}") int cooldownSeconds,
            @Value("${app.base-url:http://localhost:8080}") String appBaseUrl) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.emailService = emailService;
        this.expirationMinutes = expirationMinutes;
        this.cooldownSeconds = cooldownSeconds;
        this.appBaseUrl = appBaseUrl;
    }

    @Transactional
    public PasswordResetRequestResult requestPasswordReset(String email) {
        if (email == null || email.isBlank()) {
            return PasswordResetRequestResult.ACCOUNT_NOT_FOUND;
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailIgnoreCase(email.trim());
        if (usuarioOpt.isEmpty()) {
            return PasswordResetRequestResult.ACCOUNT_NOT_FOUND;
        }

        Usuario usuario = usuarioOpt.get();
        LocalDateTime now = LocalDateTime.now();
        Optional<PasswordResetToken> latestToken = passwordResetTokenRepository.findFirstByUsuarioOrderByCreatedAtDesc(usuario);
        if (latestToken.isPresent() && latestToken.get().getCreatedAt().plusSeconds(cooldownSeconds).isAfter(now)) {
            return PasswordResetRequestResult.RATE_LIMITED;
        }

        invalidateActiveTokens(usuario, now);

        String rawToken = generateSecureToken();
        PasswordResetToken token = new PasswordResetToken();
        token.setUsuario(usuario);
        token.setTokenHash(hashToken(rawToken));
        token.setCreatedAt(now);
        token.setExpiresAt(now.plusMinutes(expirationMinutes));
        passwordResetTokenRepository.save(token);

        String resetLink = appBaseUrl + "/reset-password?token=" + rawToken;
        try {
            emailService.sendPasswordResetEmail(usuario.getEmail(), resetLink);
            return PasswordResetRequestResult.SUCCESS;
        } catch (RuntimeException exception) {
            LOGGER.warn("Unable to send password reset email to {}", usuario.getEmail(), exception);
            return PasswordResetRequestResult.DELIVERY_FAILED;
        }
    }

    @Transactional(readOnly = true)
    public boolean isTokenValid(String rawToken) {
        return findValidToken(rawToken).isPresent();
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        PasswordResetToken token = findValidToken(rawToken)
                .orElseThrow(() -> new IllegalArgumentException("Token de redefinicao invalido ou expirado."));

        usuarioService.updatePassword(token.getUsuario(), newPassword);

        LocalDateTime now = LocalDateTime.now();
        token.setUsedAt(now);
        invalidateOtherActiveTokens(token.getUsuario(), token, now);
        passwordResetTokenRepository.save(token);
    }

    @Transactional(readOnly = true)
    public Optional<PasswordResetToken> findValidToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return Optional.empty();
        }

        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByTokenHash(hashToken(rawToken));
        if (tokenOpt.isEmpty()) {
            return Optional.empty();
        }

        PasswordResetToken token = tokenOpt.get();
        if (token.isUsed() || token.isExpired(LocalDateTime.now())) {
            return Optional.empty();
        }
        return Optional.of(token);
    }

    private void invalidateActiveTokens(Usuario usuario, LocalDateTime now) {
        List<PasswordResetToken> activeTokens = passwordResetTokenRepository
                .findByUsuarioAndUsedAtIsNullAndExpiresAtAfter(usuario, now);
        for (PasswordResetToken activeToken : activeTokens) {
            activeToken.setUsedAt(now);
        }
        passwordResetTokenRepository.saveAll(activeTokens);
    }

    private void invalidateOtherActiveTokens(Usuario usuario, PasswordResetToken currentToken, LocalDateTime now) {
        List<PasswordResetToken> activeTokens = passwordResetTokenRepository
                .findByUsuarioAndUsedAtIsNullAndExpiresAtAfter(usuario, now);
        for (PasswordResetToken activeToken : activeTokens) {
            if (!activeToken.getId().equals(currentToken.getId())) {
                activeToken.setUsedAt(now);
            }
        }
        passwordResetTokenRepository.saveAll(activeTokens);
    }

    private String generateSecureToken() {
        byte[] buffer = new byte[32];
        SECURE_RANDOM.nextBytes(buffer);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 indisponivel para hashing de token.", exception);
        }
    }
}
