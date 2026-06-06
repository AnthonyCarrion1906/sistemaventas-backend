package com.empresa.sistemaventas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "orden_compra_detalles")
public class OrdenCompraDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "orden_compra_id", nullable = false)
    private OrdenCompra ordenCompra;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private BigDecimal cantidad = BigDecimal.ONE;

    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @Column(name = "cantidad_recibida", nullable = false)
    private BigDecimal cantidadRecibida = BigDecimal.ZERO;

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;
}