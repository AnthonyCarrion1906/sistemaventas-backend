package com.empresa.sistemaventas.repository;

import com.empresa.sistemaventas.entity.KardexMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface KardexRepository extends JpaRepository<KardexMovimiento, Integer> {
    List<KardexMovimiento> findByProductoIdAndFechaBetween(Integer productoId, LocalDate desde, LocalDate hasta);
    List<KardexMovimiento> findByProductoId(Integer productoId);
}
