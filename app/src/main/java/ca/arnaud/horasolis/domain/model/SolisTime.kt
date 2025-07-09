package ca.arnaud.horasolis.domain.model

data class SolisTime(
    val hour: Int,
    val minute: Int,
    val type: Type,
) {

    enum class Type {
        Day, Night;

        companion object {

            fun fromNameOrNull(name: String): Type? {
                return Type.entries.firstOrNull { it.name == name }
            }
        }
    }
}
