package com.litera.app.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.litera.app.R
import com.litera.app.core.theme.DaisyBush100
import com.litera.app.presentation.components.icons.PhosphorIcons
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val title: String,
    val body: String,
    val illustrationRes: Int
)

// Illustrations exported straight from the LiteraUX Figma Style Guide
// (drawable-nodpi/onboarding_illustration_1..4.png) — one per page, matching
// the design 1:1 instead of a placeholder icon.
private val pages = listOf(
    OnboardingPage(
        title = "Tire um tempo da sua rotina para ler e relaxar",
        body = "Desacelere! No dia a dia, reserve um tempinho só para você. Pegue aquele livro incrível, relaxe e se jogue nas páginas.",
        illustrationRes = R.drawable.onboarding_illustration_1
    ),
    OnboardingPage(
        title = "Organize sua biblioteca",
        body = "Sua Biblioteca, Seu Jeito! Organize e registre suas leituras. Tenha à mão suas próximas aventuras literárias.",
        illustrationRes = R.drawable.onboarding_illustration_2
    ),
    OnboardingPage(
        title = "Defina novas metas de leitura",
        body = "Desafie-se! Estabeleça metas de leitura e acompanhe seu progresso. A cada livro concluído, uma nova conquista!",
        illustrationRes = R.drawable.onboarding_illustration_3
    ),
    OnboardingPage(
        title = "Descubra mais livros nacionais",
        body = "Explore a Literatura Brasileira! Descubra e se apaixone por autores nacionais e nossa rica cultura.",
        illustrationRes = R.drawable.onboarding_illustration_4
    )
)

/** Bolds the emphatic lead-in phrase (up to the first "!") in an onboarding body, matching the Figma copy treatment. */
private fun boldLeadIn(body: String) = buildAnnotatedString {
    val exclamationIndex = body.indexOf('!')
    if (exclamationIndex == -1) {
        append(body)
    } else {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(body.substring(0, exclamationIndex + 1))
        }
        append(body.substring(exclamationIndex + 1))
    }
}

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    fun finish() = viewModel.completeOnboarding(onFinished)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DaisyBush100)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { finish() }) {
                Text(
                    "Pular",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                // Real illustration exported from the LiteraUX Figma file,
                // placed directly on the page background (no card/frame
                // around it, matching the Figma mockup exactly).
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = page.illustrationRes),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = boldLeadIn(page.body),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pages.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(if (index == pageIndex) 24.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == pageIndex) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    }
                                )
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(start = 24.dp, end = 24.dp, bottom = 32.dp)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Figma only shows a back button once there's somewhere to go back to.
            if (pagerState.currentPage > 0) {
                FilledIconButton(
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    androidx.compose.material3.Icon(PhosphorIcons.ArrowLeft, contentDescription = "Voltar")
                }
            } else {
                Spacer(Modifier.width(48.dp))
            }

            FilledIconButton(
                onClick = {
                    if (pagerState.currentPage < pages.lastIndex) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        finish()
                    }
                },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                androidx.compose.material3.Icon(PhosphorIcons.ArrowRight, contentDescription = "Avançar")
            }
        }
    }
}
