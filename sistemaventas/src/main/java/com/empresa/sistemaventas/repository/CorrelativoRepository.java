package com.empresa.sistemaventas.repository;

import com.empresa.sistemaventas.entity.Correlativo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorrelativoRepository extends JpaRepository<Correlativo, Integer> {

    /**
     * Busca el correlativo por tipo de documento con LOCK PESIMISTA
     * (SELECT ... FOR UPDATE en MariaDB/MySQL).
     * Garantiza que dos transacciones simultáneas no lean el mismo número.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Correlativo c WHERE c.tipo = :tipo")
    Optional<Correlativo> findByTipoWithLock(@Param("tipo") String tipo);
}
