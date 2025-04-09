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
import com.example.clinicadiseo.Data_Models.Producto
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun generarYCompartirPDFProducto(context: Context, productos: List<Producto>) {
    if (productos.isEmpty()) return
    var pdfDocument: PdfDocument? = null
    var fileOutputStream: FileOutputStream? = null
    var file: File? = null

    try {
        pdfDocument = PdfDocument()
        val paint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 10f
        }
        val titlePaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 16f
            isFakeBoldText = true
        }
        val headerPaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 12f
            isFakeBoldText = true
        }


        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val startX = 30f
        var yPos = 40f


        val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logoalt)?.let {
            Bitmap.createScaledBitmap(it, 90, 90, true)
        }
        logoBitmap?.let {
            canvas.drawBitmap(it, startX, yPos, paint)
        }


        titlePaint.textAlign = Paint.Align.CENTER
        canvas.drawText("LISTA DE PRODUCTOS", 295f, yPos + 50f, titlePaint)
        titlePaint.textAlign = Paint.Align.LEFT
        yPos += 110f


        canvas.drawLine(startX, yPos - 20f, 565f, yPos - 20f, paint)


        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Generado: $fecha", startX, yPos, paint)
        yPos += 30f


        canvas.drawText("ID", startX, yPos, headerPaint)
        canvas.drawText("Nombre", startX + 80f, yPos, headerPaint)
        canvas.drawText("Categoría", startX + 250f, yPos, headerPaint)
        canvas.drawText("Cantidad", startX + 400f, yPos, headerPaint)
        yPos += 20f


        canvas.drawLine(startX, yPos, 565f, yPos, paint)
        yPos += 10f


        paint.textSize = 10f
        productos.forEach { producto ->
            if (yPos > 750f) {
                pdfDocument.finishPage(page)
                val newPage = pdfDocument.startPage(pageInfo)
                canvas.drawBitmap(logoBitmap!!, startX, 40f, paint)
                yPos = 40f + 110f + 30f + 20f
            }

            canvas.drawText(producto.id_producto.toString(), startX, yPos, paint)
            canvas.drawText(producto.nombre, startX + 80f, yPos, paint)
            canvas.drawText(producto.categoria, startX + 250f, yPos, paint)
            canvas.drawText(producto.cantidad_disponible.toString(), startX + 400f, yPos, paint)

            yPos += 15f
        }

        pdfDocument.finishPage(page)


        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        file = File.createTempFile(
            "productos_$timeStamp",
            ".pdf",
            context.cacheDir
        ).apply {
            createNewFile()
        }


        fileOutputStream = FileOutputStream(file)
        pdfDocument.writeTo(fileOutputStream)


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
        context.startActivity(Intent.createChooser(intent, "Compartir lista de productos"))

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



fun wrapTextProducto(
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
