package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.constant.MetodoPago;
import com.empresa.sistemaventas.entity.*;
import com.empresa.sistemaventas.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProformaService proformaService;

    @Autowired
    private CajaDiariaService cajaDiariaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private KardexService kardexService;

    public List<Venta> obtenerTodas() {
        return ventaRepository.findAll();
    }

    // --- ESTE ES EL MÉTODO QUE FALTABA PARA LAS DEVOLUCIONES ---
    public Optional<Venta> obtenerPorId(Integer id) {
        return ventaRepository.findById(id);
    }
    // ------------------------------------------------------------

    @Transactional
    public Venta convertirProformaEnVenta(Integer proformaId, String metodoPago, BigDecimal tipoCambio) {
        Proforma proforma = proformaService.obtenerPorId(proformaId)
                .orElseThrow(() -> new RuntimeException("Proforma no encontrada"));

        if (Boolean.TRUE.equals(proforma.getEsFinal())) {
            throw new RuntimeException("La proforma ya fue convertida en venta");
        }

        // Toda venta requiere caja abierta para trazabilidad completa de ingresos
        CajaDiaria cajaActiva = cajaDiariaService.obtenerCajaAbiertaHoy()
                .orElseThrow(() -> new RuntimeException(
                        "No hay una caja diaria abierta. "
                        + "Debe abrir la caja antes de registrar cualquier venta."
                ));

        // 1. Validar stock acumulado de todos los productos en la proforma
        Map<Integer, BigDecimal> cantidadesPorProducto = new HashMap<>();
        if (proforma.getDetalles() != null) {
            for (ProformaDetalle detalle : proforma.getDetalles()) {
                if (detalle.getProductoId() != null) {
                    cantidadesPorProducto.merge(
                            detalle.getProductoId(), 
                            detalle.getCantidad() != null ? detalle.getCantidad() : BigDecimal.ZERO, 
                            BigDecimal::add
                    );
                }
            }
        }

        for (Map.Entry<Integer, BigDecimal> entry : cantidadesPorProducto.entrySet()) {
            Integer productoId = entry.getKey();
            BigDecimal cantidadRequerida = entry.getValue();

            Producto producto = productoService.obtenerPorId(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado (ID: " + productoId + ")"));

            BigDecimal stockActual = producto.getStockActual() != null ? producto.getStockActual() : BigDecimal.ZERO;

            if (stockActual.compareTo(cantidadRequerida) < 0) {
                throw new RuntimeException("Stock insuficiente para el producto '" + producto.getNombre() 
                        + "'. Stock disponible: " + stockActual + ", Solicitado: " + cantidadRequerida);
            }
        }

        // Resolver el tipo de cambio efectivo
        // Prioridad: 1. tipoCambio recibido explícitamente -> 2. tipoCambio guardado en la Proforma -> 3. Fallback 1.0
        boolean esUSD = "USD".equalsIgnoreCase(proforma.getMoneda());
        BigDecimal tcEfectivo = BigDecimal.ONE;
        if (esUSD) {
            if (tipoCambio != null && tipoCambio.compareTo(BigDecimal.ZERO) > 0) {
                tcEfectivo = tipoCambio;
            } else if (proforma.getTipoCambio() != null && proforma.getTipoCambio().compareTo(BigDecimal.ZERO) > 0) {
                tcEfectivo = proforma.getTipoCambio();
            }
        }

        Venta venta = new Venta();
        venta.setProforma(proforma);
        venta.setFecha(proforma.getFecha());
        venta.setMetodoPago(metodoPago);

        // Asignamos el id del usuario que hizo la proforma
        venta.setUsuarioId(proforma.getUsuarioId());

        // El total se guarda en la moneda original de la proforma (para el comprobante)
        venta.setTotal(proforma.getTotal());

        // Guardamos el total equivalente siempre en Soles (PEN)
        venta.setTotalPen(proforma.getTotal().multiply(tcEfectivo));

        // Guardamos el tipo de cambio usado (null si fue en PEN)
        venta.setTipoCambio(esUSD ? tcEfectivo : null);

        venta.setUtilidad(BigDecimal.ZERO);

        // 2. Crear detalles de venta, registrar Kardex y descontar stock
        if (proforma.getDetalles() != null) {
            for (ProformaDetalle detalle : proforma.getDetalles()) {
                VentaDetalle ventaDetalle = new VentaDetalle();
                ventaDetalle.setVenta(venta);
                ventaDetalle.setProductoId(detalle.getProductoId());
                ventaDetalle.setServicioId(detalle.getServicioId());
                ventaDetalle.setDescripcion(detalle.getDescripcion());
                ventaDetalle.setCantidad(detalle.getCantidad());
                ventaDetalle.setPrecioUnitario(detalle.getPrecioUnitario());
                ventaDetalle.setSubtotal(detalle.getSubtotal());
                venta.getDetalles().add(ventaDetalle);

                if (detalle.getProductoId() != null) {
                    Producto producto = productoService.obtenerPorId(detalle.getProductoId()).get();

                    // Registrar movimiento de SALIDA en Kardex
                    kardexService.registrarMovimiento(
                            producto,
                            detalle.getCantidad(),
                            "SALIDA",
                            "Venta de Proforma #" + proforma.getId()
                    );

                    // Descontar del stock actual
                    BigDecimal nuevoStock = producto.getStockActual().subtract(detalle.getCantidad());
                    producto.setStockActual(nuevoStock);
                    productoService.guardar(producto);

                    // Acumular utilidad en PEN:
                    // precio de venta en PEN - costo promedio en PEN
                    // Si el costoPromedio del producto está en USD, se convierte antes de restar.
                    BigDecimal precioVentaPen = detalle.getPrecioUnitario().multiply(tcEfectivo);
                    BigDecimal costoUnitario  = producto.getCostoPromedio() != null ? producto.getCostoPromedio() : BigDecimal.ZERO;
                    boolean costoEsUSD = "USD".equalsIgnoreCase(producto.getMonedaCosto());
                    BigDecimal costoUnitarioPen = costoEsUSD ? costoUnitario.multiply(tcEfectivo) : costoUnitario;
                    BigDecimal costoTotalPen  = costoUnitarioPen.multiply(detalle.getCantidad());
                    BigDecimal utilidadItem   = precioVentaPen.multiply(detalle.getCantidad()).subtract(costoTotalPen);
                    venta.setUtilidad(venta.getUtilidad().add(utilidadItem));
                }
            }
        }

        proforma.setEstado("CERRADA");
        proforma.setEsFinal(true);

        Venta guardada = ventaRepository.save(venta);

        // Acumular ingreso en la caja SIEMPRE en PEN:
        //   - Si la proforma fue en USD, se convierte con el tipo de cambio
        //   - total_esperado: siempre (todos los métodos de pago)
        //   - monto_fisico_real: solo EFECTIVO (dinero físico que entra a la caja)
        BigDecimal totalEnPen = proforma.getTotal().multiply(tcEfectivo);
        cajaDiariaService.registrarIngreso(
                cajaActiva.getId(),
                totalEnPen,
                MetodoPago.afectaEfectivoFisico(metodoPago)
        );

        return guardada;
    }

}