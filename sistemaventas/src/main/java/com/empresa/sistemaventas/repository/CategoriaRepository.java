package com.empresa.sistemaventas.repository;

import com.empresa.sistemaventas.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    // Spring Data JPA crea la consulta SQL automáticamente solo con nombrar bien el método
    boolean existsByNombre(String nombre);
}