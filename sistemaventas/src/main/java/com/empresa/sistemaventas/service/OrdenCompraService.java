package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.entity.*;
import com.empresa.sistemaventas.repository.OrdenCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class OrdenCompraService {

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private KardexService kardexService;

    public List<OrdenCompra> obtenerTodas() {
        return ordenCompraRepository.findAll();
    }

    public List<OrdenCompra> obtenerPorProveedor(Integer proveedorId) {
        return ordenCompraRepository.findByProveedorId(proveedorId);
    }

    public OrdenCompra guardar(OrdenCompra ordenCompra) {
        Proveedor proveedor = proveedorService.obtenerPorId(ordenCompra.getProveedor().getId())
                .orElseThrow(() -> new RuntimeException("Proveedor inválido"));
        ordenCompra.setProveedor(proveedor);
        
        ordenCompra.getDetalles().forEach(detalle -> {
            detalle.setOrdenCompra(ordenCompra);
            Producto producto = productoService.obtenerPorId(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto inválido"));
            detalle.setProducto(producto);
            
            detalle.setSubtotal(detalle.getPrecioUnitario().multiply(detalle.getCantidad()));
            detalle.setCantidadRecibida(BigDecimal.ZERO);
        });
        
        return ordenCompraRepository.save(ordenCompra);
    }

    @Transactional
    public OrdenCompra recibir(Integer ordenId) {
        OrdenCompra orden = ordenCompraRepository.findById(ordenId)
                .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada"));
                
        // CORREGIDO: Uso de String y equals() de forma segura
        if ("RECIBIDA".equals(orden.getEstado())) {
            throw new RuntimeException("Orden ya recibida");
        }
        
        orden.getDetalles().forEach(detalle -> {
            detalle.setCantidadRecibida(detalle.getCantidad());
            Producto producto = detalle.getProducto();
            
            // Validación de seguridad para evitar NullPointerException en el cálculo matemático
            BigDecimal stockInicial = producto.getStockActual() != null ? producto.getStockActual() : BigDecimal.ZERO;
            producto.setStockActual(stockInicial.add(detalle.getCantidad()));
            
            BigDecimal costoPromedio = producto.getCostoPromedio() != null ? producto.getCostoPromedio() : BigDecimal.ZERO;
            
            // Si el stock inicial era 0, el nuevo costo promedio es simplemente el precio de esta compra
            BigDecimal nuevoCostoPromedio;
            if (stockInicial.compareTo(BigDecimal.ZERO) == 0) {
                nuevoCostoPromedio = detalle.getPrecioUnitario();
            } else {
                nuevoCostoPromedio = costoPromedio.multiply(stockInicial)
                        .add(detalle.getPrecioUnitario().multiply(detalle.getCantidad()))
                        .divide(stockInicial.add(detalle.getCantidad()), 2, RoundingMode.HALF_UP);
            }
            
            producto.setCostoPromedio(nuevoCostoPromedio);
            
            productoService.guardar(producto);
            
            kardexService.registrarMovimiento(producto, detalle.getCantidad(), "ENTRADA", "Orden de compra " + orden.getId());
        });
        
        // CORREGIDO: Asignación como String
        orden.setEstado("RECIBIDA");
        return ordenCompraRepository.save(orden);
    }

    public OrdenCompra adjuntarComprobante(Integer ordenId, String comprobanteUrl, String nroComprobante) {
        OrdenCompra orden = ordenCompraRepository.findById(ordenId)
                .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada"));
                
        orden.setComprobanteUrl(comprobanteUrl);
        orden.setNroComprobante(nroComprobante);
        
        return ordenCompraRepository.save(orden);
    }
}