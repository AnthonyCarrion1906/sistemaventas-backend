package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.entity.*;
import com.empresa.sistemaventas.repository.OrdenCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        return ordenCompraRepository.findAllByOrderByIdDesc();
    }

    public List<OrdenCompra> obtenerPorProveedor(Integer proveedorId) {
        return ordenCompraRepository.findByProveedorId(proveedorId);
    }

    public OrdenCompra guardar(OrdenCompra ordenCompra) {
        Proveedor proveedor = proveedorService.obtenerPorId(ordenCompra.getProveedor().getId())
                .orElseThrow(() -> new RuntimeException("Proveedor inválido"));
        ordenCompra.setProveedor(proveedor);

        BigDecimal sumaSubtotales = BigDecimal.ZERO;
        if (ordenCompra.getDetalles() != null) {
            for (OrdenCompraDetalle detalle : ordenCompra.getDetalles()) {
                detalle.setOrdenCompra(ordenCompra);
                Producto producto = productoService.obtenerPorId(detalle.getProducto().getId())
                        .orElseThrow(() -> new RuntimeException("Producto inválido"));
                detalle.setProducto(producto);

                BigDecimal subtotalItem = detalle.getPrecioUnitario().multiply(detalle.getCantidad());
                detalle.setSubtotal(subtotalItem);
                detalle.setCantidadRecibida(BigDecimal.ZERO);

                sumaSubtotales = sumaSubtotales.add(subtotalItem);
            }
        }

        ordenCompra.setTotal(sumaSubtotales);

        boolean esUSD = "USD".equalsIgnoreCase(ordenCompra.getMoneda());
        BigDecimal tc = (esUSD && ordenCompra.getTipoCambio() != null && ordenCompra.getTipoCambio().compareTo(BigDecimal.ZERO) > 0)
                ? ordenCompra.getTipoCambio()
                : BigDecimal.ONE;

        ordenCompra.setTotalPen(sumaSubtotales.multiply(tc));

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

            // Validación de seguridad para evitar NullPointerException en el incremento de
            // stock
            BigDecimal stockInicial = producto.getStockActual() != null ? producto.getStockActual() : BigDecimal.ZERO;
            producto.setStockActual(stockInicial.add(detalle.getCantidad()));

            productoService.guardar(producto);

            kardexService.registrarMovimiento(producto, detalle.getCantidad(), "ENTRADA",
                    "Orden de compra " + orden.getId());
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