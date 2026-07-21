package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.dto.DashboardResumenDto;
import com.empresa.sistemaventas.repository.ClienteRepository;
import com.empresa.sistemaventas.repository.DevolucionRepository;
import com.empresa.sistemaventas.repository.ProductoRepository;
import com.empresa.sistemaventas.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired private VentaRepository ventaRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private DevolucionRepository devolucionRepository;

    public DashboardResumenDto obtenerResumen() {
        DashboardResumenDto dto = new DashboardResumenDto();

        LocalDate hoy   = LocalDate.now();
        int anioActual  = hoy.getYear();

        // ── Rango: primer y último día del mes actual ─────────────────
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes    = hoy.with(TemporalAdjusters.lastDayOfMonth());

        // ── KPIs de ventas ────────────────────────────────────────────
        dto.setTotalVentasMes(
                coalesce(ventaRepository.sumTotalEntreFechas(inicioMes, finMes)));
        dto.setCantidadVentasMes(
                ventaRepository.countEntreFechas(inicioMes, finMes));
        dto.setTotalVentasHoy(
                coalesce(ventaRepository.sumTotalEntreFechas(hoy, hoy)));
        dto.setCantidadVentasHoy(
                ventaRepository.countEntreFechas(hoy, hoy));

        // ── KPIs de clientes y productos ──────────────────────────────
        dto.setTotalClientes(clienteRepository.count());
        dto.setTotalProductos(productoRepository.countByEstadoTrue());
        dto.setProductosStockBajo(productoRepository.countProductosConStockBajo());

        // ── KPIs de devoluciones del mes ──────────────────────────────
        LocalDateTime desdeDevMes = inicioMes.atStartOfDay();
        LocalDateTime hastaDevMes = finMes.atTime(LocalTime.MAX);
        dto.setImpactoDevolucionesMes(
                coalesce(devolucionRepository.sumImpactoEntreFechas(desdeDevMes, hastaDevMes)));
        dto.setTotalDevoluciones(devolucionRepository.count());

        // ── Gráfico de barras: últimos 7 días ─────────────────────────
        LocalDate inicioSemana = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate finSemana    = inicioSemana.plusDays(6);

        List<Object[]> datosSemana = ventaRepository.sumTotalYConteoByFecha(inicioSemana, finSemana);

        // Inicializar 7 días (Lun → Dom) en cero
        Map<LocalDate, BigDecimal> totalesPorDia = new LinkedHashMap<>();
        Map<LocalDate, Long>       pedidosPorDia  = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate dia = inicioSemana.plusDays(i);
            totalesPorDia.put(dia, BigDecimal.ZERO);
            pedidosPorDia.put(dia, 0L);
        }
        for (Object[] row : datosSemana) {
            LocalDate fecha = (LocalDate) row[0];
            BigDecimal total = (BigDecimal) row[1];
            Long conteo      = (Long)       row[2];
            if (totalesPorDia.containsKey(fecha)) {
                totalesPorDia.put(fecha, total);
                pedidosPorDia.put(fecha, conteo);
            }
        }
        dto.setVentasPorDiaSemana(new ArrayList<>(totalesPorDia.values()));
        dto.setPedidosPorDiaSemana(new ArrayList<>(pedidosPorDia.values()));

        // ── Gráfico de línea: ventas mensuales del año ────────────────
        List<Object[]> datosMeses = ventaRepository.sumTotalPorMesAnio(anioActual);

        // Inicializar 12 meses en 0
        BigDecimal[] mensual = new BigDecimal[13]; // índice 1-12
        for (int i = 1; i <= 12; i++) mensual[i] = BigDecimal.ZERO;
        for (Object[] row : datosMeses) {
            int mes         = ((Number) row[0]).intValue();
            BigDecimal total = (BigDecimal) row[1];
            mensual[mes]    = total;
        }
        List<BigDecimal> ventasMensuales = new ArrayList<>();
        for (int i = 1; i <= 12; i++) ventasMensuales.add(mensual[i]);
        dto.setVentasMensualesAnio(ventasMensuales);

        // ── Gráfico doughnut: método de pago del mes ──────────────────
        List<Object[]> datosPago = ventaRepository.sumTotalPorMetodoPago(inicioMes, finMes);
        Map<String, BigDecimal> metodoPagoMap = new LinkedHashMap<>();
        for (Object[] row : datosPago) {
            String metodo    = row[0] != null ? (String) row[0] : "OTRO";
            BigDecimal total = (BigDecimal) row[1];
            metodoPagoMap.put(metodo, total);
        }
        dto.setVentasPorMetodoPago(metodoPagoMap);

        return dto;
    }

    /** Convierte null a BigDecimal.ZERO */
    private BigDecimal coalesce(BigDecimal val) {
        return val != null ? val : BigDecimal.ZERO;
    }
}
