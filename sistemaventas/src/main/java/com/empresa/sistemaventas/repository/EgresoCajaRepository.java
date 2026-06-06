package com.empresa.sistemaventas.repository;

import com.empresa.sistemaventas.entity.EgresoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EgresoCajaRepository extends JpaRepository<EgresoCaja, Integer> {
    // Para listar en el frontend todos los gastos de un turno/caja específico
    List<EgresoCaja> findByCajaDiariaId(Integer cajaDiariaId);
}