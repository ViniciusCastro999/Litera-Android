package com.litera.app.core.common

object Constants {
    const val GOOGLE_BOOKS_BASE_URL = "https://www.googleapis.com/"
    const val WIKIPEDIA_BASE_URL = "https://pt.wikipedia.org/api/rest_v1/"

    // Google Books API's langRestrict only accepts an ISO 639-1 language code
    // (no regional variant), so "pt" is the closest match for Portuguese.
    // Pairing it with country=BR biases results/availability towards Brazil,
    // which is why every call also sends BOOKS_COUNTRY.
    const val BOOKS_LANGUAGE_RESTRICT = "pt"
    const val BOOKS_COUNTRY = "BR"

    const val DEFAULT_PAGE_SIZE = 20

    val DEFAULT_CATEGORIES = listOf(
        "Fantasia",
        "Ficção científica",
        "Poesia",
        "Biografia",
        "Romance",
        "Mistério",
        "Crime",
        "Terror",
        "Juvenil",
        "Clássicos",
        "Young Adult",
        "Não-ficção",
        "Autoajuda",
        "Comédia"
    )

    const val DATABASE_NAME = "litera_database"
    const val PREFERENCES_DATASTORE_NAME = "litera_preferences"

    const val MIN_ONBOARDING_CATEGORIES = 3
}
