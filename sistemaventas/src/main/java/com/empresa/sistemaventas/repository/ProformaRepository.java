package com.empresa.sistemaventas.repository;

import com.empresa.sistemaventas.entity.Proforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProformaRepository extends JpaRepository<Proforma, Integer> {
    List<Proforma> findByClienteId(Integer clienteId);
}