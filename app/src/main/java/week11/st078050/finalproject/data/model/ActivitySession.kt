package week11.st078050.finalproject.data.model


data class ActivitySession(
    // Firestore Document ID (often set after creation)
    val id: String = "",
    val userId: String = "", // Important for querying and security rules
    val type: String = "", // "Run", "Steps", "Pose"
    val timestamp: Long = System.currentTimeMillis(),
    val distanceKm: Double = 0.0,
    val durationSeconds: Long = 0,
    val caloriesKcal: Int = 0,
    val steps: Int = 0,

)