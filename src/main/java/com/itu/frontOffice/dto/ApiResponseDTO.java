package com.itu.frontOffice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ApiResponseDTO {
    
    @JsonProperty("code")
    private Integer code;
    
    @JsonProperty("data")
    private DataDTO data;
    
    @JsonProperty("error")
    private Object error;
    
    @JsonProperty("status")
    private String status;
    
    // Getters et Setters
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    
    public DataDTO getData() { return data; }
    public void setData(DataDTO data) { this.data = data; }
    
    public Object getError() { return error; }
    public void setError(Object error) { this.error = error; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // Classe interne pour la partie data
    public static class DataDTO {
        @JsonProperty("reservations")
        private List<ReservationDTO> reservations;
        
        @JsonProperty("count")
        private Integer count;
        
        public List<ReservationDTO> getReservations() { return reservations; }
        public void setReservations(List<ReservationDTO> reservations) { this.reservations = reservations; }
        
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }
}