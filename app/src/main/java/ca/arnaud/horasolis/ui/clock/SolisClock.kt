package ca.arnaud.horasolis.ui.clock

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme
import kotlin.math.cos
import kotlin.math.sin

/**
 * UI model for the SolisClock composable.
 *
 * @property dayStartAngle The starting angle (in degrees) for the day arc,
 *  where 0 is at 3 o'clock and angles increase clockwise.
 *  Typical range: -90f (12 o'clock) to 270f (back to 12 o'clock).
 *  -90f represents the top of the circle (12 o'clock).
 * @property daySweepAngle The sweep angle (in degrees) for the day arc, representing the duration of daylight.
 *   Range: 0f to 360f. For example, 180f means half the circle is day.
 */
data class SolisClockModel(
    val dayStartAngle: Float,
    val daySweepAngle: Float,
)

@Composable
fun SolisClock(
    model: SolisClockModel,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val daySurfaceColor = HoraSolisTheme.colors.daySurface
        val onDaySurfaceColor = HoraSolisTheme.colors.onDaySurface
        val nightSurfaceColor = HoraSolisTheme.colors.nightSurface
        val onNightSurfaceColor = HoraSolisTheme.colors.onNightSurface
        val clockBorderColor = HoraSolisTheme.colors.materialColorScheme
            .onSurface.copy(alpha = 0.5f)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2 * 0.8f
            val arcSize = Size(radius * 2, radius * 2)
            val center = Offset(size.width / 2, size.height / 2)
            val topLeft = Offset(center.x - radius, center.y - radius)

            // Night
            drawNightCircle(center, radius, nightSurfaceColor)
            drawLighterNightSections(
                model, topLeft, arcSize, onNightSurfaceColor.copy(alpha = 0.2f)
            )
            drawNightHourMarkers(model, center, radius, onNightSurfaceColor)

            // Day
            drawDayArc(model, topLeft, arcSize, daySurfaceColor)
            drawLighterDaySections(
                model, topLeft, arcSize, onDaySurfaceColor.copy(alpha = 0.2f),
            )
            drawDayHourMarkers(model, center, radius, onDaySurfaceColor)

            drawClockBorder(center, radius, clockBorderColor)

        }
    }
}

private fun DrawScope.drawNightCircle(center: Offset, radius: Float, color: Color) {
    drawCircle(
        color = color,
        center = center,
        radius = radius,
    )
}

private fun DrawScope.drawDayArc(
    model: SolisClockModel,
    topLeft: Offset,
    arcSize: Size,
    color: Color
) {
    drawArc(
        color = color,
        startAngle = model.dayStartAngle,
        sweepAngle = model.daySweepAngle,
        useCenter = true,
        topLeft = topLeft,
        size = arcSize
    )
}

private fun DrawScope.drawLighterDaySections(
    model: SolisClockModel,
    topLeft: Offset,
    arcSize: Size,
    color: Color
) {
    val dayHourSweepLight = model.daySweepAngle / 12f
    for (i in 0 until 12 step 2) {
        val sectionStart = model.dayStartAngle + dayHourSweepLight * i
        drawArc(
            color = color,
            startAngle = sectionStart,
            sweepAngle = dayHourSweepLight,
            useCenter = true,
            topLeft = topLeft,
            size = arcSize
        )
    }
}

private fun DrawScope.drawLighterNightSections(
    model: SolisClockModel,
    topLeft: Offset,
    arcSize: Size,
    color: Color
) {
    val nightSweep = 360f - model.daySweepAngle
    val nightHourSweepLight = nightSweep / 12f
    val nightStartAngle = (model.dayStartAngle + model.daySweepAngle) % 360f
    for (i in 0 until 12 step 2) {
        val sectionStart = nightStartAngle + nightHourSweepLight * i
        drawArc(
            color = color,
            startAngle = sectionStart,
            sweepAngle = nightHourSweepLight,
            useCenter = true,
            topLeft = topLeft,
            size = arcSize
        )
    }
}

private fun DrawScope.drawClockBorder(center: Offset, radius: Float, color: Color) {
    drawCircle(
        color = color,
        center = center,
        radius = radius,
        style = Stroke(width = 4.dp.toPx())
    )
}

private fun DrawScope.drawDayHourMarkers(
    model: SolisClockModel,
    center: Offset,
    radius: Float,
    color: Color
) {
    val dayHourSweep = model.daySweepAngle / 12f
    for (i in 0 until 12) {
        val angle = model.dayStartAngle + dayHourSweep * i + dayHourSweep / 2f
        val rad = Math.toRadians(angle.toDouble())
        val markerRadius = radius * 0.95f
        val x = center.x + markerRadius * cos(rad).toFloat()
        val y = center.y + markerRadius * sin(rad).toFloat()
        drawCircle(
            color = color,
            center = Offset(x, y),
            radius = 4.dp.toPx()
        )
    }
}

private fun DrawScope.drawNightHourMarkers(
    model: SolisClockModel,
    center: Offset,
    radius: Float,
    color: Color
) {
    val nightSweep = 360f - model.daySweepAngle
    val nightHourSweep = nightSweep / 12f
    val nightStartAngle = (model.dayStartAngle + model.daySweepAngle) % 360f
    for (i in 0 until 12) {
        val angle = nightStartAngle + nightHourSweep * i + nightHourSweep / 2f
        val rad = Math.toRadians(angle.toDouble())
        val markerRadius = radius * 0.95f
        val x = center.x + markerRadius * Math.cos(rad).toFloat()
        val y = center.y + markerRadius * Math.sin(rad).toFloat()
        drawCircle(
            color = color,
            center = Offset(x, y),
            radius = 4.dp.toPx()
        )
    }
}

@PreviewLightDark
@Composable
fun SolisClockPreview() {
    HoraSolisTheme {
        Surface {
            SolisClock(
                model = SolisClockModel(
                    dayStartAngle = -90f,
                    daySweepAngle = 160f,
                ),
                modifier = Modifier.size(300.dp)
            )
        }
    }
}
