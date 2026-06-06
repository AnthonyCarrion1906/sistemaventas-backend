package com.empresa.sistemaventas.repository;

import com.empresa.sistemaventas.entity.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Integer> {
    List<OrdenCompra> findByProveedorId(Integer proveedorId);
    List<OrdenCompra> findByEstado(String estado);
}