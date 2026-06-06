package com.empresa.sistemaventas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "servicios")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    // Corregido: de precio_base a precio_base_referencial
    @Column(name = "precio_base_referencial")
    private BigDecimal precioBaseReferencial;

    // Corregido: de activo a estado
    @Column(name = "estado", nullable = false)
    private Boolean estado = true; 
}