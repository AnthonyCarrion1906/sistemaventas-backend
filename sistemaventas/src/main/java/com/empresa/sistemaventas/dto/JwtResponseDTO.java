package com.empresa.sistemaventas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponseDTO {
    private String token;
    private String username;
    // Más adelante puedes agregar aquí el ROL del usuario para que React sepa qué menús mostrarle
}