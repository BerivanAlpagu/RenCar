package com.turkcell.rencar.feature.vehicles.presentation.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import com.turkcell.rencar.feature.vehicles.domain.model.Vehicle

fun createVehicleMarkerBitmap(context: Context, vehicle: Vehicle, isMine: Boolean = false): Bitmap {
    val priceText = "₺${vehicle.pricePerDay}"

    val colorInt = when {
        isMine -> Color.parseColor("#0B6BCB") // Kullanıcının kendi rezervasyonu/kiralaması — vurgulu mavi
        vehicle.status != "AVAILABLE" -> Color.parseColor("#9AA3AE") // RESERVED/RENTED (başkasına ait) — gri
        vehicle.type == "SEDAN" -> Color.parseColor("#7C5CE6") // Purple for SEDAN
        vehicle.type == "SUV" -> Color.parseColor("#E6A700") // Yellow for SUV
        else -> Color.parseColor("#1FB370") // Green for others (HATCHBACK vb.)
    }

    val scale = context.resources.displayMetrics.density
    
    // Define sizes
    val textSize = 15f * scale
    val paddingX = 14f * scale
    val paddingY = 8f * scale
    val cornerRadius = 16f * scale
    val pointerWidth = 14f * scale
    val pointerHeight = 10f * scale
    
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.textSize = textSize
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    
    // Add space for the car icon (using an emoji or text for now to match design roughly)
    val carIcon = "🚘"
    val carIconWidth = paint.measureText(carIcon) + (4f * scale)
    
    val textWidth = paint.measureText(priceText)
    val width = (carIconWidth + textWidth + paddingX * 2).toInt()
    val bubbleHeight = (textSize + paddingY * 2).toInt()
    val height = bubbleHeight + pointerHeight.toInt()

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    
    // Draw bubble
    paint.color = colorInt
    val rect = RectF(0f, 0f, width.toFloat(), bubbleHeight.toFloat())
    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
    
    // Draw pointer (triangle at bottom center)
    val path = Path()
    path.moveTo(width / 2f - pointerWidth / 2f, bubbleHeight.toFloat())
    path.lineTo(width / 2f + pointerWidth / 2f, bubbleHeight.toFloat())
    path.lineTo(width / 2f, bubbleHeight + pointerHeight)
    path.close()
    canvas.drawPath(path, paint)
    
    // Draw text
    paint.color = Color.WHITE
    // calculate Y to center text vertically in the bubble
    val textMetrics = paint.fontMetrics
    val textY = bubbleHeight / 2f - (textMetrics.ascent + textMetrics.descent) / 2f
    
    // Draw car icon and text
    canvas.drawText(carIcon, paddingX, textY, paint)
    canvas.drawText(priceText, paddingX + carIconWidth, textY, paint)
    
    return bitmap
}

fun createUserLocationBitmap(context: Context): Bitmap {
    val scale = context.resources.displayMetrics.density
    val size = (16f * scale).toInt()
    
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    // White border
    paint.color = Color.WHITE
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
    // Red inner dot
    paint.color = Color.RED
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - (2f * scale), paint)
    
    return bitmap
}
