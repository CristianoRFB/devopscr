package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.service.UsuarioService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario, Model model) {
        try {
            usuarioService.save(usuario);
            model.addAttribute("message", "Usuario cadastrado com sucesso. Faca login para continuar.");
            return "login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("error", e.getMessage());
            return "usuario/formularioUsuario";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("error", e.getMessage());
            return "usuario/formularioUsuario";
        }
    }

	@GetMapping("/criar")
	public String criarForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/formularioUsuario";
	}
    
}
