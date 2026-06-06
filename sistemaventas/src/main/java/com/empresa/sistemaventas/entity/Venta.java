package com.empresa.sistemaventas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "proforma_id", nullable = false)
    private Proforma proforma;

    // Agregado para coincidir con la BD
    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(nullable = false)
    private LocalDate fecha = LocalDate.now();

    @Column(name = "metodo_pago", length = 100)
    private String metodoPago;

    // Corregido: de totalVenta a total
    @Column(name = "total")
    private BigDecimal total;

    // Agregado para coincidir con la BD
    @Column(name = "utilidad")
    private BigDecimal utilidad;

    // ELIMINADO: private String comprobante; (No existe en tu tabla)

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaDetalle> detalles = new ArrayList<>();
}