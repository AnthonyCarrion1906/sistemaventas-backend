package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.entity.EgresoCaja;
import com.empresa.sistemaventas.service.EgresoCajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/egresos")
public class EgresoCajaController {

    @Autowired
    private EgresoCajaService egresoCajaService;

    @GetMapping("/caja/{cajaId}")
    public ResponseEntity<List<EgresoCaja>> listarPorCaja(@PathVariable Integer cajaId) {
        return new ResponseEntity<>(egresoCajaService.obtenerPorCaja(cajaId), HttpStatus.OK);
    }

    @PostMapping("/caja/{cajaId}")
    public ResponseEntity<?> registrarEgreso(@PathVariable Integer cajaId, @RequestBody EgresoCaja egreso) {
        try {
            return new ResponseEntity<>(egresoCajaService.registrarEgreso(egreso, cajaId), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping
    public ResponseEntity<List<EgresoCaja>> listarTodos() {
        return new ResponseEntity<>(egresoCajaService.obtenerTodos(), HttpStatus.OK);
    }
}