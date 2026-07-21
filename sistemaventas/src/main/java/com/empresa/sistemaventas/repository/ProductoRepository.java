package com.empresa.sistemaventas.repository;

import com.empresa.sistemaventas.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByNombreContainingIgnoreCaseOrCodigoContainingIgnoreCase(String nombre, String codigo);
    List<Producto> findByStockActualLessThanEqual(BigDecimal stockMinimo);
    java.util.Optional<Producto> findByCodigo(String codigo);

    // ── Para dashboard ────────────────────────────────────────────────
    long countByEstadoTrue();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.estado = true AND p.stockActual <= p.stockMinimo")
    long countProductosConStockBajo();
}
