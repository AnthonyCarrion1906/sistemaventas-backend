package com.empresa.sistemaventas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "auditoria_precios")
public class AuditoriaPrecio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "precio_original", nullable = false)
    private BigDecimal precioOriginal;

    @Column(name = "precio_modificado", nullable = false)
    private BigDecimal precioModificado;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String justificacion;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
}