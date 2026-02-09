package com.itu.frontOffice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import java.time.LocalDateTime;

public class ReservationDTO {
    
    @JsonProperty("idReservation")
    private Long idReservation;
    
    @JsonProperty("idClient")
    private Long idClient;
    
    @JsonProperty("nbPassager")
    private Integer nbPassager;
    
    @JsonProperty("dateHeure")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dateHeure;
    
    @JsonProperty("idHotel")
    private Long idHotel;
    
    @JsonProperty("hotel")
    private HotelDTO hotel;
    
    // Constructeurs
    public ReservationDTO() {}
    
    // Getters et Setters
    public Long getIdReservation() { return idReservation; }
    public void setIdReservation(Long idReservation) { this.idReservation = idReservation; }
    
    public Long getIdClient() { return idClient; }
    public void setIdClient(Long idClient) { this.idClient = idClient; }
    
    public Integer getNbPassager() { return nbPassager; }
    public void setNbPassager(Integer nbPassager) { this.nbPassager = nbPassager; }
    
    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }
    
    public Long getIdHotel() { return idHotel; }
    public void setIdHotel(Long idHotel) { this.idHotel = idHotel; }
    
    public HotelDTO getHotel() { return hotel; }
    public void setHotel(HotelDTO hotel) { this.hotel = hotel; }
    
    public String getHotelNom() {
        return hotel != null ? hotel.getNom() : null;
    }
    
    @Override
    public String toString() {
        return "ReservationDTO{" +
                "idReservation=" + idReservation +
                ", dateHeure=" + dateHeure +
                ", nbPassager=" + nbPassager +
                '}';
    }
    
    public static class HotelDTO {
        @JsonProperty("idHotel")
        private Long idHotel;
        
        @JsonProperty("nom")
        private String nom;
        
        public HotelDTO() {}
        
        public Long getIdHotel() { return idHotel; }
        public void setIdHotel(Long idHotel) { this.idHotel = idHotel; }
        
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
    }
}