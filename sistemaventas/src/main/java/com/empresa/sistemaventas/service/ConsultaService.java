package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.dto.DniResponseDto;
import com.empresa.sistemaventas.dto.RucResponseDto;
import com.empresa.sistemaventas.dto.TipoCambioResponseDto;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
@Slf4j
public class ConsultaService {

    @Value("${decolecta.api.url}")
    private String apiUrl;

    @Value("${decolecta.api.ruc.url}")
    private String apiRucUrl;

    @Value("${decolecta.api.tipo-cambio.url}")
    private String apiTipoCambioUrl;

    @Value("${decolecta.api.token}")
    private String apiToken;

    private final RestTemplate restTemplate;

    public ConsultaService() {
        this.restTemplate = new RestTemplate();
    }

    public DniResponseDto consultarDni(String numeroDni) {
        try {
            String urlTemplate = apiUrl + "?numero=" + numeroDni;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiToken);
            headers.set("Content-Type", "application/json");

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<DniResponseDto> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.GET,
                    entity,
                    DniResponseDto.class);

            log.info("DniResponseDto: {}", response.getBody());

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error en la comunicación con el servicio de DNI: " + e.getMessage());
        }
    }

    public RucResponseDto consultarRuc(String numeroRuc) {
        try {
            String urlTemplate = apiRucUrl + "?numero=" + numeroRuc;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiToken);
            headers.set("Content-Type", "application/json");

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<RucResponseDto> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.GET,
                    entity,
                    RucResponseDto.class);

            log.info("RucResponseDto: {}", response.getBody());

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error en la comunicación con el servicio de RUC: " + e.getMessage());
        }
    }

    /**
     * Consulta el tipo de cambio USD/PEN publicado por SUNAT a través de la API Decolecta.
     *
     * @param fecha fecha en formato YYYY-MM-DD; si es null usa la fecha actual.
     * @return TipoCambioResponseDto con los valores de compra y venta.
     */
    public TipoCambioResponseDto consultarTipoCambio(String fecha) {
        try {
            String fechaConsulta = (fecha != null && !fecha.isBlank()) ? fecha : LocalDate.now().toString();
            String urlTemplate = apiTipoCambioUrl + "?date=" + fechaConsulta;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiToken);
            headers.set("Content-Type", "application/json");

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<TipoCambioResponseDto> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.GET,
                    entity,
                    TipoCambioResponseDto.class);

            log.info("TipoCambioResponseDto [{}]: {}", fechaConsulta, response.getBody());

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar el tipo de cambio SUNAT: " + e.getMessage());
        }
    }
}
