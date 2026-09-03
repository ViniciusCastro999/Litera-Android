package com.litera.app.data.remote

import com.litera.app.data.remote.dto.VolumeDto
import com.litera.app.data.remote.dto.VolumesResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Thin wrapper around the public Google Books API (no auth required for
 * search at low volume; add an API key via local.properties -> booksApiKey
 * to raise the quota). langRestrict + country bias results towards
 * Portuguese-language, Brazil-available editions, which is what the
 * "Literatura Brasileira" sections in the design need.
 */
interface GoogleBooksApiService {

    @GET("books/v1/volumes")
    suspend fun searchVolumes(
        @Query("q") query: String,
        @Query("langRestrict") langRestrict: String,
        @Query("country") country: String,
        @Query("maxResults") maxResults: Int,
        @Query("key") apiKey: String?
    ): VolumesResponseDto

    @GET("books/v1/volumes/{volumeId}")
    suspend fun getVolume(
        @Path("volumeId") volumeId: String,
        @Query("country") country: String,
        @Query("key") apiKey: String?
    ): VolumeDto
}
