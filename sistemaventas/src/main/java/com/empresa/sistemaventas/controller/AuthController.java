package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.dto.JwtResponseDTO;
import com.empresa.sistemaventas.dto.LoginDTO;
import com.empresa.sistemaventas.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    

    @PostMapping("/login")
    public ResponseEntity<?> crearTokenAutenticacion(@RequestBody LoginDTO loginRequest) {
        try {
            // 1. Validamos las credenciales
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Usuario o contraseña incorrectos", HttpStatus.UNAUTHORIZED);
        }

        // 2. Buscamos los datos del usuario
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

        // 3. Generamos el Token
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        // 4. Respondemos con el JSON que espera React
        return ResponseEntity.ok(new JwtResponseDTO(jwt, userDetails.getUsername()));
    }
    
}