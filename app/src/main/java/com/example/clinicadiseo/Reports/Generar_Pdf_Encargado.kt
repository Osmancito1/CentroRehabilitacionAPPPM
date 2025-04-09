package com.example.clinicadiseo.Reports

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.example.clinicadiseo.R
import com.example.clinicadiseo.Data_Models.Encargado
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun generarYCompartirPDFEncargado(context: Context, encargados: List<Encargado>) {
    if (encargados.isEmpty()) return

    var pdfDocument: PdfDocument? = null
    var fileOutputStream: FileOutputStream? = null
    var file: File? = null

    try {
        pdfDocument = PdfDocument()
        val paint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 12f
        }
        val titlePaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 20f
            isFakeBoldText = true
        }
        val headerPaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 14f
            isFakeBoldText = true
        }

        val pageInfo = PdfDocument.PageInfo.Builder(842, 595, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val startX = 50f
        var yPos = 40f

        // Logo
        val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logoalt)?.let {
            Bitmap.createScaledBitmap(it, 90, 90, true)
        }
        logoBitmap?.let {
            canvas.drawBitmap(it, startX, yPos, paint)
        }

        // Título
        titlePaint.textAlign = Paint.Align.CENTER
        canvas.drawText("LISTA DE ENCARGADOS", 421f, yPos + 50f, titlePaint)
        titlePaint.textAlign = Paint.Align.LEFT
        yPos += 110f  // Aumentado el espacio después del logo

        // Línea divisoria antes de la fecha
        canvas.drawLine(startX, yPos - 20f, 792f, yPos - 20f, paint)

        // Fecha
        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Generado: $fecha", startX, yPos, paint)
        yPos += 30f

        // Encabezados de tabla
        canvas.drawText("ID", startX, yPos, headerPaint)
        canvas.drawText("Nombre", startX + 100f, yPos, headerPaint)
        canvas.drawText("Teléfono", startX + 350f, yPos, headerPaint)
        canvas.drawText("Dirección", startX + 500f, yPos, headerPaint)
        yPos += 30f

        // Línea divisoria
        canvas.drawLine(startX, yPos, 792f, yPos, paint)
        yPos += 20f

        // Contenido de la lista
        paint.textSize = 10f
        encargados.forEach { encargado ->
            if (yPos > 550f) { // Si llega al final de la página
                pdfDocument.finishPage(page)
                val newPage = pdfDocument.startPage(pageInfo)
                canvas.drawBitmap(logoBitmap!!, startX, 40f, paint)
                yPos = 40f + 110f + 30f + 20f // Reiniciar posición Y (ajustado para coincidir con los nuevos espacios)
            }

            canvas.drawText(encargado.id_encargado.toString(), startX, yPos, paint)
            canvas.drawText("${encargado.nombre} ${encargado.apellido}", startX + 100f, yPos, paint)
            canvas.drawText(encargado.telefono ?: "N/A", startX + 350f, yPos, paint)

            // Manejar dirección con posible salto de línea
            val direccion = encargado.direccion ?: "N/A"
            yPos = wrapTextEncargado(canvas, direccion, startX + 500f, yPos, paint, 250f)

            yPos += 20f // Espacio entre registros
        }

        pdfDocument.finishPage(page)

        // Crear archivo
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        file = File.createTempFile(
            "encargados_$timeStamp",
            ".pdf",
            context.cacheDir
        ).apply {
            createNewFile()
        }

        // Escribir PDF
        fileOutputStream = FileOutputStream(file)
        pdfDocument.writeTo(fileOutputStream)

        // Compartir archivo
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file!!
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir lista de encargados"))

    } catch (e: Exception) {
        Log.e("PDF_Error", "Error al generar PDF", e)
        throw e
    } finally {
        try {
            fileOutputStream?.close()
        } catch (e: Exception) {
            Log.e("PDF_Error", "Error al cerrar stream", e)
        }
        try {
            pdfDocument?.close()
        } catch (e: Exception) {
            Log.e("PDF_Error", "Error al cerrar PDF", e)
        }
    }
}

fun wrapTextEncargado(
    canvas: Canvas,
    text: String,
    startX: Float,
    startY: Float,
    paint: Paint,
    maxWidth: Float,
    lineSpacing: Float = 15f
): Float {
    var y = startY
    val words = text.split(" ")
    var line = ""

    for (word in words) {
        val testLine = if (line.isEmpty()) word else "$line $word"
        val width = paint.measureText(testLine)
        if (width > maxWidth) {
            canvas.drawText(line, startX, y, paint)
            line = word
            y += lineSpacing
        } else {
            line = testLine
        }
    }

    if (line.isNotEmpty()) {
        canvas.drawText(line, startX, y, paint)
        y += lineSpacing
    }

    return y
}