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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ca.arnaud.horasolis.ui.theme.HoraSolisTheme

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
        val nightSurfaceColor = HoraSolisTheme.colors.nightSurface
        val clockBorderColor = HoraSolisTheme.colors.materialColorScheme
            .onSurface.copy(alpha = 0.5f)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2 * 0.8f
            val arcSize = Size(radius * 2, radius * 2)
            val center = Offset(size.width / 2, size.height / 2)
            val topLeft = Offset(center.x - radius, center.y - radius)
            // Draw full night circle
            drawCircle(
                color = nightSurfaceColor,
                center = center,
                radius = radius,
            )
            // Draw day arc
            drawArc(
                color = daySurfaceColor,
                startAngle = model.dayStartAngle,
                sweepAngle = model.daySweepAngle,
                useCenter = true,
                topLeft = topLeft,
                size = arcSize
            )
            // Draw clock border
            drawCircle(
                color = clockBorderColor,
                center = center,
                radius = radius,
                style = Stroke(width = 4.dp.toPx())
            )
        }
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
