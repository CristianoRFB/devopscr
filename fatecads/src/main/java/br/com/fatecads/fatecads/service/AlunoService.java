package br.com.fatecads.fatecads.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.repository.AlunoRepository;

@Service
public class AlunoService {
    
    // Injeção de dependência do repositório de alunos
    @Autowired
    private AlunoRepository alunoRepository;

    public Aluno save(Aluno aluno) {
        // Lógica para salvar um aluno no banco de dados
        return alunoRepository.save(aluno);
    }

    public List<Aluno> findAll() {
        // Lógica para buscar todos os alunos no banco de dados
        return alunoRepository.findAll();
    }

    public Aluno findById(Integer id) {
        // Lógica para buscar um aluno por ID no banco de dados
        return alunoRepository.findById(id).orElse(null);
    }

    public void deleteById(Integer id) {
        // Lógica para deletar um aluno por ID no banco de dados
        alunoRepository.deleteById(id);
    }

    public Aluno update(Aluno aluno) {
        // Lógica para atualizar um aluno no banco de dados
        if (aluno.getIdAluno() != null && alunoRepository.existsById(aluno.getIdAluno())) {
            return alunoRepository.save(aluno);
        }
        return null; // Retorna null se o aluno não existir
    }
}
