package com.example.clinicadiseo.Data_Models

data class LoginResponse(
    val message: String,
    val token: String
)


data class LoginRequest(
    val email: String,
    val password: String
)

