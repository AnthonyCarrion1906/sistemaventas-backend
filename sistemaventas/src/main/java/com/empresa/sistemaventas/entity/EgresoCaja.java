package com.empresa.sistemaventas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "egresos_caja")
public class EgresoCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "caja_diaria_id", nullable = false)
    private CajaDiaria cajaDiaria;

    @Column(nullable = false)
    private BigDecimal monto;

    // Alineado a 255 caracteres según tu tabla SQL
    @Column(nullable = false, length = 255)
    private String motivo;

    @Column(nullable = false, length = 100)
    private String categoria;

    // CORREGIDO: Mapeado exactamente a "ticket_correlativo" como en la BD
    @Column(name = "ticket_correlativo", nullable = false, length = 50)
    private String ticketCorrelativo;

    // FALTABA: El campo fecha para saber a qué hora salió el dinero
    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
}