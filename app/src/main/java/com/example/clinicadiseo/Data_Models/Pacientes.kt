package com.example.clinicadiseo.Data_Models


import java.text.SimpleDateFormat
import java.util.*

data class Paciente(
    val id_paciente: Int,
    val nombre: String,
    val apellido: String,
    val fecha_nacimiento: String,
    val telefono: String,
    val direccion: String,
    val id_encargado: Int,
    val encargado: Encargado?
) {
    val edad: Int
        get() {
            return try {
                val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val nacimiento = formato.parse(fecha_nacimiento)
                val hoy = Calendar.getInstance()
                val fechaNacimiento = Calendar.getInstance().apply { time = nacimiento!! }

                var edadCalculada = hoy.get(Calendar.YEAR) - fechaNacimiento.get(Calendar.YEAR)
                if (hoy.get(Calendar.DAY_OF_YEAR) < fechaNacimiento.get(Calendar.DAY_OF_YEAR)) {
                    edadCalculada--
                }
                edadCalculada
            } catch (e: Exception) {
                0
            }
        }
}


data class PacienteRequest(
    val nombre: String,
    val apellido: String,
    val fecha_nacimiento: String,
    val telefono: String,
    val direccion: String,
    val id_encargado: Int
)

data class PacienteResponse(
    val result: List<Paciente>
)
