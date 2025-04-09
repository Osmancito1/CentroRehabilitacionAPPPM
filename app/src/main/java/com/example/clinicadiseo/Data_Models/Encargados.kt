package com.example.clinicadiseo.Data_Models

data class Encargado(
    val id_encargado: Int,
    val nombre: String,
    val apellido: String,
    val telefono: String?,
    val direccion: String?
)
data class EncargadoResponse(
    val result: List<Encargado>
)

data class ApiResponse(
    val message: String,
    val data: Encargado? = null
)

data class EncargadoRequest(
    val nombre: String,
    val apellido: String,
    val telefono: String?,
    val direccion: String?
)