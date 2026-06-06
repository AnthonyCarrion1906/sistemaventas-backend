package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.entity.CajaDiaria;
import com.empresa.sistemaventas.entity.EgresoCaja;
import com.empresa.sistemaventas.repository.EgresoCajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EgresoCajaService {

    @Autowired
    private EgresoCajaRepository egresoCajaRepository;

    @Autowired
    private CajaDiariaService cajaDiariaService;

    public List<EgresoCaja> obtenerPorCaja(Integer cajaDiariaId) {
        return egresoCajaRepository.findByCajaDiariaId(cajaDiariaId);
    }

    @Transactional
    public EgresoCaja registrarEgreso(EgresoCaja egreso, Integer cajaDiariaId) {
        // Buscamos la caja actual
        CajaDiaria caja = cajaDiariaService.obtenerPorId(cajaDiariaId)
                .orElseThrow(() -> new RuntimeException("Caja diaria no encontrada"));

        // Regla de negocio: La caja debe estar ABIERTA
        if ("CERRADA".equals(caja.getEstado())) {
            throw new RuntimeException("No se pueden registrar egresos en una caja que ya está cerrada");
        }

        egreso.setCajaDiaria(caja);
        
        if (egreso.getFecha() == null) {
            egreso.setFecha(LocalDateTime.now());
        }

        return egresoCajaRepository.save(egreso);
    }
    public List<EgresoCaja> obtenerTodos() {
        return egresoCajaRepository.findAll();
    }
}