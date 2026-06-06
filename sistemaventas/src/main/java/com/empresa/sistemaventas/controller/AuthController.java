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

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    

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
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin() {
        Usuario admin = new Usuario();
        admin.setUsername("admin");
        admin.setContrasena(passwordEncoder.encode("123456")); // Usa el campo 'contrasena'
        admin.setNombreCompleto("Administrador");
        admin.setEstado(true); // Tu entidad usa Boolean, no Integer
        admin.setRolId(1);    // Asumimos que el 1 es el ID del rol ADMIN
        
        usuarioRepository.save(admin);
        return ResponseEntity.ok("Admin creado correctamente");
    }
    
}