package com.litera.app.data.remote

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Minimal bridge from a Play Services [Task] to a suspend function, so we
 * don't need to pull in the kotlinx-coroutines-play-services artifact just
 * for Firebase Auth calls.
 */
suspend fun <T> Task<T>.awaitResult(): T = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(task.result)
        } else {
            val exception = task.exception ?: RuntimeException("Tarefa falhou sem uma exceção.")
            continuation.resumeWithException(exception)
        }
    }
}
