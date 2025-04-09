package com.example.clinicadiseo.Reports

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.clinicadiseo.Data_Models.Cita
import com.example.clinicadiseo.R
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

fun generarYCompartirPdfCitas(context: Context, cita: Cita) {
    val pdfDocument = PdfDocument()
    val paint = Paint()
    val pageInfo = PdfDocument.PageInfo.Builder(842, 595, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    val startX = 50f
    var yPos = 40f

    val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logoalt)
    val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, 90, 90, true)
    canvas.drawBitmap(scaledLogo, startX, yPos, paint)

    paint.textAlign = Paint.Align.CENTER
    paint.textSize = 20f
    paint.isFakeBoldText = true
    canvas.drawText("CENTRO DE REHABILITACIÓN GABRIELA ALVARADO", 421f, yPos + 25f, paint)

    yPos += 90f

    paint.textAlign = Paint.Align.LEFT
    paint.isFakeBoldText = false
    canvas.drawLine(startX, yPos, 792f, yPos, paint)
    yPos += 30f

    paint.textSize = 16f
    paint.isFakeBoldText = true
    canvas.drawText("Terapeuta:", startX, yPos, paint)
    paint.isFakeBoldText = false
    canvas.drawText("${cita.terapeuta?.nombre ?: ""} ${cita.terapeuta?.apellido ?: ""}", startX + 120f, yPos, paint)

    yPos += 40f

    val paciente = cita.paciente
    val nombreCompleto = "${paciente?.nombre ?: ""} ${paciente?.apellido ?: ""}"

    paint.isFakeBoldText = true
    canvas.drawText("Nombre:", startX, yPos, paint)
    paint.isFakeBoldText = false
    canvas.drawText(nombreCompleto, startX + 120f, yPos, paint)

    yPos += 25f

    paint.isFakeBoldText = true
    canvas.drawText("Teléfono:", startX, yPos, paint)
    paint.isFakeBoldText = false
    canvas.drawText("9599-4035", startX + 120f, yPos, paint)

    yPos += 30f
    canvas.drawLine(startX, yPos, 792f, yPos, paint)
    yPos += 30f

    val filaInfoY = yPos

    paint.textSize = 16f
    paint.textAlign = Paint.Align.LEFT

    paint.isFakeBoldText = true
    canvas.drawText("Duración:", startX, filaInfoY, paint)
    paint.isFakeBoldText = false
    canvas.drawText("${cita.duracion_min} min", startX + 120f, filaInfoY, paint)

    paint.isFakeBoldText = true
    canvas.drawText("FOLIO:", 400f, filaInfoY, paint)
    paint.isFakeBoldText = false
    canvas.drawText("${cita.id_cita.toString().padStart(3, '0')}", 470f, filaInfoY, paint)

    val fecha = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", java.util.Locale("es", "ES")).format(Date())
    paint.isFakeBoldText = true
    canvas.drawText("Fecha:", 600f, filaInfoY, paint)
    paint.isFakeBoldText = false
    canvas.drawText(fecha, 660f, filaInfoY, paint)

    yPos += 30f
    canvas.drawLine(startX, yPos, 792f, yPos, paint)
    yPos += 30f

    paint.isFakeBoldText = true
    canvas.drawText("Hora de la cita:", startX, yPos, paint)
    paint.isFakeBoldText = false
    canvas.drawText("${cita.hora_inicio} - ${cita.hora_fin}", startX + 140f, yPos, paint)

    yPos += 25f

    paint.isFakeBoldText = true
    canvas.drawText("Tipo de terapia:", startX, yPos, paint)
    paint.isFakeBoldText = false
    canvas.drawText(cita.tipo_terapia, startX + 140f, yPos, paint)

    yPos += 25f

    paint.isFakeBoldText = true
    canvas.drawText("Estado:", startX, yPos, paint)
    paint.isFakeBoldText = false
    canvas.drawText(cita.estado, startX + 140f, yPos, paint)

    yPos += 30f
    canvas.drawLine(startX, yPos, 792f, yPos, paint)
    yPos += 30f

    paint.textAlign = Paint.Align.CENTER
    paint.textSize = 12f
    paint.isFakeBoldText = true
    canvas.drawText(
        "Costado sur de la iglesia Católica, edificio de 2 plantas color beige con café.",
        421f,
        580f,
        paint
    )

    pdfDocument.finishPage(page)

    val file = File(context.cacheDir, "cita_${cita.id_cita}.pdf")
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
    context.startActivity(Intent.createChooser(intent, "Compartir cita"))
}
