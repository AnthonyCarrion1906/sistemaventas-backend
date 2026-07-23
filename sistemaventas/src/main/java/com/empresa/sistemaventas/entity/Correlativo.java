package com.empresa.sistemaventas.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "correlativos")
public class Correlativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Clave única que identifica el tipo de documento.
     * Ejemplos: "PROFORMA", "VENTA", "ORDEN_COMPRA", "DEVOLUCION"
     */
    @Column(name = "tipo", nullable = false, unique = true, length = 50)
    private String tipo;

    /**
     * Prefijo que aparecerá en el código generado.
     * Ejemplo: "PRF" genera "PRF-000001"
     */
    @Column(name = "prefijo", nullable = false, length = 10)
    private String prefijo;

    /**
     * Último número usado. Se incrementa en cada generación.
     */
    @Column(name = "ultimo_numero", nullable = false)
    private Integer ultimoNumero = 0;

    /**
     * Cantidad de dígitos (con ceros a la izquierda) para el número.
     * Ejemplo: 6 → 000001
     */
    @Column(name = "longitud", nullable = false)
    private Integer longitud = 6;
}
