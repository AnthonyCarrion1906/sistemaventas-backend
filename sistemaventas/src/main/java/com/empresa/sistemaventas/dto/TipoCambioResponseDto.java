package com.empresa.sistemaventas.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO para la respuesta del endpoint de tipo de cambio SUNAT
 * de la API Decolecta: GET /v1/tipo-cambio/sunat?date=YYYY-MM-DD
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TipoCambioResponseDto {

    @JsonProperty("fecha")
    @JsonAlias({"date", "fecha"})
    private String fecha;

    @JsonProperty("moneda")
    @JsonAlias({"base_currency", "moneda"})
    private String moneda;

    @JsonProperty("compra")
    @JsonAlias({"buy_price", "compra"})
    private BigDecimal compra;

    @JsonProperty("venta")
    @JsonAlias({"sell_price", "venta"})
    private BigDecimal venta;
}
