package br.com.fatecads.fatecads.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.fatecads.fatecads.service.UsuarioService;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/fatecads";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/cadastro")
    public String cadastro() {
        return "auth/cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String confirmarSenha,
            Model model) {

        if (!senha.equals(confirmarSenha)) {
            model.addAttribute("erro", "As senhas precisam ser iguais.");
            return "auth/cadastro";
        }

        if (senha.length() < 8) {
            model.addAttribute("erro", "A senha precisa ter pelo menos 8 caracteres.");
            return "auth/cadastro";
        }

        try {
            usuarioService.cadastrar(nome, email, senha);
        } catch (IllegalArgumentException exception) {
            model.addAttribute("erro", exception.getMessage());
            return "auth/cadastro";
        }

        return "redirect:/login?cadastro";
    }
}
