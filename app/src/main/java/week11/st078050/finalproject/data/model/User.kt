package week11.st078050.finalproject.data.model

data class User(
    // The UID from Firebase Authentication is the document ID
    val uid: String = "",
    val username: String = "",
    val email: String = "",

    // New optional profile fields
    val heightCm: Int = 0,      // 0 = not set
    val weightKg: Int = 0,      // 0 = not set
    val stepGoal: Int = 0       // 0 = not set
)
