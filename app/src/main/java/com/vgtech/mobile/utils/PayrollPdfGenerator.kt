package com.vgtech.mobile.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import com.vgtech.mobile.data.model.Employee
import com.vgtech.mobile.data.model.WorkLog
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object PayrollPdfGenerator {

    private val PAGE_WIDTH = 612
    private val PAGE_HEIGHT = 792
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun generateEmployeePayrollPdf(context: Context, employee: Employee, workLogs: List<WorkLog>) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint()
        paint.color = Color.BLACK

        // --- Header ---
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 16f
        canvas.drawText("Comprobante Fiscal Digital por Internet (Nómina)", 30f, 40f, paint)

        val headerBox = RectF(30f, 50f, PAGE_WIDTH - 30f, 130f)
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#E0E0E0")
        canvas.drawRect(headerBox, paint)
        
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK
        paint.strokeWidth = 1f
        canvas.drawRect(headerBox, paint)

        paint.style = Paint.Style.FILL
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 18f
        canvas.drawText("VG TECH S.A. DE C.V.", 40f, 75f, paint)
        
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("RFC: VGT210515XYZ", 40f, 95f, paint)
        canvas.drawText("Reg Fiscal: 601 General de Ley Personas Morales", 40f, 115f, paint)
        canvas.drawText("Lugar de expedición: 12345", 40f, 135f, paint) // Moved out of box to fit
        
        canvas.drawText("Fecha: ${dateFormatter.format(Date())}", 450f, 75f, paint)

        // --- Employee Info Box ---
        val empBox = RectF(30f, 150f, PAGE_WIDTH - 30f, 250f)
        paint.style = Paint.Style.STROKE
        canvas.drawRect(empBox, paint)
        paint.style = Paint.Style.FILL

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 14f
        canvas.drawText(employee.nombreCompleto.uppercase(), 40f, 170f, paint)
        
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 12f
        val empIdStr = employee.uid.take(8).uppercase()
        canvas.drawText("RFC: XXXX000000000", 40f, 190f, paint)
        canvas.drawText("CURP: XXXX000000XXXXXX00", 40f, 210f, paint)
        canvas.drawText("Puesto: ${employee.puesto}", 40f, 230f, paint)

        canvas.drawText("Periodo: Pago actual", 300f, 170f, paint)
        canvas.drawText("Fecha Pago: ${dateFormatter.format(Date())}", 300f, 190f, paint)
        canvas.drawText("Salario Base (Mensual): ${currencyFormatter.format(employee.sueldo)}", 300f, 210f, paint)
        canvas.drawText("Pago por Hora: ${currencyFormatter.format(employee.pagoPorHora)}", 300f, 230f, paint)

        // --- Perceptions & Deductions Tables ---
        val tableTop = 270f
        val percBox = RectF(30f, tableTop, PAGE_WIDTH / 2f, 500f)
        val dedBox = RectF(PAGE_WIDTH / 2f, tableTop, PAGE_WIDTH - 30f, 500f)
        
        paint.style = Paint.Style.STROKE
        canvas.drawRect(percBox, paint)
        canvas.drawRect(dedBox, paint)
        paint.style = Paint.Style.FILL

        // Headers
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 14f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Percepciones", 30f + (PAGE_WIDTH/2f - 30f)/2, tableTop + 20f, paint)
        canvas.drawText("Deducciones", PAGE_WIDTH/2f + (PAGE_WIDTH/2f - 30f)/2, tableTop + 20f, paint)
        paint.textAlign = Paint.Align.LEFT

        // Data
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 12f
        var pY = tableTop + 45f
        
        var totalPercepciones = employee.sueldo / 2 // Suponiendo pago quincenal base
        
        // Agregar horas extra si hay worklogs
        var totalHorasExtras = 0.0
        var horasExtraCount = 0.0
        for (log in workLogs) {
            val overtimeMultiplier = if (log.overtimeRate > 0) log.overtimeRate else 2.0
            totalHorasExtras += log.overtimeHours * log.hourlyRateAtTime * overtimeMultiplier
            horasExtraCount += log.overtimeHours
        }
        
        if (employee.sueldo > 0) {
            canvas.drawText("001", 40f, pY, paint)
            canvas.drawText("Sueldo Quincenal", 75f, pY, paint)
            drawRightAlignedText(canvas, currencyFormatter.format(totalPercepciones), PAGE_WIDTH / 2f - 10f, pY, paint)
            pY += 20f
        }
        
        if (horasExtraCount > 0) {
            canvas.drawText("019", 40f, pY, paint)
            canvas.drawText("Horas Extras (${horasExtraCount}h)", 75f, pY, paint)
            drawRightAlignedText(canvas, currencyFormatter.format(totalHorasExtras), PAGE_WIDTH / 2f - 10f, pY, paint)
            totalPercepciones += totalHorasExtras
        }

        // Deductions
        var dY = tableTop + 45f
        val isr = totalPercepciones * 0.12 // 12% ISR estimado
        val imss = totalPercepciones * 0.025 // 2.5% IMSS estimado
        val totalDeducciones = isr + imss

        canvas.drawText("002", PAGE_WIDTH / 2f + 10f, dY, paint)
        canvas.drawText("ISR", PAGE_WIDTH / 2f + 45f, dY, paint)
        drawRightAlignedText(canvas, currencyFormatter.format(isr), PAGE_WIDTH - 40f, dY, paint)
        dY += 20f
        
        canvas.drawText("001", PAGE_WIDTH / 2f + 10f, dY, paint)
        canvas.drawText("IMSS", PAGE_WIDTH / 2f + 45f, dY, paint)
        drawRightAlignedText(canvas, currencyFormatter.format(imss), PAGE_WIDTH - 40f, dY, paint)

        // --- Totals Box ---
        val totalTop = 500f
        val totalBox = RectF(PAGE_WIDTH / 2f, totalTop, PAGE_WIDTH - 30f, 600f)
        paint.style = Paint.Style.STROKE
        canvas.drawRect(totalBox, paint)
        paint.style = Paint.Style.FILL

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        var tY = totalTop + 25f
        
        canvas.drawText("Subtotal", PAGE_WIDTH / 2f + 10f, tY, paint)
        drawRightAlignedText(canvas, currencyFormatter.format(totalPercepciones), PAGE_WIDTH - 40f, tY, paint)
        tY += 25f

        canvas.drawText("Descuentos", PAGE_WIDTH / 2f + 10f, tY, paint)
        drawRightAlignedText(canvas, currencyFormatter.format(totalDeducciones), PAGE_WIDTH - 40f, tY, paint)
        tY += 25f

        val neto = totalPercepciones - totalDeducciones
        paint.color = Color.parseColor("#1976D2") // Blue for Neto
        canvas.drawText("Neto del recibo", PAGE_WIDTH / 2f + 10f, tY, paint)
        drawRightAlignedText(canvas, currencyFormatter.format(neto), PAGE_WIDTH - 40f, tY, paint)

        // QR Code placeholder / Footer
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        canvas.drawRect(30f, 620f, 130f, 720f, paint)
        paint.style = Paint.Style.FILL
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 8f
        canvas.drawText("Sello digital del CFDI", 150f, 630f, paint)
        canvas.drawText("xxxxxx...", 150f, 645f, paint)
        canvas.drawText("Este documento es una representación impresa de un CFDI", 150f, 700f, paint)

        document.finishPage(page)
        
        val fileName = "Nomina_${employee.nombreCompleto.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
        savePdf(context, document, fileName)
    }

    fun generateGlobalPayrollPdf(context: Context, employees: List<Employee>, allWorkLogs: List<WorkLog>) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint()
        paint.color = Color.BLACK

        // --- Header ---
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 20f
        canvas.drawText("Reporte Global de Nómina", 30f, 50f, paint)
        
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Fecha de generación: ${dateFormatter.format(Date())}", 30f, 75f, paint)
        
        paint.style = Paint.Style.STROKE
        canvas.drawLine(30f, 90f, PAGE_WIDTH - 30f, 90f, paint)
        paint.style = Paint.Style.FILL

        // Table Header
        var y = 120f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Empleado", 30f, y, paint)
        canvas.drawText("Puesto", 200f, y, paint)
        canvas.drawText("Sueldo Base", 320f, y, paint)
        canvas.drawText("Extras", 420f, y, paint)
        canvas.drawText("Total Bruto", 500f, y, paint)
        
        paint.style = Paint.Style.STROKE
        canvas.drawLine(30f, y + 5f, PAGE_WIDTH - 30f, y + 5f, paint)
        paint.style = Paint.Style.FILL

        y += 25f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        var granTotal = 0.0

        for (emp in employees) {
            val empLogs = allWorkLogs.filter { it.employeeUid == emp.uid }
            
            var totalHorasExtras = 0.0
            for (log in empLogs) {
                val overtimeMultiplier = if (log.overtimeRate > 0) log.overtimeRate else 2.0
                totalHorasExtras += log.overtimeHours * log.hourlyRateAtTime * overtimeMultiplier
            }
            
            val baseQuincenal = emp.sueldo / 2
            val totalBruto = baseQuincenal + totalHorasExtras
            granTotal += totalBruto

            canvas.drawText(emp.nombreCompleto.take(20), 30f, y, paint)
            canvas.drawText(emp.puesto.take(15), 200f, y, paint)
            canvas.drawText(currencyFormatter.format(baseQuincenal), 320f, y, paint)
            canvas.drawText(currencyFormatter.format(totalHorasExtras), 420f, y, paint)
            canvas.drawText(currencyFormatter.format(totalBruto), 500f, y, paint)
            y += 20f

            // Create new page if needed
            if (y > PAGE_HEIGHT - 50f) {
                // Not handled in this simple example to keep it concise, assuming it fits 1 page for demo
            }
        }

        // Totals
        paint.style = Paint.Style.STROKE
        canvas.drawLine(30f, y, PAGE_WIDTH - 30f, y, paint)
        paint.style = Paint.Style.FILL

        y += 25f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("GRAN TOTAL BRUTO:", 320f, y, paint)
        canvas.drawText(currencyFormatter.format(granTotal), 500f, y, paint)

        document.finishPage(page)
        
        val fileName = "Nomina_Global_${System.currentTimeMillis()}.pdf"
        savePdf(context, document, fileName)
    }

    private fun drawRightAlignedText(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint) {
        val width = paint.measureText(text)
        canvas.drawText(text, x - width, y, paint)
    }

    private fun savePdf(context: Context, document: PdfDocument, fileName: String) {
        try {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(dir, fileName)
            document.writeTo(FileOutputStream(file))
            document.close()
            Toast.makeText(context, "PDF guardado en Descargas: $fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al guardar el PDF: ${e.message}", Toast.LENGTH_LONG).show()
            document.close()
        }
    }
}
