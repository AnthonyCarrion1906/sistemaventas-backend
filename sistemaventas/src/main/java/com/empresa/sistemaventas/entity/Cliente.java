package com.empresa.sistemaventas.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre_razon_social", nullable = false, length = 150)
    private String nombreRazonSocial;

    @Column(nullable = false, length = 50, unique = true)
    private String documento;

    @Column(length = 100)
    private String telefono;

    @Column(length = 100)
    private String correo;

    // Eliminados: direccion y activo porque no existen en la BD
}