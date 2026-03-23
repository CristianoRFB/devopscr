package br.com.fatecads.fatecads.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.repository.CursoRepository;

@Service
public class CursoService {
   
    @Autowired
    private CursoRepository cursoRepository;
}
