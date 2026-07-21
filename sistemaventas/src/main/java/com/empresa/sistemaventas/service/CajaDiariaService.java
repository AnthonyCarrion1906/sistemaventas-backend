package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.constant.EstadoCaja;
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

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CajaDiariaService {

    @Autowired
    private CajaDiariaRepository cajaDiariaRepository;

    public List<CajaDiaria> obtenerTodas() {
        return cajaDiariaRepository.findAll();
    }

    public CajaDiaria abrirCaja(LocalDate fecha, BigDecimal montoInicial, Integer usuarioId) {
        if (cajaDiariaRepository.findByFechaAndEstado(fecha, EstadoCaja.ABIERTA).isPresent()) {
            throw new RuntimeException("Ya existe una caja abierta para esta fecha");
        }
        CajaDiaria caja = new CajaDiaria();
        caja.setFecha(fecha);
        caja.setMontoInicial(montoInicial);
        caja.setUsuarioId(usuarioId);
        caja.setEstado(EstadoCaja.ABIERTA);
        caja.setFechaApertura(LocalDateTime.now()); // Registra la hora exacta
        return cajaDiariaRepository.save(caja);
    }

    @Transactional
    public CajaDiaria cerrarCaja(Integer id, BigDecimal montoCierreReal) {
        CajaDiaria caja = cajaDiariaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada"));

        if (EstadoCaja.CERRADA.equals(caja.getEstado())) {
            throw new RuntimeException("La caja ya está cerrada");
        }

        // montoCierreReal: lo que el cajero cuenta físicamente al cierre.
        // montoFisicoReal: calculado por el sistema (montoInicial + ventasEfectivo - egresos).
        caja.setMontoCierreReal(montoCierreReal);
        caja.setEstado(EstadoCaja.CERRADA);
        caja.setFechaCierre(LocalDateTime.now());

        log.info("Caja ID {} cerrada. totalEsperado (todas las ventas): {}, montoFisicoReal (efectivo sistema): {}, montoCierreReal (arqueo manual): {}, descuadre: {}",
                id,
                caja.getTotalEsperado(),
                caja.getMontoFisicoReal(),
                montoCierreReal,
                montoCierreReal.subtract(caja.getMontoFisicoReal() != null ? caja.getMontoFisicoReal() : BigDecimal.ZERO));

        return cajaDiariaRepository.save(caja);
    }

    @Transactional
    public EgresoCaja registrarEgreso(Integer cajaId, BigDecimal monto, String motivo, String categoria,
            String numeroCorrelativo) {
        CajaDiaria caja = cajaDiariaRepository.findById(cajaId)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada"));

        if (EstadoCaja.CERRADA.equals(caja.getEstado())) {
            throw new RuntimeException("No se pueden registrar egresos en una caja cerrada");
        }

        // El efectivo disponible actual en caja se rige por montoFisicoReal (efectivo teórico del sistema)
        BigDecimal efectivoDisponible = caja.getMontoFisicoReal() != null ? caja.getMontoFisicoReal() : BigDecimal.ZERO;

        if (monto.compareTo(efectivoDisponible) > 0) {
            throw new RuntimeException("El monto del egreso supera el efectivo disponible en caja (Monto solicitado: " + monto
                    + ", Disponible actual: " + efectivoDisponible + ")");
        }

        // Creamos y asociamos el egreso
        EgresoCaja egresoCaja = new EgresoCaja();
        egresoCaja.setCajaDiaria(caja);
        egresoCaja.setMonto(monto);
        egresoCaja.setMotivo(motivo);
        egresoCaja.setCategoria(categoria);
        egresoCaja.setTicketCorrelativo(numeroCorrelativo);
        caja.getEgresos().add(egresoCaja);

        // Descontamos del efectivo del sistema (montoFisicoReal)
        caja.setMontoFisicoReal(efectivoDisponible.subtract(monto));

        // Descontamos del saldo esperado total (totalEsperado)
        BigDecimal esperadoActual = caja.getTotalEsperado() != null ? caja.getTotalEsperado() : BigDecimal.ZERO;
        caja.setTotalEsperado(esperadoActual.subtract(monto));

        cajaDiariaRepository.save(caja);
        log.info("Egreso de {} registrado en caja ID {}. Nuevo montoFisicoReal: {}, Nuevo totalEsperado: {}",
                monto, cajaId, caja.getMontoFisicoReal(), caja.getTotalEsperado());

        return egresoCaja;
    }

    public Optional<CajaDiaria> obtenerPorId(Integer id) {
        return cajaDiariaRepository.findById(id);
    }

    /**
     * Retorna la caja diaria activa/ABIERTA más reciente.
     * Utilizado por VentaService y DevolucionService para validar antes de registrar transacciones.
     */
    public Optional<CajaDiaria> obtenerCajaAbiertaHoy() {
        return cajaDiariaRepository.findFirstByEstadoOrderByFechaDesc(EstadoCaja.ABIERTA);
    }

    /**
     * Acumula un ingreso por venta en la caja:
     * - Siempre suma al {@code totalEsperado} (todos los métodos de pago).
     * - Solo suma al {@code montoFisicoReal} cuando {@code afectaFisico} es true (solo EFECTIVO).
     *
     * @param cajaId       ID de la caja a actualizar
     * @param monto        monto de la venta a registrar
     * @param afectaFisico true si el pago es en EFECTIVO (dinero físico que entra a la caja)
     */
    @Transactional
    public void registrarIngreso(Integer cajaId, BigDecimal monto, boolean afectaFisico) {
        CajaDiaria caja = cajaDiariaRepository.findById(cajaId)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada"));

        if (EstadoCaja.CERRADA.equals(caja.getEstado())) {
            throw new RuntimeException("No se pueden registrar ingresos en una caja cerrada");
        }

        // Acumula en total_esperado para todos los métodos de pago
        BigDecimal esperadoActual = caja.getTotalEsperado() != null ? caja.getTotalEsperado() : BigDecimal.ZERO;
        caja.setTotalEsperado(esperadoActual.add(monto));

        // Acumula en monto_fisico_real solo para EFECTIVO
        if (afectaFisico) {
            BigDecimal fisicoActual = caja.getMontoFisicoReal() != null ? caja.getMontoFisicoReal() : BigDecimal.ZERO;
            caja.setMontoFisicoReal(fisicoActual.add(monto));
        }

        cajaDiariaRepository.save(caja);
        log.info("Ingreso de {} registrado en caja ID {} [afectaFisico={}]. totalEsperado={}, montoFisicoReal={}",
                monto, cajaId, afectaFisico, caja.getTotalEsperado(), caja.getMontoFisicoReal());
    }
}