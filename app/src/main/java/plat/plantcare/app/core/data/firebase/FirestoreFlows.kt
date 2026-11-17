package plat.plantcare.app.core.data.firebase

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/** Observe Firestore snapshots while removing the listener when its collector stops. */
fun <T> DocumentReference.observeSnapshots(map: (DocumentSnapshot) -> T): Flow<T> = callbackFlow {
    val listener = addSnapshotListener { snapshot, error ->
        if (error != null) close(error)
        else if (snapshot != null) {
            try { trySend(map(snapshot)) } catch (e: Exception) { close(e) }
        }
    }
    awaitClose { listener.remove() }
}

fun <T> Query.observeSnapshots(map: (QuerySnapshot) -> T): Flow<T> = callbackFlow {
    val listener = addSnapshotListener { snapshot, error ->
        if (error != null) close(error)
        else if (snapshot != null) {
            try { trySend(map(snapshot)) } catch (e: Exception) { close(e) }
        }
    }
    awaitClose { listener.remove() }
}
