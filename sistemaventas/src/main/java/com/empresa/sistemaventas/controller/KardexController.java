package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.entity.KardexMovimiento;
import com.empresa.sistemaventas.service.KardexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kardex")
public class KardexController {

    @Autowired
    private KardexService kardexService;

    // --- EL NUEVO MÉTODO QUE BUSCA POSTMAN ---
    @GetMapping
    public ResponseEntity<List<KardexMovimiento>> listarTodos() {
        return new ResponseEntity<>(kardexService.obtenerTodos(), HttpStatus.OK);
    }

    // --- EL MÉTODO QUE YA TENÍAMOS ---
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<KardexMovimiento>> obtenerHistorial(@PathVariable Integer productoId) {
        return new ResponseEntity<>(kardexService.obtenerPorProducto(productoId), HttpStatus.OK);
    }
}