package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.dto.DashboardResumenDto;
import com.empresa.sistemaventas.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para el Dashboard.
 * Expone un único endpoint consolidado que el frontend consume.
 * Endpoint: GET /api/dashboard/resumen
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumenDto> obtenerResumen() {
        try {
            return new ResponseEntity<>(dashboardService.obtenerResumen(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
