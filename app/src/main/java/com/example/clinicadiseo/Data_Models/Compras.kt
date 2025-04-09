package com.example.clinicadiseo.Data_Models

data class DetalleCompra(
    val id_detalle: Int,
    val id_producto: Int,
    val cantidad: Int,
    val costo_unitario: Double
)

data class Compra(
    val id_compra: Int,
    val fecha: String,
    val donante: String,
    val total: Double,
    val detalle: List<DetalleCompra>
)

data class CompraRequest(
    val fecha: String,
    val donante: String,
    val total: Double,
    val detalle: List<DetalleCompra>
)

data class CompraResponse(
    val result: List<Compra>
)