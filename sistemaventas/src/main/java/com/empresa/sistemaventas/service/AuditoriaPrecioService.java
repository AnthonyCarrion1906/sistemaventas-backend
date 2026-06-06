package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.entity.AuditoriaPrecio;
import com.empresa.sistemaventas.entity.Producto;
import com.empresa.sistemaventas.repository.AuditoriaPrecioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AuditoriaPrecioService {

    @Autowired
    private AuditoriaPrecioRepository auditoriaPrecioRepository;

    public List<AuditoriaPrecio> obtenerHistorialPorProducto(Integer productoId) {
        return auditoriaPrecioRepository.findByProductoId(productoId);
    }

    @Transactional
    public AuditoriaPrecio registrarCambio(Integer usuarioId, Producto producto, BigDecimal precioOriginal, BigDecimal precioModificado, String justificacion) {
        AuditoriaPrecio auditoria = new AuditoriaPrecio();
        auditoria.setUsuarioId(usuarioId);
        auditoria.setProducto(producto);
        auditoria.setPrecioOriginal(precioOriginal);
        auditoria.setPrecioModificado(precioModificado);
        auditoria.setJustificacion(justificacion);
        
        return auditoriaPrecioRepository.save(auditoria);
    }
    public List<AuditoriaPrecio> obtenerTodas() {
        return auditoriaPrecioRepository.findAll();
    }
}