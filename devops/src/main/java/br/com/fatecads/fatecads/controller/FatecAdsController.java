package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.fatecads.fatecads.repository.AlunoRepository;
import br.com.fatecads.fatecads.repository.CursoRepository;
import br.com.fatecads.fatecads.repository.DisciplinaRepository;
import br.com.fatecads.fatecads.repository.ProfessorRepository;
import br.com.fatecads.fatecads.repository.UsuarioRepository;

@Controller
@RequestMapping("/fatecads")
public class FatecAdsController {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @GetMapping
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalAlunos", alunoRepository.count());
        model.addAttribute("totalCursos", cursoRepository.count());
        model.addAttribute("totalDisciplinas", disciplinaRepository.count());
        model.addAttribute("totalProfessores", professorRepository.count());
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        return "dashboard";
    }
    
}
