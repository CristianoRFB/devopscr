package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fatecads.fatecads.entity.Curso;
import br.com.fatecads.fatecads.service.CursoService;

@Controller
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Curso curso) {
        cursoService.save(curso);
        return "redirect:/cursos/listar";
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("cursos", cursoService.findAll());
        return "curso/listarCursos";
    }

    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("curso", new Curso());
        return "curso/formularioCurso";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        cursoService.deleteById(id);
        return "redirect:/cursos/listar";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        Curso curso = cursoService.findById(id);
        if (curso != null) {
            model.addAttribute("curso", curso);
            return "curso/formularioCurso";
        }
        return "redirect:/cursos/listar";
    }
}
