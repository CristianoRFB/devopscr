package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.repository.DisciplinaRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Disciplina;

@Service
public class DisciplinaService {

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    public Disciplina save(Disciplina disciplina) {
        return disciplinaRepository.save(disciplina);
    }

    public List<Disciplina> findAll() {
        // Lógica para buscar todos os Disciplinas no banco de dados
        return disciplinaRepository.findAll();
    }

    public Disciplina findById(Integer id) {
        // Lógica para buscar um Disciplina por ID no banco de dados
        return disciplinaRepository.findById(id).orElse(null);
    }

    public void deleteById(Integer id) {
        // Lógica para deletar um Disciplina por ID no banco de dados
        disciplinaRepository.deleteById(id);
    }

    public Disciplina update(Disciplina Disciplina) {
        // Lógica para atualizar um Disciplina no banco de dados
        if (Disciplina.getIdDisciplina() != null && disciplinaRepository.existsById(Disciplina.getIdDisciplina())) {
            return disciplinaRepository.save(Disciplina);
        }
        return null; // Retorna null se o Disciplina não existir
    }
}
