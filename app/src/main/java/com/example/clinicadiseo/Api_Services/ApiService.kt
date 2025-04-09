package com.example.clinicadiseo.Api_Services

import PrestamoRequest
import PrestamoResponse
import com.example.clinicadiseo.Data_Models.ApiResponse
import com.example.clinicadiseo.Data_Models.BodegaRequest
import com.example.clinicadiseo.Data_Models.BodegaResponse
import com.example.clinicadiseo.Data_Models.CitaRequest
import com.example.clinicadiseo.Data_Models.CitaResponse
import com.example.clinicadiseo.Data_Models.CompraRequest
import com.example.clinicadiseo.Data_Models.CompraResponse
import com.example.clinicadiseo.Data_Models.DiagnosticoRequest
import com.example.clinicadiseo.Data_Models.DiagnosticoResponse
import com.example.clinicadiseo.Data_Models.Encargado
import com.example.clinicadiseo.Data_Models.EncargadoResponse
import com.example.clinicadiseo.Data_Models.LoginRequest
import com.example.clinicadiseo.Data_Models.LoginResponse
import com.example.clinicadiseo.Data_Models.PacienteRequest
import com.example.clinicadiseo.Data_Models.PacienteResponse
import com.example.clinicadiseo.Data_Models.Producto
import com.example.clinicadiseo.Data_Models.ProductoResponse
import com.example.clinicadiseo.Data_Models.ProductoStockUpdate
import com.example.clinicadiseo.Data_Models.Terapeuta
import com.example.clinicadiseo.Data_Models.TerapeutaResponse
import com.example.clinicadiseo.Data_Models.UsuarioRequest
import com.example.clinicadiseo.Data_Models.UsuariosResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {


 // LOGIN ROUTE

 @POST("Api/usuarios/login")
 fun login(@Body request: LoginRequest): Call<LoginResponse>


 // USUARIOS ROUTES

 @GET("Api/usuarios/getusuarios")
 fun getUsuarios(): Call<UsuariosResponse>

 @POST("Api/usuarios/insertusuarios")
 fun insertUsuario(@Body nuevoUsuario: UsuarioRequest): Call<Void>

 @PUT("Api/usuarios/updateusuarios")
 fun updateUsuario(
  @Query("id_usuario") id: Int,
  @Body usuario: UsuarioRequest
 ): Call<Void>

 @DELETE("Api/usuarios/deleteusuarios")
 fun deleteUsuario(
  @Query("id_usuario") id: Int
 ): Call<Void>


 // PACIENTES

 @GET("Api/pacientes/getpacientes")
 fun getPacientes(): Call<PacienteResponse>

 @POST("Api/pacientes/insertpacientes")
 fun insertPaciente(@Body paciente: PacienteRequest): Call<Void>

 @PUT("Api/pacientes/updatepacientes")
 fun updatePaciente(@Query("paciente_id") id: Int, @Body paciente: PacienteRequest): Call<Void>

 @DELETE("Api/pacientes/deletepacientes")
 fun deletePaciente(@Query("paciente_id") id: Int): Call<Void>


 //ENCARGADOS

 @GET("Api/encargados/getencargados")
 fun getEncargados(): Call<EncargadoResponse>

 @POST("Api/encargados/insertencargados")
 fun insertEncargado(@Body encargado: Encargado): Call<ApiResponse>

 @PUT("Api/encargados/updateencargados")
 fun updateEncargado(@Query("encargado_id") id: Int, @Body encargado: Encargado): Call<ApiResponse>

 @DELETE("Api/encargados/deleteencargados")
 fun deleteEncargado(@Query("encargado_id") id: Int): Call<ApiResponse>


 //TERAPEUTA

 @GET("Api/terapeutas/getTerapeutas")
 fun getTerapeutas(): Call<TerapeutaResponse>

 @POST("Api/terapeutas/insertTerapeutas")
 fun insertTerapeuta(@Body terapeuta: Terapeuta): Call<ApiResponse>

 @PUT("Api/terapeutas/updateTerapeutas")
 fun updateTerapeuta(@Query("terapeuta_id") id: Int, @Body terapeuta: Terapeuta): Call<ApiResponse>

 @DELETE("Api/terapeutas/deleteTerapeutas")
 fun deleteTerapeuta(@Query("terapeuta_id") id: Int): Call<ApiResponse>


 //DIAGNOSTICO

 @GET("Api/diagnostico/getDiagnosticos")
 fun getDiagnosticos(): Call<DiagnosticoResponse>

 @POST("Api/diagnostico/insertDiagnosticos")
 fun insertDiagnostico(@Body diagnostico: DiagnosticoRequest): Call<ApiResponse>

 @PUT("Api/diagnostico/updateDiagnosticos")
 fun updateDiagnostico(@Query("diagnostico_id") id: Int, @Body diagnostico: DiagnosticoRequest): Call<ApiResponse>

 @DELETE("Api/diagnostico/deleteDiagnosticos")
 fun deleteDiagnostico(@Query("diagnostico_id") id: Int): Call<ApiResponse>

 //PRODUCTOS

 @GET("Api/productos/getProductos")
 fun getProductos(): Call<ProductoResponse>

 @POST("Api/productos/insertProductos")
 fun insertProducto(@Body producto: Producto): Call<ApiResponse>

 @PUT("Api/productos/updateProductos")
 fun updateProducto(@Query("producto_id") id: Int, @Body producto: Producto): Call<ApiResponse>

 @PUT("Api/productos/updateProductoStock")
 fun updateProductoStock(
  @Query("producto_id") id: Int,
  @Body producto: ProductoStockUpdate
 ): Call<ApiResponse>

 @DELETE("Api/productos/deleteProductos")
 fun deleteProducto(@Query("producto_id") id: Int): Call<ApiResponse>

 //BODEGA

 @GET("Api/bodega/GetBodegas")
 fun getBodegas(): Call<BodegaResponse>

 @POST("Api/bodega/InsertBodega")
 fun insertBodegas(@Body bodega: BodegaRequest): Call<ApiResponse>

 @PUT("Api/bodega/UpdateBodega")
 fun updateBodegas(@Query("bodega_id") id: Int, @Body bodega: BodegaRequest): Call<ApiResponse>

 @DELETE("Api/bodega/DeleteBodega")
 fun deleteBodegas(@Query("bodega_id") id: Int): Call<ApiResponse>

 //Prestamo
 @GET("Api/prestamos/getPrestamos")
 fun getPrestamos(): Call<PrestamoResponse>

 @POST("Api/prestamos/insertPrestamos")
 fun insertPrestamos(@Body prestamo: PrestamoRequest): Call<ApiResponse>

 @PUT("Api/prestamos/updatePrestamos")
 fun updatePrestamos(@Query("prestamo_id") id: Int, @Body prestamo: PrestamoRequest): Call<ApiResponse>

 @DELETE("Api/prestamos/deletePrestamos")
 fun deletePrestamos(@Query("prestamo_id") id: Int): Call<ApiResponse>

 //CITAS
 @GET("/Api/citas/getCitas")
 fun getCitas(
 @Query("fecha") fecha: String? = null,
 @Query("id_terapeuta") idTerapeuta: Int? = null
 ): Call<CitaResponse>

 @POST("/Api/citas/insertCita")
 fun insertCita(
  @Body citaRequest: CitaRequest
 ): Call<ApiResponse>

 @PUT("/Api/citas/updateCita")
 fun updateCita(
  @Query("cita_id") citaId: Int,
  @Body citaRequest: CitaRequest
 ): Call<ApiResponse>

 @DELETE("/Api/citas/deleteCita")
 fun deleteCita(
  @Query("cita_id") citaId: Int
 ): Call<ApiResponse>

 //COMPRAS

 @GET("Api/compras/getCompras")
 fun getCompras(): Call<CompraResponse>

 @POST("Api/compras/createCompra")
 fun insertCompra(@Body compra: CompraRequest): Call<ApiResponse>

 @PUT("Api/compras/updateCompra")
 fun updateCompra(@Query("id_compra") id: Int, @Body compra: CompraRequest): Call<ApiResponse>

 @DELETE("Api/compras/deleteCompra")
 fun deleteCompra(@Query("id_compra") id: Int): Call<ApiResponse>
}



