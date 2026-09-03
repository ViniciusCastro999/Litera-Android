package com.litera.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LiteraApplication : Application(), ImageLoaderFactory {

    // Book covers (Explore, Home, Shelf, Book Detail) are all loaded through
    // this single cached loader instead of Coil's per-request defaults, so
    // a cover fetched once is instant on every later screen/revisit — the
    // Google Books CDN round-trip only ever happens once per cover per app
    // install.
    override fun newImageLoader(): ImageLoader = ImageLoader.Builder(this)
        .crossfade(true)
        .memoryCache {
            MemoryCache.Builder(this)
                .maxSizePercent(0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(cacheDir.resolve("image_cache"))
                .maxSizePercent(0.03)
                .build()
        }
        .build()
}
