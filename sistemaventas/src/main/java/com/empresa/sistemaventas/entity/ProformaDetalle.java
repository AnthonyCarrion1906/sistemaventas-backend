package com.empresa.sistemaventas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "proforma_detalles")
public class ProformaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "proforma_id", nullable = false)
    private Proforma proforma;

    @Column(name = "tipo_item", nullable = false)
    private String tipoItem;

    @Column(name = "producto_id")
    private Integer productoId;

    @Column(name = "servicio_id")
    private Integer servicioId;

    @Column(name = "descripcion", length = 250)
    private String descripcion;

    @Column(nullable = false)
    private BigDecimal cantidad = BigDecimal.ONE;

    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;
}