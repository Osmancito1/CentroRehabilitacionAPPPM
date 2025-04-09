package com.example.clinicadiseo.Reports

import Prestamo
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.clinicadiseo.R
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun generarYCompartirPDFPrestamo(context: Context, prestamo: Prestamo) {
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

    // Título y cabecera
    paint.textAlign = Paint.Align.CENTER
    paint.textSize = 20f
    paint.isFakeBoldText = true
    canvas.drawText("CENTRO DE REHABILITACIÓN GABRIELA ALVARADO", 421f, yPos + 25f, paint)

    paint.textSize = 14f
    paint.isFakeBoldText = false
    canvas.drawText("Comprobante de Préstamo", 421f, yPos + 50f, paint)

    // Folio y Fecha
    paint.textAlign = Paint.Align.RIGHT
    paint.isFakeBoldText = true
    canvas.drawText("Folio: ${prestamo.id_prestamo.toString().padStart(3, '0')}", 792f, yPos + 20f, paint)

    val fechaFormateada = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
        .format(SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(prestamo.fecha_prestamo) ?: Date())
    canvas.drawText("Fecha de Préstamo: $fechaFormateada", 792f, yPos + 40f, paint)

    yPos += 90f
    paint.textAlign = Paint.Align.LEFT
    paint.isFakeBoldText = false
    canvas.drawLine(startX, yPos, 792f, yPos, paint)

    yPos += 30f

    // Paciente
    paint.textSize = 16f
    paint.isFakeBoldText = true
    canvas.drawText("Paciente:", startX, yPos, paint)
    paint.isFakeBoldText = false
    canvas.drawText(prestamo.paciente?.nombre ?: "Desconocido", startX + 100f, yPos, paint)

    yPos += 30f

    // Producto
    paint.isFakeBoldText = true
    canvas.drawText("Producto:", startX, yPos, paint)
    paint.isFakeBoldText = false
    canvas.drawText(prestamo.producto?.nombre ?: "Desconocido", startX + 100f, yPos, paint)

    yPos += 30f

    // Estado
    paint.isFakeBoldText = true
    canvas.drawText("Estado:", startX, yPos, paint)
    paint.isFakeBoldText = false
    canvas.drawText(prestamo.estado, startX + 100f, yPos, paint)

    yPos += 30f

    // Fecha de devolución
    paint.isFakeBoldText = true
    canvas.drawText("Fecha de Devolución:", startX, yPos, paint)
    paint.isFakeBoldText = false
    if (prestamo.fecha_devolucion != null) {
        val fechaDevFormateada = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
            .format(SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(prestamo.fecha_devolucion) ?: Date())
        canvas.drawText(fechaDevFormateada, startX + 200f, yPos, paint)
    } else {
        canvas.drawText("No registrada", startX + 200f, yPos, paint)
    }

    pdfDocument.finishPage(page)

    val file = File(context.cacheDir, "comprobante_prestamo_${prestamo.id_prestamo}.pdf")
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
    context.startActivity(Intent.createChooser(intent, "Compartir comprobante de préstamo"))
}