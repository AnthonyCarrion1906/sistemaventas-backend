package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.entity.*;
import com.empresa.sistemaventas.repository.ProformaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProformaService {

    @Autowired
    private ProformaRepository proformaRepository;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private ProductoService productoService;

    public List<Proforma> obtenerTodas() {
        return proformaRepository.findAll();
    }

    public Optional<Proforma> obtenerPorId(Integer id) {
        return proformaRepository.findById(id);
    }

    public List<Proforma> obtenerPorCliente(Integer clienteId) {
        return proformaRepository.findByClienteId(clienteId);
    }

    @Transactional
    public Proforma guardar(Proforma proforma) {
        Cliente cliente = clienteService.obtenerPorId(proforma.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente no válido"));
        
        proforma.setCliente(cliente);
        
        // CORREGIDO: Uso de String en lugar de Enum
        proforma.setEstado(proforma.getEstado() == null ? "PENDIENTE" : proforma.getEstado());
        
        if (proforma.getDetalles() != null) {
            proforma.getDetalles().forEach(detalle -> {
                detalle.setProforma(proforma);
                detalle.setSubtotal(detalle.getPrecioUnitario().multiply(detalle.getCantidad()));
                
                // CORREGIDO: Validamos con equals() y usamos getTipoItem() en lugar de getTipo()
                if ("SERVICIO".equals(detalle.getTipoItem()) && detalle.getServicioId() != null) {
                    servicioService.obtenerPorId(detalle.getServicioId())
                            .orElseThrow(() -> new RuntimeException("Servicio inválido en detalle"));
                }
                if ("PRODUCTO".equals(detalle.getTipoItem()) && detalle.getProductoId() != null) {
                    productoService.obtenerPorId(detalle.getProductoId())
                            .orElseThrow(() -> new RuntimeException("Producto inválido en detalle"));
                }
            });
        }
        
        calcularTotales(proforma);
        return proformaRepository.save(proforma);
    }

    private void calcularTotales(Proforma proforma) {
        BigDecimal sumaSubtotales = proforma.getDetalles().stream()
                .map(ProformaDetalle::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        proforma.setSubtotal(sumaSubtotales); 

        BigDecimal descuento = proforma.getDescuento() != null ? proforma.getDescuento() : BigDecimal.ZERO;
        
        if (descuento.compareTo(sumaSubtotales) > 0) {
            descuento = sumaSubtotales;
        }
        
        proforma.setDescuento(descuento);
        proforma.setTotal(sumaSubtotales.subtract(descuento)); 
    }

    @Transactional
    public Proforma actualizar(Integer id, Proforma datos) {
        Proforma existente = proformaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proforma no encontrada"));
                
        if (Boolean.TRUE.equals(existente.getEsFinal())) {
            throw new RuntimeException("No se puede modificar una proforma finalizada");
        }
        
        existente.setCliente(clienteService.obtenerPorId(datos.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente no válido")));
        existente.setEstado(datos.getEstado() != null ? datos.getEstado() : existente.getEstado());
        
        existente.setDescuento(datos.getDescuento());
        if (datos.getUsuarioId() != null) existente.setUsuarioId(datos.getUsuarioId());
        if (datos.getCodigoCorrelativo() != null) existente.setCodigoCorrelativo(datos.getCodigoCorrelativo());

        existente.getDetalles().clear();
        if (datos.getDetalles() != null) {
            datos.getDetalles().forEach(detalle -> {
                detalle.setProforma(existente);
                detalle.setSubtotal(detalle.getPrecioUnitario().multiply(detalle.getCantidad()));
                existente.getDetalles().add(detalle);
            });
        }
        
        calcularTotales(existente);
        return proformaRepository.save(existente);
    }

    @Transactional
    // CORREGIDO: Recibe String estado en lugar de Enum
    public Proforma cambiarEstado(Integer id, String estado) {
        Proforma existente = proformaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proforma no encontrada"));
                
        existente.setEstado(estado);
        // CORREGIDO: Comparación de String
        if ("CERRADA".equals(estado)) {
            existente.setEsFinal(true); 
        }
        return proformaRepository.save(existente);
    }

    @Transactional
    public Proforma crearVersion(Integer id) {
        Proforma original = proformaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proforma original no encontrada"));
                
        Proforma version = new Proforma();
        version.setCliente(original.getCliente());
        
        // CORREGIDO: Uso de String
        version.setEstado("PENDIENTE");
        version.setUsuarioId(original.getUsuarioId());
        version.setCodigoCorrelativo(original.getCodigoCorrelativo());
        version.setVersion(original.getVersion() + 1);
        
        original.getDetalles().forEach(detalle -> {
            ProformaDetalle copia = new ProformaDetalle();
            // CORREGIDO: Uso de getTipoItem() y setTipoItem()
            copia.setTipoItem(detalle.getTipoItem());
            copia.setProductoId(detalle.getProductoId());
            copia.setServicioId(detalle.getServicioId());
            copia.setDescripcion(detalle.getDescripcion());
            copia.setCantidad(detalle.getCantidad());
            copia.setPrecioUnitario(detalle.getPrecioUnitario());
            copia.setSubtotal(detalle.getSubtotal());
            copia.setProforma(version);
            version.getDetalles().add(copia);
        });
        
        version.setDescuento(original.getDescuento());
        
        calcularTotales(version);
        return proformaRepository.save(version);
    }
}