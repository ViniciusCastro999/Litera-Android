package com.litera.app.data.remote

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withTimeout

/** Every per-user collection (shelf, goals, notes, ...) lives under this path. */
fun FirebaseFirestore.userCollection(uid: String, name: String): CollectionReference =
    collection("users").document(uid).collection(name)

/** The single settings doc for a per-user preference bundle (see [name]). */
fun FirebaseFirestore.userSettingsDoc(uid: String, name: String): DocumentReference =
    collection("users").document(uid).collection("settings").document(name)

/**
 * Bridges a Firestore realtime listener into a cold Flow. Listener errors
 * (e.g. security rules not published yet, momentary offline) are recorded
 * to Crashlytics as non-fatals instead of closing/crashing the Flow — a
 * repository's `observeX()` Flow is expected to never throw, same
 * contract Room's Flow-returning DAOs already had.
 */
fun Query.snapshotsFlow(): Flow<QuerySnapshot> = callbackFlow {
    val registration = addSnapshotListener { snapshot, error ->
        if (error != null) {
            FirebaseCrashlytics.getInstance().recordException(error)
        } else if (snapshot != null) {
            trySend(snapshot)
        }
    }
    awaitClose { registration.remove() }
}

fun DocumentReference.snapshotsFlow(): Flow<DocumentSnapshot> = callbackFlow {
    val registration = addSnapshotListener { snapshot, error ->
        if (error != null) {
            FirebaseCrashlytics.getInstance().recordException(error)
        } else if (snapshot != null) {
            trySend(snapshot)
        }
    }
    awaitClose { registration.remove() }
}

/**
 * Runs a Firestore write best-effort: if it's unreachable/not provisioned
 * yet, the exception is recorded to Crashlytics instead of crashing the
 * screen that triggered it (these repository actions are fire-and-forget
 * from their ViewModels, with no error channel to surface failures today).
 *
 * Wrapped in [withTimeout]: some failures (e.g. the Firestore API not being
 * enabled yet for the project) make the underlying write stream retry
 * forever instead of failing the Task, which would otherwise hang whatever
 * "loading" state the caller is showing indefinitely.
 */
suspend inline fun safeWrite(crossinline block: suspend () -> Unit) {
    try {
        withTimeout(15_000L) { block() }
    } catch (e: TimeoutCancellationException) {
        FirebaseCrashlytics.getInstance().recordException(e)
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
    }
}
