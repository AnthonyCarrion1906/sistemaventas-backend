package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.entity.Subcategoria;
import com.empresa.sistemaventas.service.SubcategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subcategorias")
public class SubcategoriaController {

    @Autowired
    private SubcategoriaService subcategoriaService;

    @GetMapping
    public ResponseEntity<List<Subcategoria>> listarTodas() {
        return new ResponseEntity<>(subcategoriaService.obtenerTodas(), HttpStatus.OK);
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Subcategoria>> listarPorCategoria(@PathVariable Integer categoriaId) {
        return new ResponseEntity<>(subcategoriaService.obtenerPorCategoria(categoriaId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Subcategoria subcategoria) {
        try {
            return new ResponseEntity<>(subcategoriaService.guardar(subcategoria), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}