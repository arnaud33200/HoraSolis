package ca.arnaud.horasolis.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.time.LocalTime

class SolisTimeTest {

    data class ToCivilTimeTestParam(
        val givenSolisDay: SolisDay,
        val givenSolisTime: SolisTime,
        val expectedCivilTime: LocalTime,
    )

    @ParameterizedTest
    @MethodSource("getToCivilTimeTestParam")
    fun `toCivilTime - GIVEN ToCivilTimeTestParam VERIFY expected result`(
        data: ToCivilTimeTestParam
    ) {
        assertEquals(
            data.expectedCivilTime,
            data.givenSolisTime.toCivilTime(data.givenSolisDay),
        )
    }

    data class ToSolisTimeTestParam(
        val givenLocalTime: LocalTime,
        val givenSolisDay: SolisDay,
        val expectedSolisTime: SolisTime,
    )

    @ParameterizedTest
    @MethodSource("getToSolisTimeTestParam")
    fun `toSolisTime - GIVEN ToSolisTimeTestParam VERIFY expected result`(
        data: ToSolisTimeTestParam
    ) {
        val dateTime = LocalDate.of(2023, 10, 1).atTime(data.givenLocalTime)
        val solisTime = dateTime.toSolisTime(data.givenSolisDay)
        assertEquals(
            data.expectedSolisTime,
            solisTime,
        )
    }

    companion object {

        @JvmStatic
        fun getToCivilTimeTestParam() = listOf(

            // GIVEN first hour of day VERIFY sunrise time
            ToCivilTimeTestParam(
                givenSolisDay = SolisDay(
                    atDate = LocalDate.of(2023, 10, 1),
                    civilSunriseTime = LocalTime.of(6, 0),
                    civilSunsetTime = LocalTime.of(18, 0),
                ),
                givenSolisTime = SolisTime(
                    hour = 1,
                    minute = 0,
                    type = SolisTime.Type.Day,
                ),
                expectedCivilTime = LocalTime.of(6, 0),
            ),

            ToCivilTimeTestParam(
                givenSolisDay = SolisDay(
                    atDate = LocalDate.of(2023, 10, 1),
                    civilSunriseTime = LocalTime.of(4, 0),
                    civilSunsetTime = LocalTime.of(20, 0),
                ),
                givenSolisTime = SolisTime(
                    hour = 2,
                    minute = 0,
                    type = SolisTime.Type.Day,
                ),
                expectedCivilTime = LocalTime.of(5, 20),
            ),

            ToCivilTimeTestParam(
                givenSolisDay = SolisDay(
                    atDate = LocalDate.of(2023, 10, 1),
                    civilSunriseTime = LocalTime.of(4, 0),
                    civilSunsetTime = LocalTime.of(20, 0),
                ),
                givenSolisTime = SolisTime(
                    hour = 1,
                    minute = 59,
                    type = SolisTime.Type.Day,
                ),
                expectedCivilTime = LocalTime.of(5, 18, 40),
            ),

            // GIVEN minutes VERIFY sunrise time + solis minutes
            ToCivilTimeTestParam(
                givenSolisDay = SolisDay(
                    atDate = LocalDate.of(2023, 10, 1),
                    civilSunriseTime = LocalTime.of(4, 0),
                    civilSunsetTime = LocalTime.of(20, 0),
                ),
                givenSolisTime = SolisTime(
                    hour = 1,
                    minute = 30,
                    type = SolisTime.Type.Day,
                ),
                expectedCivilTime = LocalTime.of(4, 40, 0),
            ),

            // GIVEN first hour of night VERIFY sunset time
            ToCivilTimeTestParam(
                givenSolisDay = SolisDay(
                    atDate = LocalDate.of(2023, 10, 1),
                    civilSunriseTime = LocalTime.of(6, 0),
                    civilSunsetTime = LocalTime.of(18, 0),
                ),
                givenSolisTime = SolisTime(
                    hour = 1,
                    minute = 0,
                    type = SolisTime.Type.Night,
                ),
                expectedCivilTime = LocalTime.of(18, 0),
            ),
        )

        @JvmStatic
        fun getToSolisTimeTestParam() = listOf(
            ToSolisTimeTestParam(
                givenLocalTime = LocalTime.of(6, 0),
                givenSolisDay = SolisDay(
                    atDate = LocalDate.of(2023, 10, 1),
                    civilSunriseTime = LocalTime.of(6, 0),
                    civilSunsetTime = LocalTime.of(18, 0),
                ),
                expectedSolisTime = SolisTime(
                    hour = 1,
                    minute = 0,
                    type = SolisTime.Type.Day,
                ),
            ),
            ToSolisTimeTestParam(
                givenLocalTime = LocalTime.of(5, 20),
                givenSolisDay = SolisDay(
                    atDate = LocalDate.of(2023, 10, 1),
                    civilSunriseTime = LocalTime.of(4, 0),
                    civilSunsetTime = LocalTime.of(20, 0),
                ),
                expectedSolisTime = SolisTime(
                    hour = 2,
                    minute = 0,
                    type = SolisTime.Type.Day,
                ),
            ),
            ToSolisTimeTestParam(
                givenLocalTime = LocalTime.of(20, 0),
                givenSolisDay = SolisDay(
                    atDate = LocalDate.of(2023, 10, 1),
                    civilSunriseTime = LocalTime.of(4, 0),
                    civilSunsetTime = LocalTime.of(20, 0),
                ),
                expectedSolisTime = SolisTime(
                    hour = 1,
                    minute = 0,
                    type = SolisTime.Type.Night,
                ),
            ),
            ToSolisTimeTestParam(
                givenLocalTime = LocalTime.of(22, 30),
                givenSolisDay = SolisDay(
                    atDate = LocalDate.of(2023, 10, 1),
                    civilSunriseTime = LocalTime.of(4, 0),
                    civilSunsetTime = LocalTime.of(20, 0),
                ),
                expectedSolisTime = SolisTime(
                    hour = 4,
                    minute = 45,
                    type = SolisTime.Type.Night,
                ),
            ),
            ToSolisTimeTestParam(
                givenLocalTime = LocalTime.of(3, 59),
                givenSolisDay = SolisDay(
                    atDate = LocalDate.of(2023, 10, 1),
                    civilSunriseTime = LocalTime.of(4, 0),
                    civilSunsetTime = LocalTime.of(20, 0),
                ),
                expectedSolisTime = SolisTime(
                    hour = 12,
                    minute = 58,
                    type = SolisTime.Type.Night,
                ),
            ),
        )
    }
}