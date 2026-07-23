package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.dto.DniResponseDto;
import com.empresa.sistemaventas.dto.RucResponseDto;
import com.empresa.sistemaventas.dto.TipoCambioResponseDto;
import com.empresa.sistemaventas.service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consultas")
@CrossOrigin(origins = "*")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @GetMapping("/dni")
    public ResponseEntity<?> consultarDni(@RequestParam String numero) {
        try {
            if (numero == null || numero.trim().isEmpty()) {
                return new ResponseEntity<>("El número de DNI es obligatorio", HttpStatus.BAD_REQUEST);
            }
            DniResponseDto response = consultaService.consultarDni(numero);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/ruc")
    public ResponseEntity<?> consultarRuc(@RequestParam String numero) {
        try {
            if (numero == null || numero.trim().isEmpty()) {
                return new ResponseEntity<>("El número de RUC es obligatorio", HttpStatus.BAD_REQUEST);
            }
            RucResponseDto response = consultaService.consultarRuc(numero);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Consulta el tipo de cambio USD/PEN publicado por SUNAT.
     * Ejemplo: GET /api/consultas/tipo-cambio?fecha=2025-08-08
     * Si no se indica fecha, retorna el tipo de cambio del día actual.
     */
    @GetMapping("/tipo-cambio")
    public ResponseEntity<?> consultarTipoCambio(
            @RequestParam(required = false) String fecha) {
        try {
            TipoCambioResponseDto response = consultaService.consultarTipoCambio(fecha);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
