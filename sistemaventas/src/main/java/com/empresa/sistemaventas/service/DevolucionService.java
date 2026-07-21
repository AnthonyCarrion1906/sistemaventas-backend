package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.entity.CajaDiaria;
import com.empresa.sistemaventas.entity.Devolucion;
import com.empresa.sistemaventas.entity.Producto;
import com.empresa.sistemaventas.entity.Venta;
import com.empresa.sistemaventas.entity.VentaDetalle;
import com.empresa.sistemaventas.repository.DevolucionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DevolucionService {

    @Autowired
    private DevolucionRepository devolucionRepository;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private KardexService kardexService;

    @Autowired
    private CajaDiariaService cajaDiariaService;

    public List<Devolucion> obtenerPorVenta(Integer ventaId) {
        return devolucionRepository.findByVentaId(ventaId);
    }

    @Transactional
    public Devolucion registrarDevolucion(Devolucion datosDevolucion, Integer ventaId, Integer productoId) {
        // Validar que la caja esté abierta hoy
        CajaDiaria cajaActiva = cajaDiariaService.obtenerCajaAbiertaHoy()
                .orElseThrow(() -> new RuntimeException("Debe abrir la caja diaria antes de registrar una devolución."));

        Venta venta = ventaService.obtenerPorId(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
                
        Producto producto = productoService.obtenerPorId(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // 1. Validar que el producto pertenezca a la venta
        BigDecimal cantidadVendida = venta.getDetalles().stream()
                .filter(d -> productoId.equals(d.getProductoId()))
                .map(VentaDetalle::getCantidad)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (cantidadVendida.compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("El producto '" + producto.getNombre() + "' no pertenece a la venta #" + ventaId);
        }

        // 2. Validar que la cantidad a devolver sea válida
        if (datosDevolucion.getCantidad() == null || datosDevolucion.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("La cantidad a devolver debe ser mayor a cero.");
        }

        // 3. Validar cantidad máxima disponible para devolver (considerando devoluciones previas)
        List<Devolucion> devolucionesPrevias = devolucionRepository.findByVentaId(ventaId);
        BigDecimal cantidadDevueltaPrevia = devolucionesPrevias.stream()
                .filter(d -> d.getProducto() != null && productoId.equals(d.getProducto().getId()))
                .map(Devolucion::getCantidad)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal disponibleParaDevolver = cantidadVendida.subtract(cantidadDevueltaPrevia);

        if (datosDevolucion.getCantidad().compareTo(disponibleParaDevolver) > 0) {
            throw new RuntimeException("No se pueden devolver " + datosDevolucion.getCantidad() 
                    + " unidades de '" + producto.getNombre() + "'. Máximo disponible para devolución: " + disponibleParaDevolver);
        }

        datosDevolucion.setVenta(venta);
        datosDevolucion.setProducto(producto);
        
        Devolucion devolucionGuardada = devolucionRepository.save(datosDevolucion);

        if (Boolean.TRUE.equals(datosDevolucion.getRetornoStock())) {
            // CORREGIDO: Los parámetros ahora coinciden exactamente con la estructura del KardexService
            kardexService.registrarMovimiento(
                    producto, 
                    datosDevolucion.getCantidad(), 
                    "ENTRADA", 
                    "Devolución de la Venta " + venta.getId()
            );
            
            producto.setStockActual(producto.getStockActual().add(datosDevolucion.getCantidad()));
            productoService.guardar(producto);
        }

        // Impacto en la caja diaria: registrar egreso si hay devolución de dinero
        if (datosDevolucion.getImpactoEconomico() != null && datosDevolucion.getImpactoEconomico().compareTo(BigDecimal.ZERO) > 0) {
            cajaDiariaService.registrarEgreso(
                    cajaActiva.getId(),
                    datosDevolucion.getImpactoEconomico(),
                    "Devolución de producto: " + producto.getNombre() + " (Venta ID: " + venta.getId() + ")",
                    "DEVOLUCION",
                    "DEV-" + devolucionGuardada.getId()
            );
        }

        return devolucionGuardada;
    }
    public List<Devolucion> obtenerTodas() {
        return devolucionRepository.findAll();
    }
}