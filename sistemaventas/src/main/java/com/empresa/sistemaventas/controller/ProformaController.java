package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.entity.Proforma;
import com.empresa.sistemaventas.service.ProformaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proformas")
public class ProformaController {

    @Autowired
    private ProformaService proformaService;

    @GetMapping
    public ResponseEntity<List<Proforma>> listarProformas() {
        return new ResponseEntity<>(proformaService.obtenerTodas(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProforma(@PathVariable Integer id) {
        return proformaService.obtenerPorId(id)
                .map(proforma -> new ResponseEntity<Object>(proforma, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<Object>("Proforma no encontrada", HttpStatus.NOT_FOUND));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Proforma>> listarPorCliente(@PathVariable Integer clienteId) {
        return new ResponseEntity<>(proformaService.obtenerPorCliente(clienteId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> crearProforma(@RequestBody Proforma proforma) {
        try {
            return new ResponseEntity<>(proformaService.guardar(proforma), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarProforma(@PathVariable Integer id, @RequestBody Proforma proforma) {
        try {
            return new ResponseEntity<>(proformaService.actualizar(id, proforma), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/estado")
    // CORREGIDO: Ahora recibe un String en lugar del Enum eliminado
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id, @RequestParam String estado) {
        try {
            return new ResponseEntity<>(proformaService.cambiarEstado(id, estado), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/version")
    public ResponseEntity<?> crearVersion(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(proformaService.crearVersion(id), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}