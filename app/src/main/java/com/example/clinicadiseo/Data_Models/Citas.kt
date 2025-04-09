package com.example.clinicadiseo.Data_Models

data class Cita(
    val id_cita: Int,
    val id_paciente: Int,
    val id_terapeuta: Int,
    val tipo_terapia: String,
    val fecha: String,
    val hora_inicio: String,
    val hora_fin: String,
    val estado: String,
    val duracion_min: Int,
    val paciente: PacienteCita?,
    val terapeuta: TerapeutaCita?
)

data class PacienteCita(
    val nombre: String,
    val apellido: String
)

data class TerapeutaCita(
    val id_terapeuta: Int,
    val nombre: String,
    val apellido: String
)

data class CitaRequest(
    val id_paciente: Int,
    val id_terapeuta: Int,
    val tipo_terapia: String,
    val fecha: String,
    val hora_inicio: String,
    val hora_fin: String,
    val estado: String,
    val duracion_min: Int
)

data class CitaResponse(
    val result: List<Cita>
)

