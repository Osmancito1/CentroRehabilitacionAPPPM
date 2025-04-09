package com.example.clinicadiseo.Data_Models

data class Bodega(
    val id_bodega: Int,
    val id_producto: Int,
    val cantidad: Int,
    val ubicacion: String,
    val producto: Producto?
)

data class BodegaRequest(
    val id_producto: Int,
    val cantidad: Int,
    val ubicacion: String
)

data class BodegaResponse(
    val result: List<Bodega>
)