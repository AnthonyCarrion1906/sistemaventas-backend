package com.empresa.sistemaventas.service;

import com.empresa.sistemaventas.dto.DniResponseDto;
import com.empresa.sistemaventas.dto.RucResponseDto;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ConsultaService {

    @Value("${decolecta.api.url}")
    private String apiUrl;

    @Value("${decolecta.api.ruc.url}")
    private String apiRucUrl;

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
}
