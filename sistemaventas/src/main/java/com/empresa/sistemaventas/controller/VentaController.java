    package com.empresa.sistemaventas.controller;

    import com.empresa.sistemaventas.entity.Venta;
    import com.empresa.sistemaventas.service.VentaService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.Map;
    import java.util.List;

    @RestController
    @RequestMapping("/api/ventas")
    @CrossOrigin(origins = "*")
    public class VentaController {

        @Autowired
        private VentaService ventaService;

        @PostMapping("/convertir/{proformaId}")
        public ResponseEntity<?> convertirProforma(@PathVariable Integer proformaId, @RequestBody Map<String, String> payload) {
            try {
                String metodoPago = payload.get("metodoPago");
                return new ResponseEntity<>(ventaService.convertirProformaEnVenta(proformaId, metodoPago), HttpStatus.CREATED);
            } catch (RuntimeException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        // Importa List si no lo tienes: import java.util.List;

    @GetMapping
    public ResponseEntity<List<Venta>> listarTodas() {
        return new ResponseEntity<>(ventaService.obtenerTodas(), HttpStatus.OK);
    }
    }