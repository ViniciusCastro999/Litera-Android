package com.litera.app.data.remote

import com.litera.app.data.remote.dto.WikipediaSummaryDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Wikipedia's public REST summary API (no key required) — used to source an
 * author bio for the "Sobre o autor" screen, since the Google Books API has
 * no bio field. Note: Retrofit @Path segments are URL-encoded automatically,
 * so a name like "Machado de Assis" is passed through as-is.
 */
interface WikipediaApiService {

    @GET("page/summary/{title}")
    suspend fun getSummary(@Path("title") title: String): WikipediaSummaryDto
}
