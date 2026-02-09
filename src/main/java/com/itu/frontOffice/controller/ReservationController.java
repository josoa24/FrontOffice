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
        
        if (filterDate != null) {
            System.out.println("filterDate (toString): " + filterDate.toString());
            System.out.println("filterDate (ISO): " + filterDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            System.out.println("filterDate (dd/MM/yyyy): " + filterDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        
        List<ReservationDTO> reservations;
        
        if (filterDate != null) {
            System.out.println("Appel de getReservationsByDate avec: " + filterDate);
            reservations = apiService.getReservationsByDate(filterDate);
        } else {
            System.out.println("Appel de getAllReservations");
            reservations = apiService.getAllReservations();
        }
        
        System.out.println("Nombre de réservations retournées: " + reservations.size());
        
        if (!reservations.isEmpty()) {
            System.out.println("=== PREMIÈRES RÉSERVATIONS ===");
            for (int i = 0; i < Math.min(3, reservations.size()); i++) {
                ReservationDTO r = reservations.get(i);
                System.out.println("  " + (i+1) + ". ID: " + r.getIdReservation() + 
                                 ", Date: " + r.getDateHeure() +
                                 ", Hotel: " + (r.getHotel() != null ? r.getHotel().getNom() : "N/A"));
            }
        }
        
        model.addAttribute("reservations", reservations);
        model.addAttribute("filterDate", filterDate);
        
        System.out.println("=== FIN CONTROLLEUR ===");
        return "reservations/list";
    }
    
    @GetMapping("/reset")
    public String resetFilter() {
        return "redirect:/reservations";
    }
    
    
}