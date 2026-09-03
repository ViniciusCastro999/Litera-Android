package com.litera.app.presentation.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.litera.app.core.theme.DaisyBush50
import com.litera.app.core.theme.DaisyBush500
import com.litera.app.core.theme.PurplePrimaryDark
import com.litera.app.domain.model.Club
import com.litera.app.domain.model.Post
import com.litera.app.presentation.components.BookCoverImage
import com.litera.app.presentation.components.LoadingState
import com.litera.app.presentation.components.icons.PhosphorIcons

// "Entrar" badge color from the LiteraUX mock (orange) — not part of the
// shared theme tokens, so it's kept local to the community feature.
private val ClubJoinOrange = Color(0xFFE2691D)
private val ClubMemberGreen = Color(0xFF1E8E3E)

@Composable
fun CommunityScreen(
    onComposePost: () -> Unit,
    onPostClick: (Long) -> Unit,
    onClubClick: (Long) -> Unit,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CommunityHeader(
            selectedTab = uiState.tab,
            onTabSelected = viewModel::selectTab
        )

        when (uiState.tab) {
            CommunityTab.Amigos -> AmigosTabContent(
                posts = uiState.posts,
                isLoading = uiState.isLoading,
                onComposePost = onComposePost,
                onPostClick = onPostClick,
                onLikeClick = viewModel::toggleLike
            )
            CommunityTab.Clubes -> ClubesTabContent(
                clubs = uiState.clubs,
                onClubClick = onClubClick,
                onToggleMembership = viewModel::toggleClubMembership
            )
        }
    }
}

@Composable
private fun CommunityHeader(
    selectedTab: CommunityTab,
    onTabSelected: (CommunityTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PurplePrimaryDark)
    ) {
        CommunityTabItem(
            label = "Amigos",
            selected = selectedTab == CommunityTab.Amigos,
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected(CommunityTab.Amigos) }
        )
        CommunityTabItem(
            label = "Clubes Literários",
            selected = selectedTab == CommunityTab.Clubes,
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected(CommunityTab.Clubes) }
        )
    }
}

@Composable
private fun CommunityTabItem(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(if (selected) Color.White else Color.Transparent)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) PurplePrimaryDark else Color.White,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun AmigosTabContent(
    posts: List<Post>,
    isLoading: Boolean,
    onComposePost: () -> Unit,
    onPostClick: (Long) -> Unit,
    onLikeClick: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ComposePostRow(onClick = onComposePost)

        when {
            isLoading -> LoadingState()
            posts.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhuma publicação ainda. Seja o primeiro a compartilhar!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(32.dp)
                )
            }
            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(posts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        onLikeClick = { onLikeClick(post.id) },
                        onClick = { onPostClick(post.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ComposePostRow(onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(DaisyBush50)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = "Publicar algo...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onClick) {
            Icon(
                imageVector = PhosphorIcons.PlusCircle,
                contentDescription = "Nova publicação",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Shared post card rendering, reused by [PostDetailScreen] — kept
 * package-visible (no `private`) so it doesn't need to be duplicated.
 */
@Composable
fun PostCard(
    post: Post,
    onLikeClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarCircle(photoUrl = post.authorPhotoUrl, fallbackIcon = PhosphorIcons.UserCircle, size = 36.dp)
            Spacer(Modifier.width(10.dp))
            Text(
                text = post.authorName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(text = post.text, style = MaterialTheme.typography.bodyMedium)

        if (post.imageUrl != null) {
            Spacer(Modifier.height(10.dp))
            AsyncImage(
                model = post.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (post.likedByMe) PhosphorIcons.HeartFill else PhosphorIcons.Heart,
                contentDescription = "Curtir",
                tint = if (post.likedByMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onLikeClick)
            )
            Icon(
                imageVector = PhosphorIcons.Comment,
                contentDescription = "Comentários",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onClick)
            )
            Icon(
                imageVector = PhosphorIcons.Share,
                contentDescription = "Compartilhar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }

        if (post.likeCount > 0 || post.commentCount > 0) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = "${post.likeCount} curtidas · ${post.commentCount} comentários",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    }
}

@Composable
private fun ClubesTabContent(
    clubs: List<Club>,
    onClubClick: (Long) -> Unit,
    onToggleMembership: (Club) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filteredClubs = remember(clubs, query) {
        if (query.isBlank()) clubs else clubs.filter { it.handle.contains(query, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ClubSearchRow(query = query, onQueryChange = { query = it })

        if (filteredClubs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Nenhum clube encontrado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredClubs, key = { it.id }) { club ->
                    ClubCard(
                        club = club,
                        onClick = { onClubClick(club.id) },
                        onToggleMembership = { onToggleMembership(club) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ClubSearchRow(query: String, onQueryChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PurplePrimaryDark)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(DaisyBush500)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Icon(
                imageVector = PhosphorIcons.MagnifyingGlass,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Procurar clube...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    cursorBrush = SolidColor(Color.White),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ClubCard(
    club: Club,
    onClick: () -> Unit,
    onToggleMembership: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            AvatarCircle(photoUrl = club.avatarUrl, fallbackIcon = PhosphorIcons.Community, size = 44.dp)
            Spacer(Modifier.width(10.dp))
            Text(
                text = "@${club.handle}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            ClubMembershipBadge(isMember = club.isMember, onClick = onToggleMembership)
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = PhosphorIcons.Community,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "${club.memberCount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.weight(1f))

            if (club.currentBookTitle != null) {
                BookCoverImage(
                    thumbnailUrl = club.currentBookThumbnailUrl,
                    modifier = Modifier.width(32.dp).height(44.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = club.currentBookTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(140.dp)
                )
            } else {
                Text(
                    text = "Sem leitura atual",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ClubMembershipBadge(
    isMember: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = if (isMember) "Membro" else "Entrar",
        color = Color.White,
        fontWeight = FontWeight.SemiBold,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(if (isMember) ClubMemberGreen else ClubJoinOrange)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun AvatarCircle(
    photoUrl: String?,
    fallbackIcon: ImageVector,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (photoUrl.isNullOrBlank()) {
            Icon(
                imageVector = fallbackIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(size * 0.6f)
            )
        } else {
            AsyncImage(
                model = photoUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
            )
        }
    }
}
