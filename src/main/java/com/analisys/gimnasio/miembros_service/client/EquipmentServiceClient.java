package com.analisys.gimnasio.miembros_service.client;

import com.analisys.gimnasio.miembros_service.dto.EquipmentResponseDTO;
import com.analisys.gimnasio.miembros_service.dto.UseEquipmentRequestDTO;
import com.analisys.gimnasio.miembros_service.exception.EquipmentServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Cliente HTTP para comunicarse con el microservicio de equipos.
 * Utiliza RestTemplate para realizar llamadas REST síncronas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentServiceClient {

    private final RestTemplate restTemplate;

    @Value("${equipment.service.url}")
    private String equipmentServiceUrl;

    // obtener todos los equipos disponibles
    public List<EquipmentResponseDTO> getAllEquipment() {
        String url = equipmentServiceUrl + "/api/equipment";
        log.info("Llamando a: GET {}", url);
        
        try {
            ResponseEntity<EquipmentResponseDTO[]> response = restTemplate.getForEntity(
                url, 
                EquipmentResponseDTO[].class
            );
            return Arrays.asList(response.getBody());
        } catch (RestClientException e) {
            log.error("Error al obtener equipos: {}", e.getMessage());
            throw new EquipmentServiceException("Error al comunicarse con el servicio de equipos: " + e.getMessage());
        }
    }

    // obtener un equipo por ID
    public EquipmentResponseDTO getEquipmentById(Long id) {
        String url = equipmentServiceUrl + "/api/equipment/" + id;
        log.info("Llamando a: GET {}", url);
        
        try {
            ResponseEntity<EquipmentResponseDTO> response = restTemplate.getForEntity(
                url, 
                EquipmentResponseDTO.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Equipo no encontrado con ID: {}", id);
            throw new EquipmentServiceException("Equipo no encontrado con ID: " + id);
        } catch (RestClientException e) {
            log.error("Error al obtener equipo: {}", e.getMessage());
            throw new EquipmentServiceException("Error al comunicarse con el servicio de equipos: " + e.getMessage());
        }
    }

    // obtener equipos disponibles
    public List<EquipmentResponseDTO> getAvailableEquipment() {
        String url = equipmentServiceUrl + "/api/equipment/available";
        log.info("Llamando a: GET {}", url);
        
        try {
            ResponseEntity<EquipmentResponseDTO[]> response = restTemplate.getForEntity(
                url, 
                EquipmentResponseDTO[].class
            );
            return Arrays.asList(response.getBody());
        } catch (RestClientException e) {
            log.error("Error al obtener equipos disponibles: {}", e.getMessage());
            throw new EquipmentServiceException("Error al comunicarse con el servicio de equipos: " + e.getMessage());
        }
    }

    // usar/reservar un equipo (disminuye cantidad disponible)
    public EquipmentResponseDTO useEquipment(Long equipmentId, int cantidad) {
        String url = equipmentServiceUrl + "/api/equipment/" + equipmentId + "/use";
        log.info("Llamando a: POST {} con cantidad: {}", url, cantidad);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            UseEquipmentRequestDTO request = UseEquipmentRequestDTO.builder()
                    .cantidad(cantidad)
                    .build();
            
            HttpEntity<UseEquipmentRequestDTO> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<EquipmentResponseDTO> response = restTemplate.postForEntity(
                url, 
                entity, 
                EquipmentResponseDTO.class
            );
            
            log.info("Equipo usado exitosamente. Cantidad disponible restante: {}", 
                    response.getBody().getCantidadDisponible());
            
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error del servicio de equipos: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new EquipmentServiceException("Error al usar equipo: " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            log.error("Error de conexión con el servicio de equipos: {}", e.getMessage());
            throw new EquipmentServiceException("Error al comunicarse con el servicio de equipos: " + e.getMessage());
        }
    }

    // libera/devuelve un equipo (aumenta cantidad disponible)
    public EquipmentResponseDTO releaseEquipment(Long equipmentId, int cantidad) {
        String url = equipmentServiceUrl + "/api/equipment/" + equipmentId + "/release";
        log.info("Llamando a: POST {} con cantidad: {}", url, cantidad);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            UseEquipmentRequestDTO request = UseEquipmentRequestDTO.builder()
                    .cantidad(cantidad)
                    .build();
            
            HttpEntity<UseEquipmentRequestDTO> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<EquipmentResponseDTO> response = restTemplate.postForEntity(
                url, 
                entity, 
                EquipmentResponseDTO.class
            );
            
            log.info("Equipo liberado exitosamente. Cantidad disponible: {}", 
                    response.getBody().getCantidadDisponible());
            
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error del servicio de equipos: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new EquipmentServiceException("Error al liberar equipo: " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            log.error("Error de conexión con el servicio de equipos: {}", e.getMessage());
            throw new EquipmentServiceException("Error al comunicarse con el servicio de equipos: " + e.getMessage());
        }
    }

    // verificar si el servicio de equipos esta disponible
    public boolean isServiceAvailable() {
        try {
            String url = equipmentServiceUrl + "/api/equipment";
            restTemplate.getForEntity(url, String.class);
            return true;
        } catch (RestClientException e) {
            log.warn("Servicio de equipos no disponible: {}", e.getMessage());
            return false;
        }
    }
}
