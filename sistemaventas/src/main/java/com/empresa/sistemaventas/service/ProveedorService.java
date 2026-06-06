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
        return proveedorRepository.findAll();
    }

    public Optional<Proveedor> obtenerPorId(Integer id) {
        return proveedorRepository.findById(id);
    }

    public List<Proveedor> buscar(String termino) {
        if (termino == null || termino.isBlank()) {
            return proveedorRepository.findAll();
        }
        // ATENCIÓN AQUÍ: Se cambió el nombre del método de NombreRazonSocial a RazonSocial
        return proveedorRepository.findByRazonSocialContainingIgnoreCaseOrRucContainingIgnoreCase(termino, termino);
    }

    public Proveedor guardar(Proveedor proveedor) {
        if (proveedor.getRuc() != null && proveedorRepository.existsByRuc(proveedor.getRuc()) && proveedor.getId() == null) {
            throw new RuntimeException("El RUC ya está registrado para otro proveedor");
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
        
        // Se eliminó la línea de setActivo(...)
        
        return proveedorRepository.save(existente);
    }
}