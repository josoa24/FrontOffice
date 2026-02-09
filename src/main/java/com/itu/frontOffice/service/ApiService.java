// ===== ApiService.java (CORRIGÉ) =====
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
                if (res.getDateHeure() != null) {
                    System.out.println("Réservation #" + res.getIdReservation() + 
                        " - DateTime: " + res.getDateHeure() +
                        " - Date seule: " + res.getDateHeure().toLocalDate());
                }
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
        
        // Filtrage côté client avec correction
        List<ReservationDTO> filtered = allReservations.stream()
            .filter(reservation -> {
                // Vérification de nullité
                if (reservation == null) {
                    System.out.println("Réservation null ignorée");
                    return false;
                }
                
                if (reservation.getDateHeure() == null) {
                    System.out.println("Réservation #" + reservation.getIdReservation() + " - dateHeure est NULL");
                    return false;
                }
                
                // Extraction de la date sans l'heure
                LocalDate resDate = reservation.getDateHeure().toLocalDate();
                
                // Comparaison stricte des dates
                boolean matches = resDate.equals(date);
                
                System.out.println("Réservation #" + reservation.getIdReservation() + 
                    " - Date réservation: " + resDate + 
                    " - Date recherchée: " + date +
                    " - Correspond: " + matches);
                
                return matches;
            })
            .collect(Collectors.toList());
        
        System.out.println("Résultat du filtrage: " + filtered.size() + " réservation(s) trouvée(s)");
        System.out.println("=== FIN FILTRAGE ===\n");
        
        return filtered;
    }
}