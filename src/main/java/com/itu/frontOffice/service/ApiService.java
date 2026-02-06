package com.itu.frontOffice.service;

import com.itu.frontOffice.dto.ReservationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
public class ApiService {
    
    private final WebClient webClient;
    
    @Value("${api.base.url:http://localhost:8081/api}")
    private String apiBaseUrl;
    
    public ApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(apiBaseUrl).build();
    }
    
    /**
     * Récupère toutes les réservations
     */
    public List<ReservationDTO> getAllReservations() {
        try {
            return webClient.get()
                    .uri("/reservations")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ReservationDTO>>() {})
                    .block();
        } catch (WebClientResponseException e) {
            System.err.println("Erreur lors de la récupération des réservations: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    /**
     * Récupère les réservations filtrées par date
     */
    public List<ReservationDTO> getReservationsByDate(LocalDate date) {
        if (date == null) {
            return getAllReservations();
        }
        
        try {
            String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/reservations/filter")
                            .queryParam("date", dateStr)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> {
                        return Mono.error(new RuntimeException("Erreur API: " + response.statusCode()));
                    })
                    .bodyToMono(new ParameterizedTypeReference<List<ReservationDTO>>() {})
                    .block();
        } catch (Exception e) {
            System.err.println("Erreur lors du filtrage par date: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    /**
     * Récupère les réservations filtrées par date (avec une date en string)
     */
    public List<ReservationDTO> getReservationsByDateString(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return getAllReservations();
        }
        
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/reservations/filter")
                            .queryParam("date", dateString)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ReservationDTO>>() {})
                    .block();
        } catch (Exception e) {
            System.err.println("Erreur lors du filtrage: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}