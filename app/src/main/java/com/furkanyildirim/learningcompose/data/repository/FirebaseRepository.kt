package com.furkanyildirim.learningcompose.data.repository

import com.furkanyildirim.learningcompose.data.model.Todo
import com.furkanyildirim.learningcompose.domain.repository.RemoteTodoRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor() : RemoteTodoRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val todosCollection = firestore.collection("todos")

    override val todos: Flow<List<Todo>> = callbackFlow {
        var snapshotRegistration: ListenerRegistration? = null

        fun attachForCurrentUser() {
            snapshotRegistration?.remove()
            val uid = auth.currentUser?.uid
            if (uid.isNullOrBlank()) {
                trySend(emptyList())
                return
            }

            snapshotRegistration = todosCollection
                .whereEqualTo("ownerId", uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        android.util.Log.e("Firebase", "Listen failed", error)
                        return@addSnapshotListener
                    }

                    val todoList = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject<TodoFirebase>()?.toTodo(doc.id)
                    } ?: emptyList()

                    trySend(todoList)
                }
        }

        val authStateListener = FirebaseAuth.AuthStateListener {
            attachForCurrentUser()
        }
        auth.addAuthStateListener(authStateListener)
        attachForCurrentUser()

        awaitClose {
            snapshotRegistration?.remove()
            auth.removeAuthStateListener(authStateListener)
        }
    }

    override suspend fun addTodo(todo: Todo): String {
        ensureAuthenticated()
        val uid = auth.currentUser?.uid ?: return ""
        val todoFirebase = TodoFirebase.fromTodo(todo)
        val docRef = todosCollection.add(todoFirebase.copy(ownerId = uid)).await()
        return docRef.id
    }

    override suspend fun updateTodo(todo: Todo) {
        if (todo.firebaseId.isBlank()) return
        ensureAuthenticated()
        val uid = auth.currentUser?.uid ?: return
        val todoFirebase = TodoFirebase.fromTodo(todo)
        todosCollection.document(todo.firebaseId).set(todoFirebase.copy(ownerId = uid)).await()
    }

    override suspend fun deleteTodo(firebaseId: String) {
        todosCollection.document(firebaseId).delete().await()
    }

    override suspend fun ensureAuthenticated() {
        if (auth.currentUser != null) return
        runCatching {
            auth.signInAnonymously().await()
        }.onFailure { throwable ->
            // If Auth is not configured (e.g. CONFIGURATION_NOT_FOUND), continue without auth.
            val code = (throwable as? FirebaseAuthException)?.errorCode ?: "UNKNOWN"
            android.util.Log.w("FirebaseAuth", "Anonymous auth skipped: $code")
        }
    }
}

// Firebase için data class (Room entity'den ayrı)
data class TodoFirebase(
    val title: String = "",
    val completed: Boolean = false,  // isCompleted -> completed
    val dueDate: Long? = null,
    val priority: String = "MEDIUM",
    val category: String = "OTHER",
    val project: String = "",
    val tags: String = "",
    val repeatIntervalDays: Int = 0,
    val repeatRule: String = "NONE",
    val pinned: Boolean = false,
    val updatedAt: Long = 0L,
    val ownerId: String = ""
) {
    fun toTodo(firebaseId: String) = Todo(
        id = 0,
        title = title,
        isCompleted = completed,
        dueDate = dueDate,
        priority = priority,
        category = category,
        project = project,
        tags = tags,
        repeatIntervalDays = repeatIntervalDays,
        repeatRule = repeatRule,
        isPinned = pinned,
        updatedAt = updatedAt,
        firebaseId = firebaseId
    )

    companion object {
        fun fromTodo(todo: Todo) = TodoFirebase(
            title = todo.title,
            completed = todo.isCompleted,
            dueDate = todo.dueDate,
            priority = todo.priority,
            category = todo.category,
            project = todo.project,
            tags = todo.tags,
            repeatIntervalDays = todo.repeatIntervalDays,
            repeatRule = todo.repeatRule,
            pinned = todo.isPinned,
            updatedAt = todo.updatedAt
        )
    }
}
