package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.entity.Producto;
import com.empresa.sistemaventas.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos(@RequestParam(required = false) String q) {
        return new ResponseEntity<>(productoService.buscar(q), HttpStatus.OK);
    }

    @GetMapping("/reponer")
    public ResponseEntity<List<Producto>> productosParaReponer() {
        return new ResponseEntity<>(productoService.obtenerProductosParaReponer(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProducto(@PathVariable Integer id) {
        return productoService.obtenerPorId(id)
                .map(producto -> new ResponseEntity<Object>(producto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<Object>("Producto no encontrado", HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        return new ResponseEntity<>(productoService.guardar(producto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarProducto(@PathVariable Integer id, @RequestBody Producto producto) {
        try {
            return new ResponseEntity<>(productoService.actualizar(id, producto), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
