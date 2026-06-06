package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.entity.Usuario;
import com.empresa.sistemaventas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return new ResponseEntity<>(usuarioService.obtenerTodos(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Integer id) {
        return usuarioService.obtenerPorId(id)
                .map(usuario -> new ResponseEntity<Object>(usuario, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<Object>("Usuario no encontrado", HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        return new ResponseEntity<>(usuarioService.guardar(usuario), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarUsuario(@PathVariable Integer id, @RequestBody Usuario usuario) {
        try {
            return new ResponseEntity<>(usuarioService.actualizar(id, usuario), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}