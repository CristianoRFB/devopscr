package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.repository.ProfessorRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Professor;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    public Professor save(Professor professor) {
        return professorRepository.save(professor);
    }

    public List<Professor> findAll() {
        // Lógica para buscar todos os Professors no banco de dados
        return professorRepository.findAll();
    }

    public Professor findById(Integer id) {
        // Lógica para buscar um Professor por ID no banco de dados
        return professorRepository.findById(id).orElse(null);
    }

    public void deleteById(Integer id) {
        // Lógica para deletar um Professor por ID no banco de dados
        professorRepository.deleteById(id);
    }

    public Professor update(Professor professor) {
        // Lógica para atualizar um Professor no banco de dados
        if (professor.getIdProfessor() != null && professorRepository.existsById(professor.getIdProfessor())) {
            return professorRepository.save(professor);
        }
        return null; // Retorna null se o Professor não existir
    }
}
