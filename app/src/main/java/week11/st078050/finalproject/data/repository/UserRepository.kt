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

    // 🔹 Update username for current user , and from
    suspend fun updateUsername(newUsername: String): Boolean {
        val uid = currentUserId() ?: return false

        return try {
            firestore.collection("users")
                .document(uid)
                .update("username", newUsername)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
