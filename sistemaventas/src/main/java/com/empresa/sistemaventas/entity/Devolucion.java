package com.empresa.sistemaventas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "devoluciones")
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private BigDecimal cantidad;

    @Column(nullable = false, length = 255)
    private String motivo;

    // Usamos Boolean para mapear el tinyint(1) de MariaDB
    @Column(name = "retorno_stock", nullable = false)
    private Boolean retornoStock;

    @Column(name = "impacto_economico", nullable = false)
    private BigDecimal impactoEconomico;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
}