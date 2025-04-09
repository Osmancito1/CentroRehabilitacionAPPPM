package com.example.clinicadiseo.Data_Models

data class Diagnostico(
    val id_diagnostico: Int,
    val descripcion: String,
    val tratamiento: String,
    val estado: Boolean,
    val paciente: Paciente?,
    val terapeuta: Terapeuta?,
    val id_paciente: Int,
    val id_terapeuta: Int
)




data class DiagnosticoRequest(
    val id_paciente: Int,
    val id_terapeuta: Int,
    val descripcion: String?,
    val tratamiento: String
)

data class DiagnosticoResponse(
    val result: List<Diagnostico>
)