package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.entity.CajaDiaria;
import com.empresa.sistemaventas.entity.EgresoCaja;
import com.empresa.sistemaventas.service.CajaDiariaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/cajas")
@CrossOrigin(origins = "*")
public class CajaDiariaController {

    @Autowired
    private CajaDiariaService cajaDiariaService;

    @PostMapping("/abrir")
    public ResponseEntity<?> abrirCaja(@RequestBody Map<String, String> payload) {
        try {
            LocalDate fecha = LocalDate.parse(payload.get("fecha"));
            BigDecimal montoInicial = new BigDecimal(payload.get("montoInicial"));
            return new ResponseEntity<>(cajaDiariaService.abrirCaja(fecha, montoInicial), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<?> cerrarCaja(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        try {
            BigDecimal montoFisicoReal = new BigDecimal(payload.get("montoFisicoReal"));
            BigDecimal saldoEsperado = new BigDecimal(payload.get("saldoEsperado"));
            return new ResponseEntity<>(cajaDiariaService.cerrarCaja(id, montoFisicoReal, saldoEsperado), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/egreso")
    public ResponseEntity<?> registrarEgreso(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        try {
            BigDecimal monto = new BigDecimal(payload.get("monto"));
            String motivo = payload.get("motivo");
            String categoria = payload.get("categoria");
            String numeroCorrelativo = payload.get("numeroCorrelativo");
            return new ResponseEntity<>(cajaDiariaService.registrarEgreso(id, monto, motivo, categoria, numeroCorrelativo), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCaja(@PathVariable Integer id) {
        return cajaDiariaService.obtenerPorId(id)
                .map(caja -> new ResponseEntity<Object>(caja, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<Object>("Caja no encontrada", HttpStatus.NOT_FOUND));
    }
    @GetMapping
    public ResponseEntity<?> listarTodasLasCajas() {
        // Asumiendo que en tu CajaDiariaService tienes un método obtenerTodas()
        return new ResponseEntity<>(cajaDiariaService.obtenerTodas(), HttpStatus.OK);
    }
}