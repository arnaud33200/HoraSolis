package ca.arnaud.horasolis

import androidx.annotation.StringRes

enum class City(
    @StringRes val nameRes: Int,
    val latitude: Double,
    val longitude: Double,
    val timeZone: String
) {
    Thiviers(R.string.city_thiviers, 45.4151643, 0.9151798, "Europe/Paris"),
    Toronto(R.string.city_toronto, 43.6532, -79.3832, "America/New_York"),
    Bordeaux(R.string.city_bordeaux, 44.8416, -0.5811, "Europe/Paris"),
    Courtalain(R.string.city_courtalain, 48.079976, 1.135666, "Europe/Paris"),
    Sydney(R.string.city_sydney, -33.8688, 151.2093, "Australia/Sydney"),
    Tokyo(R.string.city_tokyo, 35.682839, 139.759455, "Asia/Tokyo"),
    CapeTown(R.string.city_cape_town, -33.924869, 18.424055, "Africa/Johannesburg"),
    LosAngeles(R.string.city_los_angeles, 34.052235, -118.243683, "America/Los_Angeles"),
    SaoPaulo(R.string.city_sao_paulo, -23.55052, -46.633308, "America/Sao_Paulo"),
    Mumbai(R.string.city_mumbai, 19.07609, 72.877426, "Asia/Kolkata"),
    Cairo(R.string.city_cairo, 30.04442, 31.235712, "Africa/Cairo"),
    Moscow(R.string.city_moscow, 55.755825, 37.617298, "Europe/Moscow"),
    Dubai(R.string.city_dubai, 25.276987, 55.296249, "Asia/Dubai"),
    Beijing(R.string.city_beijing, 39.9042, 116.407396, "Asia/Shanghai"),
    RioDeJaneiro(R.string.city_rio_de_janeiro, -22.906847, -43.172896, "America/Sao_Paulo");
}
