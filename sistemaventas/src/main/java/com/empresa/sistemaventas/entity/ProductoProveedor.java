package com.empresa.sistemaventas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "producto_proveedor")
public class ProductoProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    // Corregido: de codigo_proveedor a codigo_fabrica
    @Column(name = "codigo_fabrica", length = 120)
    private String codigoFabrica;

    // Corregido: de precio_costo a costo_pactado
    @Column(name = "costo_pactado", nullable = false)
    private BigDecimal costoPactado;

    // Corregido: se mapeó explícitamente a es_principal
    @Column(name = "es_principal", nullable = false)
    private Boolean esPrincipal = false;
}