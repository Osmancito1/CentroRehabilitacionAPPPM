import com.example.clinicadiseo.Data_Models.Paciente
import com.example.clinicadiseo.Data_Models.Producto

data class Prestamo(
    val id_prestamo: Int = 0,
    val id_paciente: Int,
    val id_producto: Int,
    val fecha_prestamo: String,
    val fecha_devolucion: String?,
    val paciente: Paciente?,
    val producto: Producto?,
    val estado: String
)

data class PrestamoResponse(
    val result: List<Prestamo>
)

data class PrestamoRequest(
    val id_paciente: Int,
    val id_producto: Int,
    val fecha_prestamo: String,
    val fecha_devolucion: String?,
    val estado: String
)