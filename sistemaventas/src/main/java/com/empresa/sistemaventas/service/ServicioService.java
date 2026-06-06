package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.entity.Servicio;
import com.empresa.sistemaventas.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    public List<Servicio> obtenerTodas() {
        return servicioRepository.findAll();
    }

    public List<Servicio> obtenerActivos() {
        // Corregido: Ahora busca por la propiedad 'estado' en lugar de 'activo'
        return servicioRepository.findByEstadoTrue();
    }

    public Optional<Servicio> obtenerPorId(Integer id) {
        return servicioRepository.findById(id);
    }

    public Servicio guardar(Servicio servicio) {
        if (servicio.getNombre() != null && servicioRepository.existsByNombre(servicio.getNombre()) && servicio.getId() == null) {
            throw new RuntimeException("Ya existe un servicio con ese nombre");
        }
        return servicioRepository.save(servicio);
    }

    public Servicio actualizar(Integer id, Servicio datos) {
        Servicio existente = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
                
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        
        // Corregido: de PrecioBase a PrecioBaseReferencial
        existente.setPrecioBaseReferencial(datos.getPrecioBaseReferencial());
        
        // Corregido: de Activo a Estado
        existente.setEstado(datos.getEstado() != null ? datos.getEstado() : existente.getEstado());
        
        return servicioRepository.save(existente);
    }

    // Corregido: el parámetro 'activo' pasó a llamarse 'estado'
    public Servicio cambiarEstado(Integer id, Boolean estado) {
        Servicio existente = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
                
        existente.setEstado(estado);
        return servicioRepository.save(existente);
    }
}