package com.uvs.recrutment.dto;

public class LoginRequest {
    private String email;
    private String password;

    // Getters
    public String getEmail() {
        return email;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}