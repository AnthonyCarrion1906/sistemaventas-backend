package com.empresa.sistemaventas.security;

import com.empresa.sistemaventas.entity.Usuario;
import com.empresa.sistemaventas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Además, bloqueamos el acceso si el estado del usuario es falso (inactivo)
        if (!Boolean.TRUE.equals(usuario.getEstado())) {
            throw new RuntimeException("El usuario está inactivo en el sistema");
        }

        // Ajustado a tus variables reales: getUsername() y getContrasena()
        return new User(usuario.getUsername(), usuario.getContrasena(), new ArrayList<>());
    }
}