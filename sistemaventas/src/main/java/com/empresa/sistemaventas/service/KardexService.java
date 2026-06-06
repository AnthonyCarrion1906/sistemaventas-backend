package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.entity.KardexMovimiento;
import com.empresa.sistemaventas.entity.Producto;
import com.empresa.sistemaventas.repository.KardexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class KardexService {

    @Autowired
    private KardexRepository kardexRepository;

    public List<KardexMovimiento> obtenerPorProducto(Integer productoId) {
        return kardexRepository.findByProductoId(productoId);
    }

    @Transactional
    public KardexMovimiento registrarMovimiento(Producto producto, BigDecimal cantidad, String tipoMovimiento, String documentoReferencia) {
        KardexMovimiento movimiento = new KardexMovimiento();
        movimiento.setProducto(producto);
        movimiento.setTipoMovimiento(tipoMovimiento); 
        movimiento.setCantidad(cantidad);
        
        BigDecimal stockInicial = producto.getStockActual() != null ? producto.getStockActual() : BigDecimal.ZERO;
        movimiento.setStockInicial(stockInicial);
        
        BigDecimal stockFinal;
        if ("ENTRADA".equals(tipoMovimiento)) {
            stockFinal = stockInicial.add(cantidad);
        } else if ("SALIDA".equals(tipoMovimiento)) {
            stockFinal = stockInicial.subtract(cantidad);
        } else {
            throw new RuntimeException("Tipo de movimiento no válido. Use ENTRADA o SALIDA.");
        }
        
        movimiento.setStockFinal(stockFinal);
        movimiento.setDocumentoReferencia(documentoReferencia);
        movimiento.setFecha(LocalDateTime.now()); // Ajustado a LocalDateTime
        
        return kardexRepository.save(movimiento);
    }
    public List<KardexMovimiento> obtenerTodos() {
        return kardexRepository.findAll();
    }
}