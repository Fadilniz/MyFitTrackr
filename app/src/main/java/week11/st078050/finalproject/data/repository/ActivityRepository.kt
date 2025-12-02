package week11.st078050.finalproject.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import week11.st078050.finalproject.data.model.ActivitySession

class ActivityRepository(private val firestore: FirebaseFirestore) {

    // Helper to get the correct collection path based on Firebase Auth UID
    private fun getUserSessionsCollection(userId: String) =
        firestore.collection("users").document(userId).collection("sessions")

    // --- 5b: Create data ---
    // Uses suspend and .await() for safe Coroutine execution
    suspend fun saveActivitySession(userId: String, session: ActivitySession): Result<Unit> {
        return try {
            getUserSessionsCollection(userId).add(session).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    fun getTodaySteps(userId: String): Flow<Int> = flow {
        // ... Firestore logic to listen for step updates and emit them ...
        // For now, emit a placeholder:
        emit(0)
    }

    // --- 5b: Read data (history) ---
    fun getAllSessions(userId: String): Flow<List<ActivitySession>> = flow {
        // ... Firestore logic to listen for all sessions and emit them ...
        emit(emptyList())
    }
}