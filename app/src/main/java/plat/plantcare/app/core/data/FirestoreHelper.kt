package plat.plantcare.app.core.data

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

inline fun <reified T : Any> Query.snapshotFlow(): Flow<List<T>> = callbackFlow {
    val registration = addSnapshotListener { snapshot, error ->
        if (error != null) {
            close(error)                       // the flow fails; the ViewModel's .catch handles it
            return@addSnapshotListener
        }
        if (snapshot != null) {
            trySend(snapshot.toObjects(T::class.java))
        }
    }
    awaitClose { registration.remove() }       // stops the listener when nobody collects
}

inline fun <reified T : Any> DocumentReference.documentFlow(): Flow<T?> = callbackFlow {
    val registration = addSnapshotListener { snapshot, error ->
        if (error != null) {
            close(error)
            return@addSnapshotListener
        }
        if (snapshot != null) {
            trySend(snapshot.toObject(T::class.java))
        }
    }
    awaitClose { registration.remove() }
}