package com.empresa.sistemaventas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RucResponseDto {
    @JsonProperty("razon_social")
    private String razonSocial;

    @JsonProperty("numero_documento")
    private String numeroDocumento;

    @JsonProperty("estado")
    private String estado;

    @JsonProperty("condicion")
    private String condicion;

    @JsonProperty("direccion")
    private String direccion;

    @JsonProperty("ubigeo")
    private String ubigeo;

    @JsonProperty("distrito")
    private String distrito;

    @JsonProperty("provincia")
    private String provincia;

    @JsonProperty("departamento")
    private String departamento;
}
