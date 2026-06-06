package com.empresa.sistemaventas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Cambiado para coincidir con la base de datos
    @Column(name = "subcategoria_id")
    private Integer subcategoriaId; 

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "unidad_principal", length = 50)
    private String unidadPrincipal;

    @Column(name = "unidad_secundaria", length = 50)
    private String unidadSecundaria;

    @Column(name = "factor_conversion")
    private BigDecimal factorConversion = BigDecimal.ONE;

    @Column(name = "stock_actual", nullable = false)
    private BigDecimal stockActual = BigDecimal.ZERO;

    @Column(name = "stock_minimo", nullable = false)
    private BigDecimal stockMinimo = BigDecimal.ZERO;

    @Column(name = "costo_promedio")
    private BigDecimal costoPromedio = BigDecimal.ZERO;

    // Renombrado a "estado" para coincidir con la BD. 
    // Asumo que en MariaDB lo creaste como TINYINT(1) para booleanos.
    @Column(name = "estado", nullable = false)
    private Boolean estado = true; 
}