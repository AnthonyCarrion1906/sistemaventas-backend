package com.empresa.sistemaventas.constant;

/**
 * Constantes para los métodos de pago aceptados en el sistema.
 * Sirve como fuente de verdad única para validaciones en servicios.
 */
public final class MetodoPago {

    public static final String EFECTIVO      = "EFECTIVO";
    public static final String TRANSFERENCIA = "TRANSFERENCIA";
    public static final String YAPE_PLIN     = "YAPE_PLIN";
    public static final String TARJETA       = "TARJETA";

    /**
     * Todos los métodos de pago requieren una caja diaria abierta para registrar ventas.
     * El registro en caja permite tener trazabilidad completa de todos los ingresos del día.
     *
     * @param metodo el código del método de pago
     * @return siempre true — toda venta requiere caja abierta
     */
    public static boolean requiereCaja(String metodo) {
        return true;
    }

    /**
     * Indica si el método de pago representa dinero físico que entra a la caja.
     * Solo EFECTIVO incrementa el monto_fisico_real.
     * YAPE_PLIN, TRANSFERENCIA y TARJETA van al banco, no al efectivo físico de la caja.
     *
     * @param metodo el código del método de pago
     * @return true solo para EFECTIVO
     */
    public static boolean afectaEfectivoFisico(String metodo) {
        return EFECTIVO.equals(metodo);
    }

    private MetodoPago() {
        // Clase de constantes — no instanciable
    }
}
