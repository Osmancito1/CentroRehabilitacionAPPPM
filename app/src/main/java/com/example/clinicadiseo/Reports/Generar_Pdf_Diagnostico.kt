package com.example.clinicadiseo.Reports

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.clinicadiseo.R
import com.example.clinicadiseo.Data_Models.Diagnostico
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun generarYCompartirPDF(context: Context, diagnostico: Diagnostico) {
    val pdfDocument = PdfDocument()
    val paint = Paint()
    val pageInfo = PdfDocument.PageInfo.Builder(842, 595, 1).create() // Horizontal A4
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    val startX = 50f
    var yPos = 40f

    // Logo
    val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logoalt)
    val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, 90, 90, true)
    canvas.drawBitmap(scaledLogo, startX, yPos, paint)

    // Encabezado
    paint.textAlign = Paint.Align.CENTER
    paint.textSize = 20f
    paint.isFakeBoldText = true
    canvas.drawText("CENTRO DE REHABILITACIÓN GABRIELA ALVARADO", 421f, yPos + 25f, paint)

    yPos += 90f

    paint.textAlign = Paint.Align.LEFT
    paint.isFakeBoldText = false
    paint.strokeWidth = 1f
    canvas.drawLine(startX, yPos, 792f, yPos, paint)
    yPos += 30f

    // TERAPEUTA Y FOLIO
    paint.textSize = 16f
    paint.isFakeBoldText = true
    canvas.drawText("Terapeuta:", startX, yPos, paint)
    paint.isFakeBoldText = false
    canvas.drawText("${diagnostico.terapeuta?.nombre ?: ""}", startX + 100f, yPos, paint)

    paint.isFakeBoldText = true
    canvas.drawText("FOLIO:", 600f, yPos, paint)
    paint.isFakeBoldText = false
    canvas.drawText("${diagnostico.id_diagnostico.toString().padStart(3, '0')}", 660f, yPos, paint)

    yPos += 40f

    // DATOS DEL PACIENTE
    val paciente = diagnostico.paciente
    val nombreCompleto = "${paciente?.nombre ?: ""} ${paciente?.apellido ?: ""}"
    val edadTexto = "${paciente?.edad ?: 0} años"

    paint.isFakeBoldText = true
    canvas.drawText("Paciente:", startX, yPos, paint)
    paint.isFakeBoldText = false
    canvas.drawText(nombreCompleto, startX + 100f, yPos, paint)

    paint.isFakeBoldText = true
    canvas.drawText("Edad:", 500f, yPos, paint)
    paint.isFakeBoldText = false
    canvas.drawText(edadTexto, 550f, yPos, paint)

    yPos += 25f

    paint.isFakeBoldText = true
    canvas.drawText("Teléfono:", startX, yPos, paint)
    paint.isFakeBoldText = false
    canvas.drawText("9599-4035", startX + 100f, yPos, paint)

    val fecha = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES")).format(Date())
    paint.textAlign = Paint.Align.RIGHT
    paint.textSize = 14f
    paint.isFakeBoldText = true
    canvas.drawText("Fecha: $fecha", 792f, yPos - 20f, paint)

    yPos += 30f
    paint.textAlign = Paint.Align.LEFT
    paint.strokeWidth = 0.5f
    canvas.drawLine(startX, yPos, 792f, yPos, paint)
    yPos += 30f

    // DIAGNÓSTICO
    paint.textSize = 16f
    paint.isFakeBoldText = true
    canvas.drawText("Diagnóstico:", startX, yPos, paint)
    paint.isFakeBoldText = false
    yPos += 20f
    yPos = wrapText(canvas, diagnostico.descripcion ?: "No especificado", startX + 20f, yPos, paint, 700f) + 20f

    // TRATAMIENTO
    paint.isFakeBoldText = true
    canvas.drawText("Tratamiento:", startX, yPos, paint)
    paint.isFakeBoldText = false
    yPos += 20f
    yPos = wrapText(canvas, diagnostico.tratamiento, startX + 20f, yPos, paint, 700f) + 40f

    // FIRMA
    canvas.drawLine(600f, yPos + 50f, 800f, yPos + 50f, paint)
    paint.isFakeBoldText = true
    canvas.drawText("Firma del profesional", 655f, yPos + 70f, paint)

    // FOOTER
    paint.textAlign = Paint.Align.CENTER
    paint.textSize = 12f
    canvas.drawText(
        "Costado sur de la iglesia Católica, edificio de 2 plantas color beige con café.",
        421f,
        580f,
        paint
    )

    pdfDocument.finishPage(page)

    // GUARDAR Y COMPARTIR
    val file = File(context.cacheDir, "receta_diagnostico_${diagnostico.id_diagnostico}.pdf")
    pdfDocument.writeTo(FileOutputStream(file))
    pdfDocument.close()

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

    context.startActivity(Intent.createChooser(intent, "Compartir receta"))
}

fun wrapText(
    canvas: Canvas,
    text: String,
    startX: Float,
    startY: Float,
    paint: Paint,
    maxWidth: Float,
    lineSpacing: Float = 20f
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
