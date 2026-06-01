package br.com.fatecads.fatecads.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fatecads.fatecads.dto.ForgotPasswordForm;
import br.com.fatecads.fatecads.dto.ResetPasswordForm;
import br.com.fatecads.fatecads.service.PasswordResetRequestResult;
import br.com.fatecads.fatecads.service.PasswordResetService;
import br.com.fatecads.fatecads.service.UsuarioService;

@Controller
public class LoginController {

    private final PasswordResetService passwordResetService;
    private final UsuarioService usuarioService;

    public LoginController(PasswordResetService passwordResetService, UsuarioService usuarioService) {
        this.passwordResetService = passwordResetService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "logout", required = false) String logout,
            @RequestParam(name = "resetSuccess", required = false) String resetSuccess,
            Model model) {
        if (error != null) {
            model.addAttribute("error", "Login ou senha invalidos.");
        }
        if (logout != null) {
            model.addAttribute("message", "Sessao encerrada com sucesso.");
        }
        if (resetSuccess != null) {
            model.addAttribute("message", "Senha redefinida com sucesso. Faca login com a nova credencial.");
        }
        return "login";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        if (!model.containsAttribute("forgotPasswordForm")) {
            model.addAttribute("forgotPasswordForm", new ForgotPasswordForm());
        }
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String requestPasswordReset(
            @ModelAttribute ForgotPasswordForm forgotPasswordForm,
            Model model) {
        PasswordResetRequestResult result = passwordResetService.requestPasswordReset(forgotPasswordForm.getEmail());
        model.addAttribute("forgotPasswordForm", forgotPasswordForm);
        switch (result) {
            case SUCCESS -> model.addAttribute("message", "Email de recuperacao enviado com sucesso.");
            case ACCOUNT_NOT_FOUND -> model.addAttribute("error", "Conta nao cadastrada para o email informado.");
            case RATE_LIMITED -> model.addAttribute("error", "Aguarde um instante antes de solicitar um novo email.");
            case DELIVERY_FAILED -> model.addAttribute("error",
                    "Nao foi possivel enviar o email de recuperacao. Verifique a configuracao da conta de envio.");
            default -> model.addAttribute("error", "Nao foi possivel processar a solicitacao.");
        }
        return "auth/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword(
            @RequestParam(name = "token", required = false) String token,
            Model model) {
        boolean tokenValid = passwordResetService.isTokenValid(token);
        ResetPasswordForm form = new ResetPasswordForm();
        form.setToken(token);
        model.addAttribute("resetPasswordForm", form);
        model.addAttribute("tokenValid", tokenValid);
        if (!tokenValid) {
            model.addAttribute("error", "O link de redefinicao e invalido ou expirou.");
        }
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String updatePassword(
            @ModelAttribute ResetPasswordForm resetPasswordForm,
            Model model,
            RedirectAttributes redirectAttributes) {
        model.addAttribute("resetPasswordForm", resetPasswordForm);

        if (!passwordResetService.isTokenValid(resetPasswordForm.getToken())) {
            model.addAttribute("tokenValid", false);
            model.addAttribute("error", "O link de redefinicao e invalido ou expirou.");
            return "auth/reset-password";
        }

        if (resetPasswordForm.getPassword() == null
                || !resetPasswordForm.getPassword().equals(resetPasswordForm.getConfirmPassword())) {
            model.addAttribute("tokenValid", true);
            model.addAttribute("error", "A confirmacao de senha nao confere.");
            return "auth/reset-password";
        }

        if (!usuarioService.isPasswordStrong(resetPasswordForm.getPassword())) {
            model.addAttribute("tokenValid", true);
            model.addAttribute("error",
                    "A senha deve ter pelo menos 8 caracteres, com letras maiusculas, minusculas, numero e simbolo.");
            return "auth/reset-password";
        }

        passwordResetService.resetPassword(resetPasswordForm.getToken(), resetPasswordForm.getPassword());
        redirectAttributes.addAttribute("resetSuccess", "true");
        return "redirect:/login";
    }
}
