package com.empresa.sistemaventas.repository;

import com.empresa.sistemaventas.entity.Devolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DevolucionRepository extends JpaRepository<Devolucion, Integer> {

    // Para buscar todas las devoluciones asociadas a una venta específica
    List<Devolucion> findByVentaId(Integer ventaId);

    // ── Total devoluciones e impacto económico en un rango de fechas ─
    @Query("SELECT COALESCE(SUM(d.impactoEconomico), 0) " +
           "FROM Devolucion d WHERE d.fecha BETWEEN :desde AND :hasta")
    BigDecimal sumImpactoEntreFechas(@Param("desde") LocalDateTime desde,
                                     @Param("hasta") LocalDateTime hasta);

    @Query("SELECT COUNT(d) FROM Devolucion d WHERE d.fecha BETWEEN :desde AND :hasta")
    long countEntreFechas(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}