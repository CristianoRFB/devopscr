package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fatecads.fatecads.service.AlunoService;

@Controller
@RequestMapping("/alunos")
public class AlunoController {
    
    // Injeção de dependência do serviço de alunos
    @Autowired
    private AlunoService alunoService;
}
