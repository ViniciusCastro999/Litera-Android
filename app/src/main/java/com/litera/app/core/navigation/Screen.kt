package com.litera.app.core.navigation

import java.net.URLDecoder
import java.net.URLEncoder

/**
 * All navigable destinations. Using plain string routes (rather than the
 * newer type-safe Nav Compose serialization API) keeps this file dependable
 * across Navigation Compose versions.
 */
sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object SignUp : Screen("sign_up")
    data object ForgotPassword : Screen("forgot_password")

    data object QuizIntro : Screen("quiz_intro")
    data object QuizCategories : Screen("quiz_categories")

    data object Home : Screen("home")
    data object Explore : Screen("explore")
    data object Shelf : Screen("shelf")
    data object Community : Screen("community")
    data object Profile : Screen("profile")

    data object CategoryBooks : Screen("category_books/{category}") {
        const val ARG_CATEGORY = "category"
        fun createRoute(category: String) = "category_books/${category.urlEncode()}"
        fun decodeCategory(raw: String) = raw.urlDecode()
    }

    data object BookDetail : Screen("book_detail/{volumeId}") {
        const val ARG_VOLUME_ID = "volumeId"
        fun createRoute(volumeId: String) = "book_detail/${volumeId.urlEncode()}"
    }

    data object AuthorDetail : Screen("author_detail/{authorName}") {
        const val ARG_AUTHOR_NAME = "authorName"
        fun createRoute(authorName: String) = "author_detail/${authorName.urlEncode()}"
    }

    data object Notes : Screen("notes/{volumeId}") {
        const val ARG_VOLUME_ID = "volumeId"
        fun createRoute(volumeId: String) = "notes/${volumeId.urlEncode()}"
    }

    // Reading pace ("Ritmo de leitura") flow — measure how fast the user
    // reads by timing one minute, then asking for the start/end page.
    data object ReadingPaceIntro : Screen("reading_pace_intro")
    data object ReadingPaceTimer : Screen("reading_pace_timer")
    data object ReadingPaceCalculate : Screen("reading_pace_calculate")
    data object ReadingPaceResult : Screen("reading_pace_result")

    // Focus mode (Pomodoro-style "Modo foco").
    data object FocusIntro : Screen("focus_intro")
    data object FocusSession : Screen("focus_session")
    data object FocusSettings : Screen("focus_settings")

    data object ReadingGoals : Screen("reading_goals")
    data object ReadingProgressDashboard : Screen("reading_progress_dashboard")

    data object ClubDetail : Screen("club_detail/{clubId}") {
        const val ARG_CLUB_ID = "clubId"
        fun createRoute(clubId: Long) = "club_detail/$clubId"
    }

    data object PostComposer : Screen("post_composer")

    data object PostDetail : Screen("post_detail/{postId}") {
        const val ARG_POST_ID = "postId"
        fun createRoute(postId: Long) = "post_detail/$postId"
    }

    companion object {
        /**
         * Destinations that show the bottom navigation bar. `by lazy`
         * deliberately defers evaluation past `Screen`'s own static
         * initialization — eagerly reading sibling `data object` singletons
         * (Home.route, etc.) here caused a JVM class-init ordering crash
         * (NullPointerException on Screen$Home.getRoute()) once the sealed
         * class grew enough nested types.
         */
        val bottomBarRoutes: Set<String> by lazy {
            setOf(Home.route, Explore.route, Shelf.route, Community.route, Profile.route)
        }
    }
}

private fun String.urlEncode(): String = URLEncoder.encode(this, "UTF-8")
private fun String.urlDecode(): String = URLDecoder.decode(this, "UTF-8")
