package com.empresa.sistemaventas.repository;

import com.empresa.sistemaventas.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Ajustado al nombre exacto de tu variable "username"
    Optional<Usuario> findByUsername(String username); 
}