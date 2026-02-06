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
        
        List<ReservationDTO> reservations;
        
        if (filterDate != null) {
            reservations = apiService.getReservationsByDate(filterDate);
            model.addAttribute("filterDate", filterDate);
        } else {
            reservations = apiService.getAllReservations();
        }
        
        model.addAttribute("reservations", reservations);
        return "reservations/list";
    }

    @GetMapping("/reset")
    public String resetFilter() {
        return "redirect:/reservations";
    }
}