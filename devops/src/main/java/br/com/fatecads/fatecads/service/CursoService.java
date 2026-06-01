package br.com.fatecads.fatecads.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Curso;
import br.com.fatecads.fatecads.repository.CursoRepository;

@Service
public class CursoService {
   
    @Autowired
    private CursoRepository cursoRepository;

    public Curso save(Curso curso) {
        return cursoRepository.save(curso);
    }

    public List<Curso> findAll() {
        return cursoRepository.findAll();
    }

    public Curso findById(Integer id) {
        return cursoRepository.findById(id).orElse(null);
    }

    public void deleteById(Integer id) {
        cursoRepository.deleteById(id);
    }

    public Curso update(Curso curso) {
        if (curso.getIdCurso() != null && cursoRepository.existsById(curso.getIdCurso())) {
            return cursoRepository.save(curso);
        }
        return null;
    }
}
