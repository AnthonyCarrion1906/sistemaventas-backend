package com.empresa.sistemaventas.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Corregido: de nombre_razon_social a razon_social
    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    @Column(nullable = false, length = 50, unique = true)
    private String ruc;

    @Column(length = 250)
    private String direccion;

    @Column(length = 100)
    private String telefono;

    @Column(length = 100)
    private String correo;

    @Column(name = "contacto_directo", length = 150)
    private String contactoDirecto;

    // Corregido: Ahora mapea directamente a la columna de la tabla como texto,
    // eliminando la configuración de tabla relacional (@ElementCollection)
    @Column(name = "categorias_suministro")
    private String categoriasSuministro;

    // Eliminado: El campo "activo" porque no existe en MariaDB
}