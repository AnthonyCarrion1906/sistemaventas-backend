package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.entity.CajaDiaria;
import com.empresa.sistemaventas.entity.EgresoCaja;
import com.empresa.sistemaventas.repository.CajaDiariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CajaDiariaService {

    @Autowired
    private CajaDiariaRepository cajaDiariaRepository;

    public List<CajaDiaria> obtenerTodas() {
        return cajaDiariaRepository.findAll();
    }

    public CajaDiaria abrirCaja(LocalDate fecha, BigDecimal montoInicial) {
        if (cajaDiariaRepository.findByFecha(fecha).isPresent()) {
            throw new RuntimeException("Ya existe una caja abierta para esta fecha");
        }
        CajaDiaria caja = new CajaDiaria();
        caja.setFecha(fecha);
        caja.setMontoInicial(montoInicial);
        caja.setEstado("ABIERTA"); 
        caja.setFechaApertura(LocalDateTime.now()); // Registra la hora exacta
        return cajaDiariaRepository.save(caja);
    }

    @Transactional
    public CajaDiaria cerrarCaja(Integer id, BigDecimal montoFisicoReal, BigDecimal saldoEsperado) {
        CajaDiaria caja = cajaDiariaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada"));
                
        if ("CERRADA".equals(caja.getEstado())) {
            throw new RuntimeException("La caja ya está cerrada");
        }
        
        caja.setMontoFisicoReal(montoFisicoReal);
        caja.setTotalEsperado(saldoEsperado);
        caja.setEstado("CERRADA");
        caja.setFechaCierre(LocalDateTime.now()); // Registra la hora exacta de cierre
        
        return cajaDiariaRepository.save(caja);
    }

    @Transactional
    public EgresoCaja registrarEgreso(Integer cajaId, BigDecimal monto, String motivo, String categoria, String numeroCorrelativo) {
        CajaDiaria caja = cajaDiariaRepository.findById(cajaId)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada"));
                
        if ("CERRADA".equals(caja.getEstado())) {
            throw new RuntimeException("No se pueden registrar egresos en una caja cerrada");
        }
        
        BigDecimal efectivoDisponible = caja.getMontoInicial();
        for (EgresoCaja egreso : caja.getEgresos()) {
            efectivoDisponible = efectivoDisponible.subtract(egreso.getMonto());
        }
        
        if (monto.compareTo(efectivoDisponible) > 0) {
            throw new RuntimeException("El monto del egreso supera el efectivo disponible");
        }
        
        EgresoCaja egresoCaja = new EgresoCaja();
        egresoCaja.setCajaDiaria(caja);
        egresoCaja.setMonto(monto);
        egresoCaja.setMotivo(motivo);
        egresoCaja.setCategoria(categoria);
        egresoCaja.setTicketCorrelativo(numeroCorrelativo);
        caja.getEgresos().add(egresoCaja);
        cajaDiariaRepository.save(caja);
        return egresoCaja;
    }

    public Optional<CajaDiaria> obtenerPorId(Integer id) {
        return cajaDiariaRepository.findById(id);
    }
}