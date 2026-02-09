package com.itu.frontOffice.service;

import com.itu.frontOffice.dto.ApiResponseDTO;
import com.itu.frontOffice.dto.ReservationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiService {
    
    private final WebClient webClient;
    
    @Value("${api.external.url}")
    private String apiExternalUrl;
    
    public ApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    
    public List<ReservationDTO> getAllReservations() {
        try {
            System.out.println("=== APPEL API getAllReservations ===");
            System.out.println("URL: " + apiExternalUrl);
            
            ApiResponseDTO apiResponse = webClient.get()
                    .uri(apiExternalUrl)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO>() {})
                    .block();
            
            if (apiResponse == null || apiResponse.getData() == null) {
                System.out.println("Réponse API vide");
                return Collections.emptyList();
            }
            
            List<ReservationDTO> reservations = apiResponse.getData().getReservations();
            if (reservations == null) {
                reservations = Collections.emptyList();
            }
            
            System.out.println("Nombre total de réservations: " + reservations.size());
            
            // Debug: afficher toutes les dates
            reservations.forEach(res -> {
                System.out.println("Réservation #" + res.getIdReservation() + 
                    " - DateTime: " + res.getDateHeure() +
                    " - Date seule: " + (res.getDateHeure() != null ? res.getDateHeure().toLocalDate() : "null"));
            });
            
            return reservations;
            
        } catch (Exception e) {
            System.err.println("ERREUR lors de l'appel API: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    public List<ReservationDTO> getReservationsByDate(LocalDate date) {
        System.out.println("\n=== FILTRAGE PAR DATE ===");
        System.out.println("Date recherchée: " + date);
        
        if (date == null) {
            System.out.println("Date null, retour de toutes les réservations");
            return getAllReservations();
        }
        
        // Récupérer toutes les réservations
        List<ReservationDTO> allReservations = getAllReservations();
        
        if (allReservations.isEmpty()) {
            System.out.println("Aucune réservation disponible");
            return Collections.emptyList();
        }
        
        // Filtrage côté client avec debug détaillé
        List<ReservationDTO> filtered = allReservations.stream()
            .filter(reservation -> {
                if (reservation.getDateHeure() == null) {
                    System.out.println("Réservation #" + reservation.getIdReservation() + " - dateHeure est NULL");
                    return false;
                }
                
                LocalDate resDate = reservation.getDateHeure().toLocalDate();
                boolean matches = resDate.isEqual(date);
                
                System.out.println("Réservation #" + reservation.getIdReservation() + 
                    " - Date: " + resDate + 
                    " - Correspond: " + matches +
                    " (recherché: " + date + ")");
                
                return matches;
            })
            .collect(Collectors.toList());
        
        System.out.println("Résultat du filtrage: " + filtered.size() + " réservation(s)");
        System.out.println("=== FIN FILTRAGE ===\n");
        
        return filtered;
    }
}