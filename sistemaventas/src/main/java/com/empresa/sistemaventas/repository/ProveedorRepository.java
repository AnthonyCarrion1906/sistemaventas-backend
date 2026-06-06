package com.empresa.sistemaventas.repository;

import com.empresa.sistemaventas.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    List<Proveedor> findByRazonSocialContainingIgnoreCaseOrRucContainingIgnoreCase(String razonSocial, String ruc);
    boolean existsByRuc(String ruc);
}