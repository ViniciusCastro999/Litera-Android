package com.litera.app.data.local

import com.litera.app.data.local.entity.ClubEntity
import com.litera.app.data.local.entity.PostEntity

/**
 * Fixture content matching the LiteraUX Figma "Amigos"/"Clubes literários"
 * mockups, inserted once when the local database is first created so the
 * community feed isn't empty on a fresh install.
 */
object SeedData {

    fun seedPosts(now: Long): List<PostEntity> = listOf(
        PostEntity(
            authorName = "@anasilva",
            authorPhotoUrl = null,
            text = "Definitivamente o melhor livro que li esse ano!",
            imageUrl = null,
            tags = "",
            likeCount = 12,
            likedByMe = false,
            createdAt = now - 3 * 60 * 60 * 1000
        ),
        PostEntity(
            authorName = "@chicof",
            authorPhotoUrl = null,
            text = "Viram que o trecho da semana é do \"Quarto de Despejo\"? Bora comentar o que acharam.",
            imageUrl = null,
            tags = "",
            likeCount = 5,
            likedByMe = false,
            createdAt = now - 6 * 60 * 60 * 1000
        )
    )

    fun seedClubs(): List<ClubEntity> = listOf(
        ClubEntity(
            handle = "LeitoresCanarinhos",
            avatarUrl = null,
            memberCount = 10,
            currentBookTitle = "Os quase completos",
            currentBookThumbnailUrl = null,
            description = "Clube de leitura focado em autores nacionais.",
            isMember = true
        ),
        ClubEntity(
            handle = "AmantesdeMisterios",
            avatarUrl = null,
            memberCount = 20,
            currentBookTitle = null,
            currentBookThumbnailUrl = null,
            description = "Para quem ama um bom suspense.",
            isMember = true
        ),
        ClubEntity(
            handle = "QueridoClassico",
            avatarUrl = null,
            memberCount = 20,
            currentBookTitle = "Memórias Póstumas de Brás Cubas",
            currentBookThumbnailUrl = null,
            description = "Para os fãs dos grandes clássicos da literatura brasileira e mundial.",
            isMember = false
        )
    )
}
