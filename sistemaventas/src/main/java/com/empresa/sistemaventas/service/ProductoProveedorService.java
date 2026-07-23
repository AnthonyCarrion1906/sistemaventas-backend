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

    public ProductoProveedor actualizar(Integer id, ProductoProveedor datos) {
        ProductoProveedor existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro Producto-Proveedor no encontrado con ID: " + id));

        if (datos.getProducto() != null) existente.setProducto(datos.getProducto());
        if (datos.getProveedor() != null) existente.setProveedor(datos.getProveedor());
        if (datos.getCodigoFabrica() != null) existente.setCodigoFabrica(datos.getCodigoFabrica());
        if (datos.getCostoPactado() != null) existente.setCostoPactado(datos.getCostoPactado());
        if (datos.getEsPrincipal() != null) existente.setEsPrincipal(datos.getEsPrincipal());
        if (datos.getMoneda() != null) existente.setMoneda(datos.getMoneda());

        return repository.save(existente);
    }

    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Registro Producto-Proveedor no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }
}