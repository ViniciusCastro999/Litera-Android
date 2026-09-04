package com.litera.app.core.common

/**
 * Central switches for features that already exist in code but aren't
 * ready for real users yet. Flip one to `true` once the feature behind it
 * is actually ready to ship — no other code changes needed at call sites.
 */
object FeatureFlags {

    /**
     * Comunidade (feed "Amigos", posts, clubes) runs entirely on local
     * Room data seeded on first install — there's no real backend, so
     * nothing posted is ever shared between different users/devices. Hidden
     * from the bottom bar until it's backed by something real (Firestore
     * or similar).
     */
    const val COMMUNITY_ENABLED = false

    /**
     * "Escanear texto" / "Capturar página" on the Anotações screen — OCR
     * was never implemented, only manual text notes. The buttons render
     * disabled with an "em breve" affordance while this is false.
     */
    const val NOTES_OCR_ENABLED = false
}
