package com.itu.frontOffice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class ReservationDTO {
    private Long idReservation;
    private Long idClient;
    private Integer nbPassager;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateHeure;
    private String hotelNom;
    
    public ReservationDTO() {}
    
    public ReservationDTO(Long idReservation, Long idClient, Integer nbPassager, 
                         LocalDateTime dateHeure, String hotelNom) {
        this.idReservation = idReservation;
        this.idClient = idClient;
        this.nbPassager = nbPassager;
        this.dateHeure = dateHeure;
        this.hotelNom = hotelNom;
    }
    
    public Long getIdReservation() {
        return idReservation;
    }
    
    public void setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
    }
    
    public Long getIdClient() {
        return idClient;
    }
    
    public void setIdClient(Long idClient) {
        this.idClient = idClient;
    }
    
    public Integer getNbPassager() {
        return nbPassager;
    }
    
    public void setNbPassager(Integer nbPassager) {
        this.nbPassager = nbPassager;
    }
    
    public LocalDateTime getDateHeure() {
        return dateHeure;
    }
    
    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }
    
    public String getHotelNom() {
        return hotelNom;
    }
    
    public void setHotelNom(String hotelNom) {
        this.hotelNom = hotelNom;
    }
}