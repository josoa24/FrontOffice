// ===== ReservationController.java (AMÉLIORÉ) =====
package com.itu.frontOffice.controller;

import com.itu.frontOffice.dto.ReservationDTO;
import com.itu.frontOffice.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/reservations")
public class ReservationController {
    
    @Autowired
    private ApiService apiService;
    
    @GetMapping
    public String listReservations(
            @RequestParam(value = "filterDate", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate filterDate,
            Model model) {
        
        System.out.println("\n=== DÉBUT CONTROLLEUR listReservations ===");
        System.out.println("Paramètre filterDate reçu: " + filterDate);
        
        List<ReservationDTO> reservations;
        
        if (filterDate != null) {
            System.out.println("Filtrage avec date: " + filterDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            reservations = apiService.getReservationsByDate(filterDate);
        } else {
            System.out.println("Aucun filtre - Récupération de toutes les réservations");
            reservations = apiService.getAllReservations();
        }
        
        System.out.println("Nombre de réservations retournées au controller: " + reservations.size());
        
        // Debug détaillé
        if (!reservations.isEmpty()) {
            System.out.println("=== APERÇU DES RÉSERVATIONS ===");
            int maxDisplay = Math.min(5, reservations.size());
            for (int i = 0; i < maxDisplay; i++) {
                ReservationDTO r = reservations.get(i);
                System.out.println("  " + (i+1) + ". " + r.toString());
            }
            if (reservations.size() > maxDisplay) {
                System.out.println("  ... et " + (reservations.size() - maxDisplay) + " autre(s)");
            }
        } else {
            System.out.println("⚠️ AUCUNE RÉSERVATION À AFFICHER");
        }
        
        model.addAttribute("reservations", reservations);
        model.addAttribute("filterDate", filterDate);
        
        System.out.println("=== FIN CONTROLLEUR ===\n");
        return "reservations/list";
    }
    
    @GetMapping("/reset")
    public String resetFilter() {
        System.out.println("Reset du filtre - Redirection vers /reservations");
        return "redirect:/reservations";
    }
}