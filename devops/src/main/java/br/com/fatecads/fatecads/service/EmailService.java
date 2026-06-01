package br.com.fatecads.fatecads.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private static final Pattern RESEND_ID_PATTERN = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"");

    private final HttpClient httpClient;
    private final SpringTemplateEngine templateEngine;
    private final String resendApiKey;
    private final String resendApiUrl;
    private final String fromEmail;
    private final String fromName;

    public EmailService(
            SpringTemplateEngine templateEngine,
            @Value("${resend.api-key:}") String resendApiKey,
            @Value("${resend.api-url:https://api.resend.com/emails}") String resendApiUrl,
            @Value("${resend.from-email:onboarding@resend.dev}") String fromEmail,
            @Value("${resend.from-name:FatecADS}") String fromName) {
        this.httpClient = HttpClient.newBuilder().build();
        this.templateEngine = templateEngine;
        this.resendApiKey = resendApiKey;
        this.resendApiUrl = resendApiUrl;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        LOGGER.info("Password reset link generated for {}", to);

        if (resendApiKey == null || resendApiKey.isBlank()) {
            LOGGER.warn("Resend API key is not configured. Password reset link kept only in application flow.");
            return;
        }

        Context context = new Context();
        context.setVariable("resetLink", resetLink);
        String html = templateEngine.process("mail/password-reset", context);
        String text = """
                Recebemos uma solicitacao para redefinir sua senha.

                Use o link abaixo para criar uma nova senha:
                %s

                Se voce nao solicitou esta alteracao, ignore este email.
                """.formatted(resetLink);

        String payload = """
                {
                  "from": "%s",
                  "to": ["%s"],
                  "subject": "Redefinicao de senha",
                  "html": "%s",
                  "text": "%s"
                }
                """.formatted(
                escapeJson("%s <%s>".formatted(fromName, fromEmail)),
                escapeJson(to),
                escapeJson(html),
                escapeJson(text));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(resendApiUrl))
                .header("Authorization", "Bearer " + resendApiKey)
                .header("Content-Type", "application/json")
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Resend returned status %s with body: %s"
                        .formatted(response.statusCode(), response.body()));
            }
            LOGGER.info("Password reset email accepted by Resend for {} with id {}", to, extractEmailId(response.body()));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Unable to send password reset email with Resend.", exception);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to send password reset email with Resend.", exception);
        }
    }

    private String extractEmailId(String responseBody) {
        Matcher matcher = RESEND_ID_PATTERN.matcher(responseBody);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "unknown";
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}
