package com.itu.frontOffice.service;

import com.itu.frontOffice.dto.ApiResponseDTO;
import com.itu.frontOffice.dto.ReservationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ApiService {
    
    private final WebClient webClient;
    
    @Value("${api.external.url}")
    private String apiExternalUrl;
    
    // Formatters pour différents formats de date
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),  // Format API: 2026-02-17T10:00
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),    // Format alternatif
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),    // Format français
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,              // Format ISO standard
    };
    
    public ApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    
    public List<ReservationDTO> getAllReservations() {
        try {
            System.out.println("=== APPEL API POUR TOUTES LES RÉSERVATIONS ===");
            System.out.println("URL: " + apiExternalUrl);
            
            ApiResponseDTO apiResponse = webClient.get()
                    .uri(apiExternalUrl)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO>() {})
                    .block();
            
            if (apiResponse == null) {
                System.out.println("Réponse API est null");
                return Collections.emptyList();
            }
            
            System.out.println("Code réponse: " + apiResponse.getCode());
            System.out.println("Status: " + apiResponse.getStatus());
            
            List<ReservationDTO> reservations = new ArrayList<>();
            if (apiResponse.getData() != null && apiResponse.getData().getReservations() != null) {
                reservations = apiResponse.getData().getReservations();
                System.out.println("Nombre de réservations récupérées: " + reservations.size());
                
                // DEBUG: Afficher toutes les dates
                System.out.println("=== DATES DES RÉSERVATIONS ===");
                for (ReservationDTO res : reservations) {
                    if (res.getDateHeure() != null) {
                        System.out.println("Réservation " + res.getIdReservation() + 
                                         " - Date: " + res.getDateHeure() +
                                         " (LocalDate: " + res.getDateHeure().toLocalDate() + ")");
                    } else {
                        System.out.println("Réservation " + res.getIdReservation() + " - Date: NULL");
                    }
                }
            }
            
            return reservations;
            
        } catch (Exception e) {
            System.err.println("ERREUR API: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    public List<ReservationDTO> getReservationsByDate(LocalDate targetDate) {
        System.out.println("\n=== DÉBUT FILTRAGE PAR DATE ===");
        System.out.println("Date cible: " + targetDate);
        System.out.println("Date formatée ISO: " + targetDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        
        try {
            // 1. D'abord essayer avec l'API si elle supporte le filtrage
            String dateStr = targetDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            String filterUrl = apiExternalUrl + "?date=" + dateStr;
            
            System.out.println("Tentative de filtrage via API: " + filterUrl);
            
            try {
                ApiResponseDTO apiResponse = webClient.get()
                        .uri(filterUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO>() {})
                        .block();
                
                if (apiResponse != null && apiResponse.getData() != null && 
                    apiResponse.getData().getReservations() != null) {
                    
                    List<ReservationDTO> filtered = apiResponse.getData().getReservations();
                    System.out.println("API retourne " + filtered.size() + " réservations filtrées");
                    
                    if (!filtered.isEmpty()) {
                        System.out.println("=== FIN FILTRAGE (via API) ===");
                        return filtered;
                    }
                }
            } catch (Exception apiFilterError) {
                System.out.println("API ne supporte pas le filtrage ou erreur: " + apiFilterError.getMessage());
            }
            
            // 2. Sinon, récupérer toutes et filtrer côté serveur
            System.out.println("Filtrage côté serveur...");
            List<ReservationDTO> allReservations = getAllReservations();
            
            if (allReservations.isEmpty()) {
                System.out.println("Aucune réservation à filtrer");
                System.out.println("=== FIN FILTRAGE (aucune donnée) ===");
                return Collections.emptyList();
            }
            
            // Filtrer par date
            List<ReservationDTO> filteredReservations = new ArrayList<>();
            
            for (ReservationDTO reservation : allReservations) {
                if (reservation.getDateHeure() == null) {
                    continue;
                }
                
                LocalDate reservationDate = reservation.getDateHeure().toLocalDate();
                boolean matches = reservationDate.isEqual(targetDate);
                
                if (matches) {
                    System.out.println("MATCH - Réservation " + reservation.getIdReservation() + 
                                     ": " + reservationDate + " == " + targetDate);
                    filteredReservations.add(reservation);
                } else {
                    System.out.println("NO MATCH - Réservation " + reservation.getIdReservation() + 
                                     ": " + reservationDate + " != " + targetDate);
                }
            }
            
            System.out.println("Résultat: " + filteredReservations.size() + " réservations filtrées");
            
            if (filteredReservations.isEmpty()) {
                System.out.println("=== DATES DISPONIBLES ===");
                for (ReservationDTO r : allReservations) {
                    if (r.getDateHeure() != null) {
                        System.out.println("  - " + r.getIdReservation() + ": " + 
                                         r.getDateHeure().toLocalDate() + 
                                         " (" + r.getDateHeure() + ")");
                    }
                }
            }
            
            System.out.println("=== FIN FILTRAGE ===");
            return filteredReservations;
            
        } catch (Exception e) {
            System.err.println("ERREUR lors du filtrage: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    // Méthode utilitaire pour parser différentes formats de date
    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // Continuer avec le prochain formatter
            }
        }
        
        System.err.println("Impossible de parser la date: " + dateStr);
        return null;
    }
}