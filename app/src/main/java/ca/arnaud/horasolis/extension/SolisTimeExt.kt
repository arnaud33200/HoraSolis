package ca.arnaud.horasolis.extension

import ca.arnaud.horasolis.domain.model.SolisTime

fun SolisTime.format(): String {
    val emoji = when (type) {
        SolisTime.Type.Day -> "\u2600\uFE0F"
        SolisTime.Type.Night -> "\uD83C\uDF1A"
    }
    return "%02d %s %02d".format(hour, emoji, minute)
}
