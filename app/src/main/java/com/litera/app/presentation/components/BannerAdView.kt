package com.litera.app.presentation.components

import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.litera.app.BuildConfig

/**
 * A full-width AdMob banner. Uses the "inline adaptive" format (as opposed
 * to the short anchored adaptive banner, ~50-90dp tall) so it can request a
 * height close to [maxHeightDp] — matching the footprint of the quote hero
 * card it replaces on Home, instead of a thin strip.
 */
@Composable
fun BannerAdView(modifier: Modifier = Modifier, maxHeightDp: Int = 250) {
    val context = LocalContext.current
    val screenWidthDp = LocalConfiguration.current.screenWidthDp

    val adView = remember {
        AdView(context).apply {
            adUnitId = BuildConfig.ADMOB_BANNER_AD_UNIT_ID
            setAdSize(AdSize.getInlineAdaptiveBannerAdSize(screenWidthDp, maxHeightDp))
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            // A served creative narrower than the requested slot gets
            // centered and letterboxed — transparent instead of the
            // default opaque black lets the surrounding card color show
            // through there instead of harsh black bars.
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            loadAd(AdRequest.Builder().build())
        }
    }

    DisposableEffect(adView) {
        onDispose { adView.destroy() }
    }

    AndroidView(
        factory = { adView },
        modifier = modifier.fillMaxWidth()
    )
}
