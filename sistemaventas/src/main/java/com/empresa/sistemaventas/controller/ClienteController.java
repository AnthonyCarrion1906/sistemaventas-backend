package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.entity.Cliente;
import com.empresa.sistemaventas.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes(@RequestParam(required = false) String q) {
        return new ResponseEntity<>(clienteService.buscar(q), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCliente(@PathVariable Integer id) {
        return clienteService.obtenerPorId(id)
                .map(cliente -> new ResponseEntity<Object>(cliente, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<Object>("Cliente no encontrado", HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> crearCliente(@RequestBody Cliente cliente) {
        try {
            return new ResponseEntity<>(clienteService.guardar(cliente), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarCliente(@PathVariable Integer id, @RequestBody Cliente cliente) {
        try {
            return new ResponseEntity<>(clienteService.actualizar(id, cliente), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
