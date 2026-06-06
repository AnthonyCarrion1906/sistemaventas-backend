package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.entity.Proveedor;
import com.empresa.sistemaventas.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@CrossOrigin(origins = "*")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<List<Proveedor>> listarProveedores(@RequestParam(required = false) String q) {
        return new ResponseEntity<>(proveedorService.buscar(q), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProveedor(@PathVariable Integer id) {
        return proveedorService.obtenerPorId(id)
                .map(proveedor -> new ResponseEntity<Object>(proveedor, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<Object>("Proveedor no encontrado", HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> crearProveedor(@RequestBody Proveedor proveedor) {
        try {
            return new ResponseEntity<>(proveedorService.guardar(proveedor), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarProveedor(@PathVariable Integer id, @RequestBody Proveedor proveedor) {
        try {
            return new ResponseEntity<>(proveedorService.actualizar(id, proveedor), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
