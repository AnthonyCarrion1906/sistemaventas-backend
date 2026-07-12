package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.entity.Proveedor;
import com.empresa.sistemaventas.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    public List<Proveedor> obtenerTodos() {
        // Filtramos para devolver únicamente los proveedores activos
        return proveedorRepository.findAll().stream()
                .filter(proveedor -> Boolean.TRUE.equals(proveedor.getEstado()))
                .toList();
    }

    public Optional<Proveedor> obtenerPorId(Integer id) {
        // Mantenemos la búsqueda por ID intacta para el historial de compras antiguas
        return proveedorRepository.findById(id);
    }

    public List<Proveedor> buscar(String termino) {
        if (termino == null || termino.isBlank()) {
            return obtenerTodos(); // Reutilizamos la validación de activos
        }
        // Filtramos los resultados de la búsqueda para ocultar los inactivos
        return proveedorRepository.findByRazonSocialContainingIgnoreCaseOrRucContainingIgnoreCase(termino, termino).stream()
                .filter(proveedor -> Boolean.TRUE.equals(proveedor.getEstado()))
                .toList();
    }

    public Proveedor guardar(Proveedor proveedor) {
        if (proveedor.getRuc() != null && proveedorRepository.existsByRuc(proveedor.getRuc()) && proveedor.getId() == null) {
            throw new RuntimeException("El RUC ya está registrado para otro proveedor");
        }
        
        // Garantizamos que todo proveedor nuevo nazca activo por defecto
        if (proveedor.getEstado() == null) {
            proveedor.setEstado(true);
        }
        
        return proveedorRepository.save(proveedor);
    }

    public Proveedor actualizar(Integer id, Proveedor datos) {
        Proveedor existente = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
                
        // Se actualizaron los getters y setters a los nombres correctos
        existente.setRazonSocial(datos.getRazonSocial());
        existente.setRuc(datos.getRuc());
        existente.setDireccion(datos.getDireccion());
        existente.setTelefono(datos.getTelefono());
        existente.setCorreo(datos.getCorreo());
        existente.setContactoDirecto(datos.getContactoDirecto());
        
        // Se actualizó a categoriasSuministro (como texto simple)
        existente.setCategoriasSuministro(datos.getCategoriasSuministro());
        
        // Actualizamos el estado si es que se envía uno nuevo
        existente.setEstado(datos.getEstado() != null ? datos.getEstado() : existente.getEstado());
        
        return proveedorRepository.save(existente);
    }

    // NUEVO MÉTODO: Desactivación Lógica (Soft Delete)
    public void desactivar(Integer id) {
        Proveedor existente = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
                
        // Cambiamos el estado a false para desactivarlo de las vistas
        existente.setEstado(false); 
        proveedorRepository.save(existente);
    }
}