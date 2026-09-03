package com.litera.app.presentation.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.litera.app.BuildConfig

sealed class GoogleSignInResult {
    data class Success(val idToken: String) : GoogleSignInResult()

    /** No Web Client ID configured yet — see README "Login com Google". */
    data object NotConfigured : GoogleSignInResult()

    /** The user dismissed the system account picker. */
    data object Cancelled : GoogleSignInResult()

    /** No Google account is signed into this device. */
    data object NoAccountFound : GoogleSignInResult()
}

/**
 * Launches the system "Sign in with Google" bottom sheet (Credential
 * Manager) and resolves a Google ID token that AuthViewModel.signInWithGoogle
 * exchanges for a Firebase session — same flow for both Login and SignUp,
 * since Firebase creates the account automatically on first sign-in.
 */
suspend fun requestGoogleIdToken(context: Context): GoogleSignInResult {
    if (BuildConfig.GOOGLE_WEB_CLIENT_ID.isBlank()) {
        return GoogleSignInResult.NotConfigured
    }

    val option = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
        .build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(option)
        .build()

    return try {
        val response = CredentialManager.create(context).getCredential(context, request)
        val credential = response.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            GoogleSignInResult.Success(GoogleIdTokenCredential.createFrom(credential.data).idToken)
        } else {
            GoogleSignInResult.Cancelled
        }
    } catch (e: GetCredentialCancellationException) {
        GoogleSignInResult.Cancelled
    } catch (e: NoCredentialException) {
        GoogleSignInResult.NoAccountFound
    } catch (e: GetCredentialException) {
        GoogleSignInResult.Cancelled
    }
}
