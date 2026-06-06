package com.empresa.sistemaventas.repository;

import com.empresa.sistemaventas.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    List<Cliente> findByNombreRazonSocialContainingIgnoreCaseOrDocumentoContainingIgnoreCase(String nombre, String documento);
    boolean existsByDocumento(String documento);
}