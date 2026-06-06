package com.empresa.sistemaventas.controller;

import com.empresa.sistemaventas.dto.JwtResponseDTO;
import com.empresa.sistemaventas.dto.LoginDTO;
import com.empresa.sistemaventas.security.JwtUtil;
import com.empresa.sistemaventas.entity.Usuario;             // <-- AGREGA ESTO
import com.empresa.sistemaventas.repository.UsuarioRepository; // <-- AGREGA ESTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder; // <-- AGREGA ESTO
import org.springframework.web.bind.annotation.*;