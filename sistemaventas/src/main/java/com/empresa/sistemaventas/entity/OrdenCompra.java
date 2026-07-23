package com.empresa.sistemaventas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "ordenes_compra")
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    // Agregado para coincidir con la BD. Uso Integer temporalmente
    // a menos que tengas una entidad Usuario ya creada.
    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(length = 20)
    private String estado = "PENDIENTE";

    @Column(name = "comprobante_url", length = 255)
    private String comprobanteUrl;

    @Column(name = "nro_comprobante", length = 50)
    private String nroComprobante;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision = LocalDate.now();

    // Moneda en que se pactó la orden de compra: "PEN" o "USD"
    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda = "PEN";

    // Tipo de cambio PEN/USD al momento de la orden. Solo se usa si moneda = "USD".
    @Column(name = "tipo_cambio", precision = 10, scale = 4)
    private BigDecimal tipoCambio;

    // Total en la moneda original de la orden (PEN o USD)
    @Column(name = "total")
    private BigDecimal total = BigDecimal.ZERO;

    // Total en Soles (PEN) siempre guardado para reportes y egresos
    @Column(name = "total_pen")
    private BigDecimal totalPen = BigDecimal.ZERO;

    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenCompraDetalle> detalles = new ArrayList<>();

    // Eliminados: proforma, totalCompra, y todos los campos de byte[] comprobante.
}