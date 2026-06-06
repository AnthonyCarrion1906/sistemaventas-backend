package com.empresa.sistemaventas.repository;

import com.empresa.sistemaventas.entity.ProductoProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoProveedorRepository extends JpaRepository<ProductoProveedor, Integer> {
    List<ProductoProveedor> findByProveedorId(Integer proveedorId);
    List<ProductoProveedor> findByProductoId(Integer productoId);
    List<ProductoProveedor> findByProductoIdOrderByCostoPactadoAsc(Integer productoId);
}