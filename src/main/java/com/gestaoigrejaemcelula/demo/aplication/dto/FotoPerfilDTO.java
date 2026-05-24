package com.gestaoigrejaemcelula.demo.aplication.dto;


public class FotoPerfilDTO {
    private String fotoBase64; // "data:image/jpeg;base64,/9j/4AAQ..."
    // getter/setter

    public String getFotoBase64() {
        return fotoBase64;
    }

    public void setFotoBase64(String fotoBase64) {
        this.fotoBase64 = fotoBase64;
    }
}