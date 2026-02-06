package com.itu.frontOffice.controller;

import com.itu.frontOffice.dto.ReservationDTO;
import com.itu.frontOffice.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
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
        
        System.out.println("=== DÉBUT listReservations ===");
        System.out.println("filterDate: " + filterDate);
        
        List<ReservationDTO> reservations;
        
        if (filterDate != null) {
            System.out.println("Appel de getReservationsByDate avec: " + filterDate);
            reservations = apiService.getReservationsByDate(filterDate);
        } else {
            System.out.println("Appel de getAllReservations");
            reservations = apiService.getAllReservations();
        }
        
        System.out.println("Nombre de réservations dans le modèle: " + reservations.size());
        for (ReservationDTO r : reservations) {
            System.out.println(" - Réservation: " + r.getIdReservation() + ", Client: " + r.getIdClient());
        }
        
        model.addAttribute("reservations", reservations);
        model.addAttribute("filterDate", filterDate);
        
        System.out.println("=== FIN listReservations ===");
        return "reservations/list";
    }
    
    @GetMapping("/reset")
    public String resetFilter() {
        return "redirect:/reservations";
    }
}