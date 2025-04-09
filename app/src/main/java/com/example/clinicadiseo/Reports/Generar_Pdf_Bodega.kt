package com.example.clinicadiseo.Reports

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.example.clinicadiseo.R
import com.example.clinicadiseo.Data_Models.Bodega
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun generarYCompartirPDFBodega(context: Context, bodegas: List<Bodega>) {
    if (bodegas.isEmpty()) return

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

        // Cargar y validar logo
        val originalLogo = BitmapFactory.decodeResource(context.resources, R.drawable.logoalt)
            ?: throw IllegalStateException("No se pudo cargar el logo R.drawable.logoalt")
        val logoBitmap = Bitmap.createScaledBitmap(originalLogo, 90, 90, true)

        val pageInfo = PdfDocument.PageInfo.Builder(842, 595, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        val startX = 50f
        var yPos = 40f

        fun drawHeader() {
            canvas.drawBitmap(logoBitmap, startX, yPos, paint)

            titlePaint.textAlign = Paint.Align.CENTER
            canvas.drawText("LISTA DE BODEGA", 421f, yPos + 50f, titlePaint)
            titlePaint.textAlign = Paint.Align.LEFT
            yPos += 110f

            canvas.drawLine(startX, yPos - 20f, 792f, yPos - 20f, paint)

            val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            canvas.drawText("Generado: $fecha", startX, yPos, paint)
            yPos += 30f

            canvas.drawText("ID", startX, yPos, headerPaint)
            canvas.drawText("Producto", startX + 100f, yPos, headerPaint)
            canvas.drawText("Cantidad", startX + 350f, yPos, headerPaint)
            canvas.drawText("Ubicación", startX + 500f, yPos, headerPaint)
            yPos += 30f

            canvas.drawLine(startX, yPos, 792f, yPos, paint)
            yPos += 20f
        }

        drawHeader()

        paint.textSize = 10f
        for (bodega in bodegas) {
            if (yPos > 550f) {
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPos = 40f
                drawHeader()
            }

            canvas.drawText(bodega.id_bodega.toString(), startX, yPos, paint)
            val nombreProducto = bodega.producto?.nombre ?: "N/A"
            yPos = wrapTextBodega(canvas, nombreProducto, startX + 100f, yPos, paint, 250f)
            canvas.drawText(bodega.cantidad.toString(), startX + 350f, yPos, paint)
            canvas.drawText(bodega.ubicacion, startX + 500f, yPos, paint)
            yPos += 20f
        }

        pdfDocument.finishPage(page)

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        file = File.createTempFile("bodega_$timeStamp", ".pdf", context.cacheDir).apply {
            createNewFile()
        }

        fileOutputStream = FileOutputStream(file)
        pdfDocument.writeTo(fileOutputStream)

        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir lista de bodega"))

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

fun wrapTextBodega(
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
