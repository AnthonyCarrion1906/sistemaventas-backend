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
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> crearTokenAutenticacion(@RequestBody LoginDTO loginRequest) {
        
        System.out.println("======> ALERTA: INTENTO DE LOGIN RECIBIDO PARA: " + loginRequest.getUsername());

        try {
            // 1. Validamos las credenciales
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            System.out.println("======> ALERTA: CREDENCIALES VALIDADAS CORRECTAMENTE");
            
        } catch (BadCredentialsException e) {
            System.out.println("======> ALERTA: CONTRASEÑA INCORRECTA");
            return new ResponseEntity<>("Usuario o contraseña incorrectos", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // ESTA ES LA TRAMPA: Si falla la conexión a BD, caerá aquí y no dará 403
            System.out.println("======> ERROR CRÍTICO AL AUTENTICAR: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 2. Buscamos los datos del usuario
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

        // 3. Generamos el Token
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        // 4. Respondemos con el JSON que espera React
        return ResponseEntity.ok(new JwtResponseDTO(jwt, userDetails.getUsername()));
    }

    // Dejé el generar-hash por si lo necesitas luego, pero no afecta al login
    @GetMapping("/generar-hash")
    public String generarHash() {
        return passwordEncoder.encode("123456");
    }
}