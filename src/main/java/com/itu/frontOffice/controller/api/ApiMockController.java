package com.itu.frontOffice.controller.api;

import com.itu.frontOffice.dto.ReservationDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiMockController {
    
    // Données mockées pour simulation
    private final List<ReservationDTO> mockReservations = Arrays.asList(
        new ReservationDTO(1L, 1001L, 2, 
            LocalDateTime.of(2024, 1, 15, 10, 30), "Hôtel Paris"),
        new ReservationDTO(2L, 1002L, 3, 
            LocalDateTime.of(2024, 1, 15, 14, 0), "Hôtel Lyon"),
        new ReservationDTO(3L, 1003L, 1, 
            LocalDateTime.of(2024, 1, 16, 9, 0), "Hôtel Marseille"),
        new ReservationDTO(4L, 1004L, 4, 
            LocalDateTime.of(2024, 1, 17, 16, 30), "Hôtel Bordeaux"),
        new ReservationDTO(5L, 1005L, 2, 
            LocalDateTime.of(2024, 1, 15, 11, 45), "Hôtel Lille"),
        new ReservationDTO(6L, 1006L, 3, 
            LocalDateTime.of(2024, 1, 18, 13, 15), "Hôtel Toulouse")
    );
    
    /**
     * API: GET toutes les réservations
     */
    @GetMapping("/reservations")
    public List<ReservationDTO> getAllReservations() {
        return mockReservations;
    }
    
    /**
     * API: GET réservations filtrées par date
     */
    @GetMapping("/reservations/filter")
    public List<ReservationDTO> getReservationsByDate(
            @RequestParam(value = "date", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        if (date == null) {
            return mockReservations;
        }
        
        return mockReservations.stream()
                .filter(reservation -> reservation.getDateHeure().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }
    
    /**
     * API: Recherche par période (optionnel)
     */
    @GetMapping("/reservations/search")
    public List<ReservationDTO> searchReservations(
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate) {
        
        if (startDate == null && endDate == null) {
            return mockReservations;
        }
        
        return mockReservations.stream()
                .filter(reservation -> {
                    LocalDate reservationDate = reservation.getDateHeure().toLocalDate();
                    if (startDate != null && endDate != null) {
                        return !reservationDate.isBefore(startDate) && !reservationDate.isAfter(endDate);
                    } else if (startDate != null) {
                        return !reservationDate.isBefore(startDate);
                    } else {
                        return !reservationDate.isAfter(endDate);
                    }
                })
                .collect(Collectors.toList());
    }
}