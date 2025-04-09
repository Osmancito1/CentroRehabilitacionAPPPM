package com.example.clinicadiseo.Data_Models

data class Terapeuta(
    val id_terapeuta: Int,
    val nombre: String,
    val apellido: String,
    val especialidad: String,
    val telefono: String
)

data class TerapeutaRequest(
    val nombre: String,
    val apellido: String,
    val especialidad: String,
    val telefono: String
)

data class TerapeutaResponse(
    val result: List<Terapeuta>
)