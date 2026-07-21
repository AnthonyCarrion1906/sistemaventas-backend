package com.empresa.sistemaventas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DTO de respuesta del endpoint /api/dashboard/resumen.
 * Agrupa todas las métricas calculadas en el servidor para alimentar el dashboard.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResumenDto {

    // ── KPIs principales ─────────────────────────────────────────────
    private BigDecimal totalVentasMes;       // Suma de ventas del mes actual
    private long      cantidadVentasMes;     // Número de ventas del mes
    private BigDecimal totalVentasHoy;       // Suma de ventas del día de hoy
    private long      cantidadVentasHoy;     // Número de ventas hoy

    private long totalClientes;              // Total de clientes registrados
    private long totalProductos;             // Total de productos activos
    private long productosStockBajo;         // Productos con stock <= stockMinimo

    private long  totalDevoluciones;         // Total de devoluciones históricas
    private BigDecimal impactoDevolucionesMes; // Suma impacto económico devoluciones del mes

    // ── Gráfico: Ventas por día de la semana (últimos 7 días) ────────
    // Lista de 7 elementos: [Lun, Mar, Mié, Jue, Vie, Sáb, Dom]
    private List<BigDecimal> ventasPorDiaSemana;   // Totales S/
    private List<Long>       pedidosPorDiaSemana;  // Conteo de ventas

    // ── Gráfico: Ventas mensuales del año actual ─────────────────────
    // Lista de 12 elementos (Ene → Dic)
    private List<BigDecimal> ventasMensualesAnio;

    // ── Gráfico doughnut: Métodos de pago (mes actual) ───────────────
    // Key: metodo_pago, Value: total S/
    private Map<String, BigDecimal> ventasPorMetodoPago;
}
