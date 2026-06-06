package com.empresa.sistemaventas.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServicioDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private BigDecimal precioBase;
    private Boolean activo;
}