package com.empresa.sistemaventas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "proformas")
public class Proforma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Agregado para coincidir con la BD
    @Column(name = "usuario_id")
    private Integer usuarioId;

    // Agregado para coincidir con la BD
    @Column(name = "codigo_correlativo", length = 50)
    private String codigoCorrelativo;

    // Renombrado de versionNumero a version
    @Column(name = "version")
    private Integer version = 1;

    // Renombrado de finalizada a es_final
    @Column(name = "es_final", nullable = false)
    private Boolean esFinal = false;

    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE";

    // Renombrado de totalAntesDescuento a subtotal
    @Column(name = "subtotal")
    private BigDecimal subtotal = BigDecimal.ZERO;

    // Unificado a una sola columna de descuento
    @Column(name = "descuento")
    private BigDecimal descuento = BigDecimal.ZERO;

    // Renombrado de totalFinal a total
    @Column(name = "total")
    private BigDecimal total = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDate fecha = LocalDate.now();

    @OneToMany(mappedBy = "proforma", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProformaDetalle> detalles = new ArrayList<>();
}