package com.empresa.sistemaventas.repository;

import com.empresa.sistemaventas.entity.Devolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevolucionRepository extends JpaRepository<Devolucion, Integer> {
    // Para buscar todas las devoluciones asociadas a una venta específica
    List<Devolucion> findByVentaId(Integer ventaId);
}