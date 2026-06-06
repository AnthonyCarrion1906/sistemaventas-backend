package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.entity.ProductoProveedor;
import com.empresa.sistemaventas.service.ProductoProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/producto-proveedor")
@CrossOrigin(origins = "*")
public class ProductoProveedorController {

    @Autowired
    private ProductoProveedorService service;

    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<List<ProductoProveedor>> listarPorProveedor(@PathVariable Integer proveedorId) {
        return new ResponseEntity<>(service.obtenerPorProveedor(proveedorId), HttpStatus.OK);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ProductoProveedor>> listarPorProducto(@PathVariable Integer productoId) {
        return new ResponseEntity<>(service.obtenerPorProducto(productoId), HttpStatus.OK);
    }

    @GetMapping("/comparativa/{productoId}")
    public ResponseEntity<List<ProductoProveedor>> comparativaPorProducto(@PathVariable Integer productoId) {
        return new ResponseEntity<>(service.obtenerComparativaPrecios(productoId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductoProveedor> crearVinculo(@RequestBody ProductoProveedor registro) {
        return new ResponseEntity<>(service.guardar(registro), HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<ProductoProveedor>> listarTodos() {
        // Asumiendo que en tu service tienes un método obtenerTodos()
        return new ResponseEntity<>(service.obtenerTodos(), HttpStatus.OK);
    }
}