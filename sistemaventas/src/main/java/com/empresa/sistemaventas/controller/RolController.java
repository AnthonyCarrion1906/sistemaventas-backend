package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.entity.Rol;
import com.empresa.sistemaventas.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping
    public ResponseEntity<List<Rol>> listarTodos() {
        return new ResponseEntity<>(rolService.obtenerTodos(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Rol> crear(@RequestBody Rol rol) {
        return new ResponseEntity<>(rolService.guardar(rol), HttpStatus.CREATED);
    }
}