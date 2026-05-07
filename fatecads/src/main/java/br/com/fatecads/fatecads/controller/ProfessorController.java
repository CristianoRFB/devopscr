package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fatecads.fatecads.entity.Professor;
import br.com.fatecads.fatecads.service.DisciplinaService;
import br.com.fatecads.fatecads.service.ProfessorService;

@Controller
@RequestMapping("/professores")
public class ProfessorController {

	@Autowired
	private ProfessorService professorService;

    @Autowired
    private DisciplinaService disciplinaService;

	@PostMapping("/salvar")
	public String salvar(@ModelAttribute Professor professor) {
		professorService.save(professor);
		return "redirect:/professores/listar";
	}

	@GetMapping("/listar")
	public String listar(Model model) {
		model.addAttribute("professores", professorService.findAll());
		return "professor/listarProfessores";
	}

	@GetMapping("/criar")
	public String criarForm(Model model) {
		model.addAttribute("professor", new Professor());
        model.addAttribute("disciplinas", disciplinaService.findAll());
		return "professor/formularioProfessor";
	}

	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable Integer id) {
		professorService.deleteById(id);
		return "redirect:/professores/listar";
	}

	@GetMapping("/editar/{id}")
	public String editarForm(@PathVariable Integer id, Model model) {
		Professor professor = professorService.findById(id);
		if (professor != null) {
			model.addAttribute("professor", professor);
			model.addAttribute("disciplinas", disciplinaService.findAll());
			return "professor/formularioProfessor";
		}
		return "redirect:/professores/listar";
	}
}
