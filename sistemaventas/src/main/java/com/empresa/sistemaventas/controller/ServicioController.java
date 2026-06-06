package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.entity.Servicio;
import com.empresa.sistemaventas.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "*")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @GetMapping
    public ResponseEntity<List<Servicio>> listarServicios() {
        return new ResponseEntity<>(servicioService.obtenerTodas(), HttpStatus.OK);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Servicio>> listarServiciosActivos() {
        return new ResponseEntity<>(servicioService.obtenerActivos(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> crearServicio(@RequestBody Servicio servicio) {
        try {
            return new ResponseEntity<>(servicioService.guardar(servicio), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarServicio(@PathVariable Integer id, @RequestBody Servicio servicio) {
        try {
            return new ResponseEntity<>(servicioService.actualizar(id, servicio), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id, @RequestParam Boolean activo) {
        try {
            return new ResponseEntity<>(servicioService.cambiarEstado(id, activo), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}