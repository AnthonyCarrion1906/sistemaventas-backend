package com.empresa.sistemaventas.repository;

import com.empresa.sistemaventas.entity.AuditoriaPrecio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaPrecioRepository extends JpaRepository<AuditoriaPrecio, Integer> {
    // Para ver el historial de cambios de un producto en específico
    List<AuditoriaPrecio> findByProductoId(Integer productoId);
}