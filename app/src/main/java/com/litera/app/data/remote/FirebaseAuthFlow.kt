package com.litera.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Emits the signed-in user's uid (or null) every time it changes, so
 * per-user Firestore repositories can switch which document/collection
 * they're listening to when the user signs in, signs out, or switches
 * accounts — instead of capturing a single uid at construction time.
 */
fun FirebaseAuth.currentUserIdFlow(): Flow<String?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth -> trySend(auth.currentUser?.uid) }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}.distinctUntilChanged()
