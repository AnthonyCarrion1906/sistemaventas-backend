package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.entity.Producto;
import com.empresa.sistemaventas.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorId(Integer id) {
        return productoRepository.findById(id);
    }

    public List<Producto> buscar(String termino) {
        if (termino == null || termino.isBlank()) {
            return productoRepository.findAll();
        }
        return productoRepository.findByNombreContainingIgnoreCase(termino);
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizar(Integer id, Producto datos) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        // Se actualizan solo los campos que existen en la BD
        existente.setNombre(datos.getNombre());
        existente.setSubcategoriaId(datos.getSubcategoriaId()); 
        existente.setStockActual(datos.getStockActual() != null ? datos.getStockActual() : existente.getStockActual());
        existente.setStockMinimo(datos.getStockMinimo() != null ? datos.getStockMinimo() : existente.getStockMinimo());
        existente.setUnidadPrincipal(datos.getUnidadPrincipal());
        existente.setUnidadSecundaria(datos.getUnidadSecundaria());
        existente.setFactorConversion(datos.getFactorConversion() != null ? datos.getFactorConversion() : existente.getFactorConversion());
        existente.setCostoPromedio(datos.getCostoPromedio() != null ? datos.getCostoPromedio() : existente.getCostoPromedio());
        existente.setEstado(datos.getEstado() != null ? datos.getEstado() : existente.getEstado());
        
        return productoRepository.save(existente);
    }

    public List<Producto> obtenerProductosParaReponer() {
        return productoRepository.findAll().stream()
                .filter(producto -> producto.getStockActual() != null && producto.getStockMinimo() != null)
                .filter(producto -> producto.getStockActual().compareTo(producto.getStockMinimo()) <= 0)
                .toList();
    }
}