package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.entity.AuditoriaPrecio;
import com.empresa.sistemaventas.service.AuditoriaPrecioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria-precios")
public class AuditoriaPrecioController {

    @Autowired
    private AuditoriaPrecioService auditoriaPrecioService;

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<AuditoriaPrecio>> obtenerHistorialPorProducto(@PathVariable Integer productoId) {
        return new ResponseEntity<>(auditoriaPrecioService.obtenerHistorialPorProducto(productoId), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<AuditoriaPrecio>> listarTodas() {
        return new ResponseEntity<>(auditoriaPrecioService.obtenerTodas(), HttpStatus.OK);
    }
}