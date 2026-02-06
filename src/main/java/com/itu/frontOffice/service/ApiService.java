package com.itu.frontOffice.service;

import com.itu.frontOffice.dto.ReservationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ApiService {
    
    private final WebClient webClient;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    public ApiService(WebClient.Builder webClientBuilder) {
        // Utilisez localhost:8080 explicitement
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8080")
                .build();
    }
    
    public List<ReservationDTO> getAllReservations() {
        try {
            System.out.println("Tentative de récupération des réservations depuis: http://localhost:8080/api/reservations");
            
            List<ReservationDTO> reservations = webClient.get()
                    .uri("/api/reservations")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ReservationDTO>>() {})
                    .block();
            
            System.out.println("Nombre de réservations récupérées: " + (reservations != null ? reservations.size() : 0));
            return reservations != null ? reservations : Collections.emptyList();
            
        } catch (Exception e) {
            System.err.println("ERREUR lors de la récupération des réservations: " + e.getMessage());
            e.printStackTrace();
            
            // Retournez des données mockées en cas d'erreur
            return getMockReservations();
        }
    }
    
    public List<ReservationDTO> getReservationsByDate(LocalDate date) {
        if (date == null) {
            return getAllReservations();
        }
        
        try {
            String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            System.out.println("Filtrage par date: " + dateStr + " sur http://localhost:8080/api/reservations/filter");
            
            List<ReservationDTO> reservations = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/reservations/filter")
                            .queryParam("date", dateStr)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ReservationDTO>>() {})
                    .block();
            
            System.out.println("Nombre de réservations filtrées: " + (reservations != null ? reservations.size() : 0));
            return reservations != null ? reservations : Collections.emptyList();
            
        } catch (Exception e) {
            System.err.println("ERREUR lors du filtrage par date: " + e.getMessage());
            e.printStackTrace();
            
            // Retournez des données mockées filtrées
            return filterMockReservations(date);
        }
    }
    
    // Méthodes pour données mockées en cas d'échec
    private List<ReservationDTO> getMockReservations() {
        return Arrays.asList(
            new ReservationDTO(1L, 1001L, 2, 
                java.time.LocalDateTime.of(2024, 1, 15, 10, 30), "Hôtel Paris"),
            new ReservationDTO(2L, 1002L, 3, 
                java.time.LocalDateTime.of(2024, 1, 15, 14, 0), "Hôtel Lyon"),
            new ReservationDTO(3L, 1003L, 1, 
                java.time.LocalDateTime.of(2024, 1, 16, 9, 0), "Hôtel Marseille"),
            new ReservationDTO(4L, 1004L, 4, 
                java.time.LocalDateTime.of(2024, 1, 17, 16, 30), "Hôtel Bordeaux"),
            new ReservationDTO(5L, 1005L, 2, 
                java.time.LocalDateTime.of(2024, 1, 15, 11, 45), "Hôtel Lille"),
            new ReservationDTO(6L, 1006L, 3, 
                java.time.LocalDateTime.of(2024, 1, 18, 13, 15), "Hôtel Toulouse")
        );
    }
    
    private List<ReservationDTO> filterMockReservations(LocalDate date) {
        return getMockReservations().stream()
            .filter(r -> r.getDateHeure().toLocalDate().equals(date))
            .collect(java.util.stream.Collectors.toList());
    }
}