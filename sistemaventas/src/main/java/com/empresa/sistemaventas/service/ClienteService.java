package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.entity.Cliente;
import com.empresa.sistemaventas.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> obtenerPorId(Integer id) {
        return clienteRepository.findById(id);
    }

    public List<Cliente> buscar(String termino) {
        if (termino == null || termino.isBlank()) {
            return clienteRepository.findAll();
        }
        return clienteRepository.findByNombreRazonSocialContainingIgnoreCaseOrDocumentoContainingIgnoreCase(termino, termino);
    }

    public Cliente guardar(Cliente cliente) {
        if (cliente.getDocumento() != null && clienteRepository.existsByDocumento(cliente.getDocumento()) && cliente.getId() == null) {
            throw new RuntimeException("El documento ya está registrado para otro cliente");
        }
        return clienteRepository.save(cliente);
    }

    public Cliente actualizar(Integer id, Cliente datos) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
                
        // Se actualizan solo los campos que realmente existen en la tabla `clientes`
        existente.setNombreRazonSocial(datos.getNombreRazonSocial());
        existente.setDocumento(datos.getDocumento());
        existente.setTelefono(datos.getTelefono());
        existente.setCorreo(datos.getCorreo());
        
        // Eliminados setDireccion y setActivo
        
        return clienteRepository.save(existente);
    }
}