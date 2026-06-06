package com.empresa.sistemaventas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "cajas_diarias")
public class CajaDiaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "monto_inicial", nullable = false)
    private BigDecimal montoInicial = BigDecimal.ZERO;

    @Column(name = "total_esperado")
    private BigDecimal totalEsperado = BigDecimal.ZERO;

    @Column(name = "monto_fisico_real")
    private BigDecimal montoFisicoReal = BigDecimal.ZERO;

    // Asumiendo que 'estado' es un VARCHAR (ej. ABIERTA / CERRADA)
    // Si en tu BD es un TINYINT, cambia String a Boolean
    @Column(length = 20)
    private String estado = "ABIERTA"; 

    @Column(name = "fecha_apertura")
    private LocalDateTime fechaApertura;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @OneToMany(mappedBy = "cajaDiaria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EgresoCaja> egresos = new ArrayList<>();
}