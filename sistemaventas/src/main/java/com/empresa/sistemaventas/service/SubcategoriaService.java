package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.entity.Categoria;
import com.empresa.sistemaventas.entity.Subcategoria;
import com.empresa.sistemaventas.repository.SubcategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubcategoriaService {

    @Autowired
    private SubcategoriaRepository subcategoriaRepository;

    @Autowired
    private CategoriaService categoriaService; // Reutilizamos el servicio que ya tienes

    public List<Subcategoria> obtenerTodas() {
        return subcategoriaRepository.findAll();
    }

    public List<Subcategoria> obtenerPorCategoria(Integer categoriaId) {
        return subcategoriaRepository.findByCategoriaId(categoriaId);
    }

    public Optional<Subcategoria> obtenerPorId(Integer id) {
        return subcategoriaRepository.findById(id);
    }

    public Subcategoria guardar(Subcategoria subcategoria) {
        Categoria categoria = categoriaService.obtenerPorId(subcategoria.getCategoria().getId())
                .orElseThrow(() -> new RuntimeException("Categoría principal no encontrada"));
        
        subcategoria.setCategoria(categoria);
        return subcategoriaRepository.save(subcategoria);
    }
}