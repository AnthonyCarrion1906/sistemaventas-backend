package com.empresa.sistemaventas.repository;

import com.empresa.sistemaventas.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Integer> {
    // Cambia findByActivoTrue() por esto:
    List<Servicio> findByEstadoTrue();
    boolean existsByNombre(String nombre);
}