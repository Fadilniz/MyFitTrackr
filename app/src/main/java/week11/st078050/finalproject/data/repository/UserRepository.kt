package week11.st078050.finalproject.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import week11.st078050.finalproject.data.model.User

class UserRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private fun currentUserId(): String? = auth.currentUser?.uid

    // 🔹 Get current user's profile from Firestore
    suspend fun getCurrentUserProfile(): User? {
        val uid = currentUserId() ?: return null

        val snapshot = firestore
            .collection("users")
            .document(uid)
            .get()
            .await()

        return snapshot.toObject(User::class.java)
    }

    // 🔹 Update profile fields (no photo)
    suspend fun updateProfile(
        username: String,
        heightCm: Int,
        weightKg: Int,
        stepGoal: Int
    ): Boolean {
        val uid = currentUserId() ?: return false

        val updates = mapOf(
            "username" to username,
            "heightCm" to heightCm,
            "weightKg" to weightKg,
            "stepGoal" to stepGoal
        )

        return try {
            firestore.collection("users")
                .document(uid)
                .update(updates)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // 🔹 Update just photo URL
    suspend fun updatePhotoUrl(photoUrl: String): Boolean {
        val uid = currentUserId() ?: return false

        return try {
            firestore.collection("users")
                .document(uid)
                .update("photoUrl", photoUrl)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // 🔹 Reset height/weight/goal and photo
    suspend fun resetProfileData(): Boolean {
        val uid = currentUserId() ?: return false

        val updates = mapOf(
            "heightCm" to 0,
            "weightKg" to 0,
            "stepGoal" to 0,
            "photoUrl" to ""
        )

        return try {
            firestore.collection("users")
                .document(uid)
                .update(updates)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
