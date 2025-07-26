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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
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
 * @property dayEndAngle The sweep angle (in degrees) for the day arc, representing the duration of daylight.
 *   Range: 0f to 360f. For example, 180f means half the circle is day.
 * @property needleAngle The angle (in degrees) for the needle, where 0 is at 3 o'clock and angles increase clockwise.
 */
data class SolisClockModel(
    val dayStartAngle: Float,
    val dayEndAngle: Float,
    val needleAngle: Float,
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
        val needleColor = HoraSolisTheme.colors.onDayNightSurface
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
            drawNeedle(model, center, radius, needleColor)
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
        sweepAngle = model.dayEndAngle,
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
    val dayHourSweepLight = model.dayEndAngle / 12f
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
    val nightSweep = 360f - model.dayEndAngle
    val nightHourSweepLight = nightSweep / 12f
    val nightStartAngle = (model.dayStartAngle + model.dayEndAngle) % 360f
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
    val dayHourSweep = model.dayEndAngle / 12f
    for (i in 0 until 12) {
        val angle = model.dayStartAngle + dayHourSweep * i + dayHourSweep / 2f
        val rad = Math.toRadians(angle.toDouble())
        val markerRadius = radius * 0.95f
        val x = center.x + markerRadius * cos(rad).toFloat()
        val y = center.y + markerRadius * sin(rad).toFloat()
        drawCircle(
            color = color,
            center = Offset(x, y),
            radius = 2.dp.toPx()
        )
    }
}

private fun DrawScope.drawNightHourMarkers(
    model: SolisClockModel,
    center: Offset,
    radius: Float,
    color: Color
) {
    val nightSweep = 360f - model.dayEndAngle
    val nightHourSweep = nightSweep / 12f
    val nightStartAngle = (model.dayStartAngle + model.dayEndAngle) % 360f
    for (i in 0 until 12) {
        val angle = nightStartAngle + nightHourSweep * i + nightHourSweep / 2f
        val rad = Math.toRadians(angle.toDouble())
        val markerRadius = radius * 0.95f
        val x = center.x + markerRadius * cos(rad).toFloat()
        val y = center.y + markerRadius * sin(rad).toFloat()
        drawCircle(
            color = color,
            center = Offset(x, y),
            radius = 2.dp.toPx()
        )
    }
}

private fun DrawScope.drawNeedle(model: SolisClockModel, center: Offset, radius: Float, color: Color) {
    val rad = Math.toRadians(model.needleAngle.toDouble())
    val needleLength = radius * 0.95f
    val needleEnd = Offset(
        center.x + needleLength * cos(rad).toFloat(),
        center.y + needleLength * sin(rad).toFloat()
    )
    val needleBaseRadius = 8.dp.toPx()
    val needleTipRadius = 2.dp.toPx()
    // Draw needle body (line)
    drawLine(
        color = color,
        start = center,
        end = needleEnd,
        strokeWidth = 6.dp.toPx()
    )
    // Draw base circle
    drawCircle(
        color = color,
        center = center,
        radius = needleBaseRadius
    )
    // Draw tip circle (smaller, for pointy effect)
    drawCircle(
        color = color,
        center = needleEnd,
        radius = needleTipRadius
    )
}

class SolisClockModelPreviewProvider : PreviewParameterProvider<SolisClockModel> {

    override val values = sequenceOf(
        SolisClockModel(
            dayStartAngle = -90f,
            dayEndAngle = 200f,
            needleAngle = 30f,
        ),
        SolisClockModel(
            dayStartAngle = 0f,
            dayEndAngle = 180f,
            needleAngle = 90f,
        ),
        SolisClockModel(
            dayStartAngle = 45f,
            dayEndAngle = 120f,
            needleAngle = 270f,
        )
    )
}

@PreviewLightDark
@Composable
fun SolisClockPreview(
    @PreviewParameter(SolisClockModelPreviewProvider::class) model: SolisClockModel
) {
    HoraSolisTheme {
        Surface {
            SolisClock(
                model = model,
                modifier = Modifier.size(300.dp)
            )
        }
    }
}
