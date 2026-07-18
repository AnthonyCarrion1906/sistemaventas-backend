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
        // Filtramos la lista completa para devolver únicamente los productos activos
        return productoRepository.findAll().stream()
                .filter(producto -> Boolean.TRUE.equals(producto.getEstado()))
                .toList();
    }

    public Optional<Producto> obtenerPorId(Integer id) {
        // Mantenemos la búsqueda por ID intacta. Si consultas un comprobante antiguo, 
        // necesitas que el producto aparezca aunque haya sido desactivado recientemente.
        return productoRepository.findById(id);
    }

    public List<Producto> buscar(String termino) {
        if (termino == null || termino.isBlank()) {
            return obtenerTodos(); // Reutilizamos la validación de activos
        }
        // Filtramos los resultados de la búsqueda para ocultar los desactivados
        return productoRepository.findByNombreContainingIgnoreCaseOrCodigoContainingIgnoreCase(termino, termino).stream()
                .filter(producto -> Boolean.TRUE.equals(producto.getEstado()))
                .toList();
    }

    public Producto guardar(Producto producto) {
        // Validamos que el código no esté duplicado
        if (producto.getCodigo() == null || producto.getCodigo().isBlank()) {
            throw new RuntimeException("El código del producto es obligatorio");
        }
        if (productoRepository.findByCodigo(producto.getCodigo().trim()).isPresent()) {
            throw new RuntimeException("Ya existe un producto registrado con el código: " + producto.getCodigo());
        }
        // Garantizamos que todo producto nuevo nazca activo por defecto
        if (producto.getEstado() == null) {
            producto.setEstado(true);
        }
        producto.setCodigo(producto.getCodigo().trim());
        return productoRepository.save(producto);
    }

    public Producto actualizar(Integer id, Producto datos) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        if (datos.getCodigo() == null || datos.getCodigo().isBlank()) {
            throw new RuntimeException("El código del producto es obligatorio");
        }

        // Si se cambia el código, validar que no colisione con otro producto
        if (!existente.getCodigo().equalsIgnoreCase(datos.getCodigo().trim())) {
            if (productoRepository.findByCodigo(datos.getCodigo().trim()).isPresent()) {
                throw new RuntimeException("Ya existe otro producto registrado con el código: " + datos.getCodigo());
            }
            existente.setCodigo(datos.getCodigo().trim());
        }

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


    // NUEVO MÉTODO: Desactivación Lógica (Soft Delete)
    public void desactivar(Integer id) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                
        existente.setEstado(false); // Cambia el estado a inactivo
        productoRepository.save(existente);
    }

    public List<Producto> obtenerProductosParaReponer() {
        return productoRepository.findAll().stream()
                .filter(producto -> Boolean.TRUE.equals(producto.getEstado())) // No reponer productos descontinuados
                .filter(producto -> producto.getStockActual() != null && producto.getStockMinimo() != null)
                .filter(producto -> producto.getStockActual().compareTo(producto.getStockMinimo()) <= 0)
                .toList();
    }
}