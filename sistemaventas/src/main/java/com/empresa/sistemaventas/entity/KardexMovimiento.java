package com.empresa.sistemaventas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime; // <-- CORREGIDO: Usamos LocalDateTime en lugar de LocalDate

@Data
@Entity
@Table(name = "kardex")
public class KardexMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // Cambiado de Enum a String para que coincida con MariaDB
    @Column(name = "tipo_movimiento", nullable = false, length = 10)
    private String tipoMovimiento;

    @Column(nullable = false)
    private BigDecimal cantidad;

    @Column(name = "stock_inicial", nullable = false)
    private BigDecimal stockInicial;

    @Column(name = "stock_final", nullable = false)
    private BigDecimal stockFinal;

    @Column(name = "documento_referencia", length = 100)
    private String documentoReferencia;

    // CORREGIDO: Mapeado con LocalDateTime para guardar hora y minutos
    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now(); 
}