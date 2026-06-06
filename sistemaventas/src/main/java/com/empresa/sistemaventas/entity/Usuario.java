package com.empresa.sistemaventas.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Cambiado para que coincida con la columna rol_id de la BD
    @Column(name = "rol_id", nullable = false)
    private Integer rolId;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(nullable = false, length = 120, unique = true)
    private String username;

    // Corregido: mapeado a password_hash
    @Column(name = "password_hash", length = 150)
    private String contrasena;

    // Corregido: de activo a estado
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
    
    // Eliminado: El campo correo ya que no existe en tu tabla
}