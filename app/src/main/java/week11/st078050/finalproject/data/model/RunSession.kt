package week11.st078050.finalproject.data.model

data class RoutePoint(
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

data class RunSession(
    val id: String = "",
    val userId: String = "",
    val distanceMeters: Double = 0.0,
    val durationMillis: Long = 0L,
    val avgPaceSecPerKm: Double = 0.0,
    val startedAt: Long = 0L,
    val endedAt: Long = 0L,
    val route: List<RoutePoint> = emptyList()
)
