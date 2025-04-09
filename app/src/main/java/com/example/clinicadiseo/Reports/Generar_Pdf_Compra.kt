package com.example.clinicadiseo.Reports


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.clinicadiseo.R
import com.example.clinicadiseo.Data_Models.Compra
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun generarYCompartirPDFCompra(context: Context, compra: Compra) {
    val pdfDocument = PdfDocument()
    val paint = Paint()
    val pageInfo = PdfDocument.PageInfo.Builder(842, 595, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    val startX = 50f
    var yPos = 40f

    // Logo
    val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logoalt)
    val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, 90, 90, true)
    canvas.drawBitmap(scaledLogo, startX, yPos, paint)

    // Título y cabecera derecha
    paint.textAlign = Paint.Align.CENTER
    paint.textSize = 20f
    paint.isFakeBoldText = true
    canvas.drawText("CENTRO DE REHABILITACIÓN GABRIELA ALVARADO", 421f, yPos + 25f, paint)

    paint.textSize = 14f
    paint.isFakeBoldText = false
    canvas.drawText("Factura de Compra", 421f, yPos + 50f, paint)

    // Folio y Fecha (derecha)
    paint.textAlign = Paint.Align.RIGHT
    paint.isFakeBoldText = true
    canvas.drawText("Folio: ${compra.id_compra.toString().padStart(3, '0')}", 792f, yPos + 20f, paint)

    val fechaFormateada = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
        .format(SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(compra.fecha) ?: Date())
    canvas.drawText("Fecha: $fechaFormateada", 792f, yPos + 40f, paint)

    // Línea divisoria
    yPos += 90f
    paint.textAlign = Paint.Align.LEFT
    paint.isFakeBoldText = false
    canvas.drawLine(startX, yPos, 792f, yPos, paint)

    yPos += 30f

    // Donante
    paint.textSize = 16f
    paint.isFakeBoldText = true
    canvas.drawText("Donante:", startX, yPos, paint)
    paint.isFakeBoldText = false
    canvas.drawText(compra.donante, startX + 100f, yPos, paint)

    yPos += 30f

    // Total
    paint.isFakeBoldText = true
    canvas.drawText("Total:", startX, yPos, paint)
    paint.isFakeBoldText = false
    canvas.drawText(String.format("%.2f", compra.total), startX + 100f, yPos, paint)

    yPos += 40f

    // Tabla de detalles
    val headers = listOf("#", "Producto", "Cantidad", "Costo Unitario", "Subtotal")
    val colWidths = listOf(50f, 250f, 120f, 150f, 150f)
    var x = startX

    paint.isFakeBoldText = true
    for ((i, header) in headers.withIndex()) {
        canvas.drawText(header, x, yPos, paint)
        x += colWidths[i]
    }

    paint.isFakeBoldText = false
    yPos += 25f

    for ((index, detalle) in compra.detalle.withIndex()) {
        x = startX
        val nombreProducto = obtenerNombreProductoPorId(detalle.id_producto) // Placeholder
        val subtotal = detalle.costo_unitario * detalle.cantidad

        val valores = listOf(
            (index + 1).toString(),
            nombreProducto,
            detalle.cantidad.toString(),
            String.format("%.2f", detalle.costo_unitario),
            String.format("%.2f", subtotal)
        )

        for ((i, valor) in valores.withIndex()) {
            canvas.drawText(valor, x, yPos, paint)
            x += colWidths[i]
        }
        yPos += 25f
    }

    pdfDocument.finishPage(page)

    val file = File(context.cacheDir, "factura_compra_${compra.id_compra}.pdf")
    pdfDocument.writeTo(FileOutputStream(file))
    pdfDocument.close()

    val uri: Uri = FileProvider.getUriForFile(
        context,
        context.packageName + ".provider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir factura de compra"))
}

// Ejemplo de mapeo estático de productos
fun obtenerNombreProductoPorId(idProducto: Int): String {
    return when (idProducto) {
        1 -> "Silla de Ruedas"
        2 -> "Muletas de Acero"
        else -> "Producto Desconocido"
    }
}
