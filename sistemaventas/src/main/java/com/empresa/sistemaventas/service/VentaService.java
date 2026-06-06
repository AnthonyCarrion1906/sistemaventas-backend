package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.entity.*;
import com.empresa.sistemaventas.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional; // <-- Agregamos esta importación

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProformaService proformaService;

    public List<Venta> obtenerTodas() {
        return ventaRepository.findAll();
    }

    // --- ESTE ES EL MÉTODO QUE FALTABA PARA LAS DEVOLUCIONES ---
    public Optional<Venta> obtenerPorId(Integer id) {
        return ventaRepository.findById(id);
    }
    // ------------------------------------------------------------

    @Transactional
    // OJO: Le quité el parámetro String comprobante porque ya no existe en la BD
    public Venta convertirProformaEnVenta(Integer proformaId, String metodoPago) {
        Proforma proforma = proformaService.obtenerPorId(proformaId)
                .orElseThrow(() -> new RuntimeException("Proforma no encontrada"));
                
        if (Boolean.TRUE.equals(proforma.getEsFinal())) {
            throw new RuntimeException("La proforma ya fue convertida en venta");
        }
        
        Venta venta = new Venta();
        venta.setProforma(proforma);
        venta.setFecha(proforma.getFecha());
        venta.setMetodoPago(metodoPago);
        
        // Asignamos el id del usuario que hizo la proforma (si aplica) o puedes pasarlo como parámetro luego
        venta.setUsuarioId(proforma.getUsuarioId()); 
        
        // Corregido: de setTotalVenta a setTotal
        venta.setTotal(proforma.getTotal());
        
        // La utilidad la podemos inicializar en cero por ahora si no tienes la fórmula aún
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
        
        return ventaRepository.save(venta);
    }
    
}