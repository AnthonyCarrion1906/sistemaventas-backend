package com.empresa.sistemaventas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "ordenes_compra")
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    // Agregado para coincidir con la BD. Uso Integer temporalmente 
    // a menos que tengas una entidad Usuario ya creada.
    @Column(name = "usuario_id")
    private Integer usuarioId;

     @Column(length = 20)
    private String estado = "PENDIENTE";

    @Column(name = "comprobante_url", length = 255)
    private String comprobanteUrl;

    @Column(name = "nro_comprobante", length = 50)
    private String nroComprobante;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision = LocalDate.now();

    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenCompraDetalle> detalles = new ArrayList<>();
    
    // Eliminados: proforma, totalCompra, y todos los campos de byte[] comprobante.
}