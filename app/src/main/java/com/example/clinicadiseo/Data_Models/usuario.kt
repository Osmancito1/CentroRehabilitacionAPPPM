package com.example.clinicadiseo.Data_Models

data class Usuario(
    val id_usuario: Int,
    val nombre: String,
    val email: String,
    val rol: String,
    val estado: String,
    val password: String
)

data class UsuarioRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String,
    val estado: String
)
