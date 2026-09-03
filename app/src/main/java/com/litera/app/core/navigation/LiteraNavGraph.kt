package com.litera.app.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.litera.app.presentation.AppViewModel
import com.litera.app.presentation.StartDestination
import com.litera.app.presentation.auth.ForgotPasswordScreen
import com.litera.app.presentation.auth.LoginScreen
import com.litera.app.presentation.auth.SignUpScreen
import com.litera.app.presentation.author.AuthorDetailScreen
import com.litera.app.presentation.bookdetail.BookDetailScreen
import com.litera.app.presentation.community.ClubDetailScreen
import com.litera.app.presentation.community.CommunityScreen
import com.litera.app.presentation.community.PostComposerScreen
import com.litera.app.presentation.community.PostDetailScreen
import com.litera.app.presentation.components.LiteraBottomBar
import com.litera.app.presentation.components.LoadingState
import com.litera.app.presentation.explore.CategoryBooksScreen
import com.litera.app.presentation.explore.ExploreScreen
import com.litera.app.presentation.focus.FocusIntroScreen
import com.litera.app.presentation.focus.FocusSessionScreen
import com.litera.app.presentation.focus.FocusSettingsScreen
import com.litera.app.presentation.goals.ReadingGoalsScreen
import com.litera.app.presentation.home.HomeScreen
import com.litera.app.presentation.notes.NotesScreen
import com.litera.app.presentation.onboarding.OnboardingScreen
import com.litera.app.presentation.profile.ProfileScreen
import com.litera.app.presentation.progress.ReadingProgressScreen
import com.litera.app.presentation.quiz.CategoryQuizScreen
import com.litera.app.presentation.quiz.QuizIntroScreen
import com.litera.app.presentation.readingpace.ReadingPaceCalculateScreen
import com.litera.app.presentation.readingpace.ReadingPaceIntroScreen
import com.litera.app.presentation.readingpace.ReadingPaceResultScreen
import com.litera.app.presentation.readingpace.ReadingPaceTimerScreen
import com.litera.app.presentation.readingpace.ReadingPaceViewModel
import com.litera.app.presentation.shelf.ShelfScreen

private const val READING_PACE_FLOW_ROUTE = "reading_pace_flow"

@Composable
fun LiteraNavGraph(
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel = hiltViewModel()
) {
    val startDestination by appViewModel.startDestination.collectAsStateWithLifecycle()

    if (startDestination is StartDestination.Loading) {
        LoadingState()
        return
    }

    val startRoute = when (startDestination) {
        StartDestination.Onboarding -> Screen.Onboarding.route
        StartDestination.Auth -> Screen.Login.route
        StartDestination.Quiz -> Screen.QuizIntro.route
        else -> Screen.Home.route
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in Screen.bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                LiteraBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinished = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { navController.navigateAfterAuth(appViewModel.quizCompleted.value) },
                    onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                    onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
                )
            }

            composable(Screen.SignUp.route) {
                SignUpScreen(
                    onSignUpSuccess = { navController.navigateAfterAuth(appViewModel.quizCompleted.value) },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.QuizIntro.route) {
                QuizIntroScreen(
                    onSkip = { navController.navigateToHomeClearingAuthAndQuiz() },
                    onStart = { navController.navigate(Screen.QuizCategories.route) }
                )
            }

            composable(Screen.QuizCategories.route) {
                CategoryQuizScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.navigateToHomeClearingAuthAndQuiz() }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onBookClick = { volumeId -> navController.navigate(Screen.BookDetail.createRoute(volumeId)) }
                )
            }

            composable(Screen.Explore.route) {
                ExploreScreen(
                    onBookClick = { volumeId -> navController.navigate(Screen.BookDetail.createRoute(volumeId)) },
                    onCategoryClick = { category -> navController.navigate(Screen.CategoryBooks.createRoute(category)) }
                )
            }

            composable(Screen.CategoryBooks.route) {
                CategoryBooksScreen(
                    onBack = { navController.popBackStack() },
                    onBookClick = { volumeId -> navController.navigate(Screen.BookDetail.createRoute(volumeId)) }
                )
            }

            composable(Screen.BookDetail.route) {
                BookDetailScreen(
                    onBack = { navController.popBackStack() },
                    onBookClick = { volumeId -> navController.navigate(Screen.BookDetail.createRoute(volumeId)) },
                    onAuthorClick = { authorName -> navController.navigate(Screen.AuthorDetail.createRoute(authorName)) },
                    onNotesClick = { volumeId -> navController.navigate(Screen.Notes.createRoute(volumeId)) },
                    onFocusModeClick = { navController.navigate(Screen.FocusIntro.route) }
                )
            }

            composable(
                Screen.AuthorDetail.route,
                arguments = listOf(navArgument(Screen.AuthorDetail.ARG_AUTHOR_NAME) { type = NavType.StringType })
            ) {
                AuthorDetailScreen(
                    onBack = { navController.popBackStack() },
                    onBookClick = { volumeId -> navController.navigate(Screen.BookDetail.createRoute(volumeId)) }
                )
            }

            composable(
                Screen.Notes.route,
                arguments = listOf(navArgument(Screen.Notes.ARG_VOLUME_ID) { type = NavType.StringType })
            ) {
                NotesScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Shelf.route) {
                ShelfScreen(
                    onBookClick = { volumeId -> navController.navigate(Screen.BookDetail.createRoute(volumeId)) }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onSignedOut = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToGoals = { navController.navigate(Screen.ReadingGoals.route) },
                    onNavigateToProgress = { navController.navigate(Screen.ReadingProgressDashboard.route) },
                    onNavigateToReadingPace = { navController.navigate(READING_PACE_FLOW_ROUTE) }
                )
            }

            composable(Screen.ReadingGoals.route) {
                ReadingGoalsScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.ReadingProgressDashboard.route) {
                ReadingProgressScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToGoals = { navController.navigate(Screen.ReadingGoals.route) }
                )
            }

            // Focus mode ("Modo foco") — each screen reads/writes persisted
            // settings itself, no shared in-memory state needed between steps.
            composable(Screen.FocusIntro.route) {
                FocusIntroScreen(
                    onStart = {
                        navController.navigate(Screen.FocusSession.route) {
                            popUpTo(Screen.FocusIntro.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.FocusSession.route) {
                FocusSessionScreen(
                    onBack = { navController.popBackStack() },
                    onSettingsClick = { navController.navigate(Screen.FocusSettings.route) }
                )
            }

            composable(Screen.FocusSettings.route) {
                FocusSettingsScreen(onBack = { navController.popBackStack() })
            }

            // Reading pace ("Ritmo de leitura") — a nested graph so all four
            // steps share one ReadingPaceViewModel scoped to this sub-graph's
            // own back-stack entry (the stopwatch reading + page inputs are
            // ephemeral and must survive screen-to-screen navigation without
            // being persisted anywhere).
            navigation(
                startDestination = Screen.ReadingPaceIntro.route,
                route = READING_PACE_FLOW_ROUTE
            ) {
                composable(Screen.ReadingPaceIntro.route) {
                    ReadingPaceIntroScreen(
                        onSkip = { navController.popBackStack(READING_PACE_FLOW_ROUTE, inclusive = true) },
                        onStart = { navController.navigate(Screen.ReadingPaceTimer.route) }
                    )
                }
                composable(Screen.ReadingPaceTimer.route) { entry ->
                    val parentEntry = remember(entry) { navController.getBackStackEntry(READING_PACE_FLOW_ROUTE) }
                    ReadingPaceTimerScreen(
                        onBack = { navController.popBackStack() },
                        onNext = { navController.navigate(Screen.ReadingPaceCalculate.route) },
                        viewModel = hiltViewModel<ReadingPaceViewModel>(parentEntry)
                    )
                }
                composable(Screen.ReadingPaceCalculate.route) { entry ->
                    val parentEntry = remember(entry) { navController.getBackStackEntry(READING_PACE_FLOW_ROUTE) }
                    ReadingPaceCalculateScreen(
                        onBack = { navController.popBackStack() },
                        onNext = { navController.navigate(Screen.ReadingPaceResult.route) },
                        viewModel = hiltViewModel<ReadingPaceViewModel>(parentEntry)
                    )
                }
                composable(Screen.ReadingPaceResult.route) { entry ->
                    val parentEntry = remember(entry) { navController.getBackStackEntry(READING_PACE_FLOW_ROUTE) }
                    ReadingPaceResultScreen(
                        onBack = { navController.popBackStack() },
                        onFinish = { navController.popBackStack(READING_PACE_FLOW_ROUTE, inclusive = true) },
                        onContinueReading = { navController.popBackStack(READING_PACE_FLOW_ROUTE, inclusive = true) },
                        viewModel = hiltViewModel<ReadingPaceViewModel>(parentEntry)
                    )
                }
            }

            // Community (local-only, single-device — see plan notes).
            composable(Screen.Community.route) {
                CommunityScreen(
                    onComposePost = { navController.navigate(Screen.PostComposer.route) },
                    onPostClick = { postId -> navController.navigate(Screen.PostDetail.createRoute(postId)) },
                    onClubClick = { clubId -> navController.navigate(Screen.ClubDetail.createRoute(clubId)) }
                )
            }

            composable(Screen.PostComposer.route) {
                PostComposerScreen(
                    onBack = { navController.popBackStack() },
                    onPosted = { navController.popBackStack() }
                )
            }

            composable(
                Screen.PostDetail.route,
                arguments = listOf(navArgument(Screen.PostDetail.ARG_POST_ID) { type = NavType.LongType })
            ) {
                PostDetailScreen(onBack = { navController.popBackStack() })
            }

            composable(
                Screen.ClubDetail.route,
                arguments = listOf(navArgument(Screen.ClubDetail.ARG_CLUB_ID) { type = NavType.LongType })
            ) {
                ClubDetailScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

private fun NavHostController.navigateAfterAuth(quizCompleted: Boolean) {
    val destination = if (quizCompleted) Screen.Home.route else Screen.QuizIntro.route
    navigate(destination) {
        popUpTo(Screen.Login.route) { inclusive = true }
    }
}

private fun NavHostController.navigateToHomeClearingAuthAndQuiz() {
    navigate(Screen.Home.route) {
        popUpTo(Screen.QuizIntro.route) { inclusive = true }
    }
}
