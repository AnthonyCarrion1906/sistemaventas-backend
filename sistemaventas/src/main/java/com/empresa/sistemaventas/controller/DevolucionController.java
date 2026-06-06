package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.entity.Devolucion;
import com.empresa.sistemaventas.service.DevolucionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devoluciones")
public class DevolucionController {

    @Autowired
    private DevolucionService devolucionService;

    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<Devolucion>> obtenerPorVenta(@PathVariable Integer ventaId) {
        return new ResponseEntity<>(devolucionService.obtenerPorVenta(ventaId), HttpStatus.OK);
    }

    @PostMapping("/venta/{ventaId}/producto/{productoId}")
    public ResponseEntity<?> registrarDevolucion(
            @PathVariable Integer ventaId,
            @PathVariable Integer productoId,
            @RequestBody Devolucion devolucion) {
        try {
            Devolucion nuevaDevolucion = devolucionService.registrarDevolucion(devolucion, ventaId, productoId);
            return new ResponseEntity<>(nuevaDevolucion, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping
    public ResponseEntity<List<Devolucion>> listarTodas() {
        return new ResponseEntity<>(devolucionService.obtenerTodas(), HttpStatus.OK);
    }
}