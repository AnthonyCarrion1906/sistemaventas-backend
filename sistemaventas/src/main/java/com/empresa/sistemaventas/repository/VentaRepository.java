package com.empresa.sistemaventas.repository;

import com.empresa.sistemaventas.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    // ── Total ventas (monto) en un rango de fechas ──────────────────
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.fecha BETWEEN :desde AND :hasta")
    BigDecimal sumTotalEntreFechas(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    // ── Conteo de ventas en un rango de fechas ───────────────────────
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.fecha BETWEEN :desde AND :hasta")
    long countEntreFechas(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    // ── Total S/ agrupado por mes para el año actual ─────────────────
    // Devuelve List<Object[]> con [mes(int), total(BigDecimal)]
    @Query("SELECT MONTH(v.fecha), COALESCE(SUM(v.total), 0) " +
           "FROM Venta v WHERE YEAR(v.fecha) = :anio " +
           "GROUP BY MONTH(v.fecha) ORDER BY MONTH(v.fecha)")
    List<Object[]> sumTotalPorMesAnio(@Param("anio") int anio);

    // ── Total S/ por método de pago en un rango de fechas ───────────
    @Query("SELECT v.metodoPago, COALESCE(SUM(v.total), 0) " +
           "FROM Venta v WHERE v.fecha BETWEEN :desde AND :hasta " +
           "GROUP BY v.metodoPago")
    List<Object[]> sumTotalPorMetodoPago(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    // ── Totales por día en un rango de fechas (para gráfico semanal) ─
    @Query("SELECT v.fecha, COALESCE(SUM(v.total), 0), COUNT(v) " +
           "FROM Venta v WHERE v.fecha BETWEEN :desde AND :hasta " +
           "GROUP BY v.fecha ORDER BY v.fecha")
    List<Object[]> sumTotalYConteoByFecha(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
}