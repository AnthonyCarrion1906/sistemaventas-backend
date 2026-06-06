package com.empresa.sistemaventas.dto;

import lombok.Data;

@Data
public class LoginDTO {
    // Puedes cambiar "username" por "email" si en tu BD el usuario entra con su correo
    private String username; 
    private String password;
}