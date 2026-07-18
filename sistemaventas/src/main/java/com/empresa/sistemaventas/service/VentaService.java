package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.constant.MetodoPago;
import com.empresa.sistemaventas.entity.*;
import com.empresa.sistemaventas.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProformaService proformaService;

    @Autowired
    private CajaDiariaService cajaDiariaService;

    public List<Venta> obtenerTodas() {
        return ventaRepository.findAll();
    }

    // --- ESTE ES EL MÉTODO QUE FALTABA PARA LAS DEVOLUCIONES ---
    public Optional<Venta> obtenerPorId(Integer id) {
        return ventaRepository.findById(id);
    }
    // ------------------------------------------------------------

    @Transactional
    public Venta convertirProformaEnVenta(Integer proformaId, String metodoPago) {
        Proforma proforma = proformaService.obtenerPorId(proformaId)
                .orElseThrow(() -> new RuntimeException("Proforma no encontrada"));

        if (Boolean.TRUE.equals(proforma.getEsFinal())) {
            throw new RuntimeException("La proforma ya fue convertida en venta");
        }

        // Toda venta requiere caja abierta para trazabilidad completa de ingresos
        CajaDiaria cajaActiva = cajaDiariaService.obtenerCajaAbiertaHoy()
                .orElseThrow(() -> new RuntimeException(
                        "No hay una caja diaria abierta para hoy. "
                        + "Debe abrir la caja antes de registrar cualquier venta."
                ));

        Venta venta = new Venta();
        venta.setProforma(proforma);
        venta.setFecha(proforma.getFecha());
        venta.setMetodoPago(metodoPago);

        // Asignamos el id del usuario que hizo la proforma
        venta.setUsuarioId(proforma.getUsuarioId());

        venta.setTotal(proforma.getTotal());
        venta.setUtilidad(BigDecimal.ZERO);

        proforma.getDetalles().forEach(detalle -> {
            VentaDetalle ventaDetalle = new VentaDetalle();
            ventaDetalle.setVenta(venta);
            ventaDetalle.setProductoId(detalle.getProductoId());
            ventaDetalle.setServicioId(detalle.getServicioId());
            ventaDetalle.setDescripcion(detalle.getDescripcion());
            ventaDetalle.setCantidad(detalle.getCantidad());
            ventaDetalle.setPrecioUnitario(detalle.getPrecioUnitario());
            ventaDetalle.setSubtotal(detalle.getSubtotal());
            venta.getDetalles().add(ventaDetalle);
        });

        proforma.setEstado("CERRADA");
        proforma.setEsFinal(true);

        Venta guardada = ventaRepository.save(venta);

        // Acumular ingreso en la caja:
        //   - total_esperado: siempre (todos los métodos de pago)
        //   - monto_fisico_real: solo EFECTIVO (dinero físico que entra a la caja)
        cajaDiariaService.registrarIngreso(
                cajaActiva.getId(),
                proforma.getTotal(),
                MetodoPago.afectaEfectivoFisico(metodoPago)
        );

        return guardada;
    }

}