package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.entity.ProductoProveedor;
import com.empresa.sistemaventas.repository.ProductoProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoProveedorService {

    @Autowired
    private ProductoProveedorRepository repository;

    // --- MÉTODO AGREGADO PARA SOLUCIONAR EL ERROR 405 EN POSTMAN ---
    public List<ProductoProveedor> obtenerTodos() {
        return repository.findAll();
    }
    // ----------------------------------------------------------------

    public List<ProductoProveedor> obtenerPorProveedor(Integer proveedorId) {
        return repository.findByProveedorId(proveedorId);
    }

    public List<ProductoProveedor> obtenerPorProducto(Integer productoId) {
        return repository.findByProductoId(productoId);
    }

    public List<ProductoProveedor> obtenerComparativaPrecios(Integer productoId) {
        return repository.findByProductoIdOrderByCostoPactadoAsc(productoId);
    }

    public ProductoProveedor guardar(ProductoProveedor registro) {
        return repository.save(registro);
    }
}