package com.example.clinicadiseo.Data_Models

data class Producto(
    val id_producto: Int,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val cantidad_disponible: Int
)

data class ProductoRequest(
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val cantidad_disponible: Int
)

data class ProductoResponse(
    val result: List<Producto>
)

data class ProductoStockUpdate(
    val cantidad_a_sumar: Int
)