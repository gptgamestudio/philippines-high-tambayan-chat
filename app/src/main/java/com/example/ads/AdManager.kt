package com.example.ads

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AdSystemState(
    val isBannerEnabled: Boolean = true,
    val isInterstitialEnabled: Boolean = true,
    val isRewardedEnabled: Boolean = false,
    val maxDailyRewardedAds: Int = 10,
    val adsWatchedToday: Int = 0,
    val rewardPerAd: Int = 10,
    val millisUntilReset: Long = 0L,
    val totalAdsWatchedAllTime: Int = 0,
    val isAdShowing: Boolean = false,
    val adTypeShowing: String = "", // "INTERSTITIAL"
    val adCountdownSeconds: Int = 0,
    val lastAdMessage: String = "",
    val safeKidAdsMode: Boolean = false
)

class AdManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("ad_prefs", Context.MODE_PRIVATE)
    private val _state = MutableStateFlow(AdSystemState())
    val state: StateFlow<AdSystemState> = _state.asStateFlow()

    private var interstitialAd: InterstitialAd? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var countdownRunnable: Runnable? = null

    companion object {
        private const val TAG = "AdManager"
        // Official Google AdMob sample test ad unit IDs
        const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
        // Production AdMob IDs from user configuration
        const val PROD_INTERSTITIAL_ID = "ca-app-pub-9786472984542330/7506482083"
        const val PROD_BANNER_ID = "ca-app-pub-9786472984542330/4596337589"
        private const val TWENTY_FOUR_HOURS_MS = 24 * 60 * 60 * 1000L

        @Volatile
        private var INSTANCE: AdManager? = null

        fun getInstance(context: Context): AdManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AdManager(context.applicationContext).also { INSTANCE = it }
            }
        }

        fun isEmulator(): Boolean {
            return (Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.startsWith("unknown")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")
                    || Build.HARDWARE.contains("goldfish")
                    || Build.HARDWARE.contains("ranchu")
                    || Build.PRODUCT.contains("sdk_gphone")
                    || "google_sdk" == Build.PRODUCT)
        }
    }

    private var boundActivity: Activity? = null

    fun bindActivity(activity: Activity) {
        this.boundActivity = activity
    }

    fun showRewardedAd(onRewardEarned: (Int) -> Unit) {
        showRewardedAd(boundActivity, onRewardEarned)
    }

    fun showInterstitial(onDismiss: () -> Unit = {}) {
        showInterstitial(boundActivity, onDismiss)
    }

    fun toggleBanner(enabled: Boolean) = setBannerEnabled(enabled)
    fun toggleRewarded(enabled: Boolean) = setRewardedEnabled(enabled)
    fun resetDailyLimit() = resetDailyCounterNow()

    init {
        if (!isEmulator()) {
            try {
                // Apply COPPA & Google Play Families policy child-directed request configuration
                val requestConfig = RequestConfiguration.Builder()
                    .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                    .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)
                    .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                    .build()
                MobileAds.setRequestConfiguration(requestConfig)

                MobileAds.initialize(context) { status ->
                    Log.d(TAG, "AdMob MobileAds initialized: ${status.adapterStatusMap}")
                }
                loadInterstitial()
            } catch (e: Exception) {
                Log.e(TAG, "AdMob initialization warning: ${e.message}")
            }
        } else {
            Log.d(TAG, "Android Emulator detected: Safe Native Sponsor mode active.")
        }
        loadStateFromPrefs()
        checkAndResetDailyCycle()
    }

    private fun loadStateFromPrefs() {
        val banner = prefs.getBoolean("banner_enabled", true)
        val inter = prefs.getBoolean("interstitial_enabled", true)
        val reward = prefs.getBoolean("rewarded_enabled", false)
        val maxDaily = prefs.getInt("max_daily", 10)
        val watched = prefs.getInt("watched_today", 0)
        val rewardStars = prefs.getInt("reward_per_ad", 10)
        val allTime = prefs.getInt("all_time_watched", 0)
        val safeMode = prefs.getBoolean("safe_kid_ads_mode", false)

        _state.update {
            it.copy(
                isBannerEnabled = banner,
                isInterstitialEnabled = inter,
                isRewardedEnabled = reward,
                maxDailyRewardedAds = maxDaily,
                adsWatchedToday = watched,
                rewardPerAd = rewardStars,
                totalAdsWatchedAllTime = allTime,
                safeKidAdsMode = safeMode
            )
        }
    }

    fun checkAndResetDailyCycle() {
        val now = System.currentTimeMillis()
        val lastReset = prefs.getLong("last_reset_time", now)
        val elapsed = now - lastReset

        if (elapsed >= TWENTY_FOUR_HOURS_MS || !prefs.contains("last_reset_time")) {
            prefs.edit()
                .putLong("last_reset_time", now)
                .putInt("watched_today", 0)
                .apply()
            _state.update {
                it.copy(
                    adsWatchedToday = 0,
                    millisUntilReset = TWENTY_FOUR_HOURS_MS
                )
            }
        } else {
            val remaining = TWENTY_FOUR_HOURS_MS - elapsed
            _state.update { it.copy(millisUntilReset = remaining) }
        }
    }

    fun loadInterstitial() {
        if (!_state.value.isInterstitialEnabled || isEmulator()) return
        try {
            val adRequest = AdRequest.Builder().build()
            val adUnitId = if (isEmulator()) TEST_INTERSTITIAL_ID else PROD_INTERSTITIAL_ID
            InterstitialAd.load(
                context,
                adUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        Log.d(TAG, "Interstitial Ad Loaded successfully")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        interstitialAd = null
                        Log.w(TAG, "Interstitial load error: ${loadAdError.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error loading interstitial: ${e.message}")
            interstitialAd = null
        }
    }

    /**
     * Show interstitial ad break every 5 chats.
     * On emulators or when bufferpool fails, automatically runs the Compose KidAdDialog
     * avoiding MESA rendernode or Codec2 buffer crashes.
     */
    fun showInterstitial(activity: Activity?, onDismiss: () -> Unit) {
        if (!_state.value.isInterstitialEnabled) {
            onDismiss()
            return
        }

        // On emulators or safe mode, use smooth, zero-hardware-crash Compose ad break dialog
        if (isEmulator() || _state.value.safeKidAdsMode || activity == null || activity.isFinishing || activity.isDestroyed) {
            startKidInteractiveAd("INTERSTITIAL", 3) {
                onDismiss()
            }
            return
        }

        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    _state.update { it.copy(isAdShowing = true, adTypeShowing = "INTERSTITIAL") }
                }

                override fun onAdDismissedFullScreenContent() {
                    _state.update { it.copy(isAdShowing = false, adTypeShowing = "") }
                    interstitialAd = null
                    loadInterstitial()
                    onDismiss()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.w(TAG, "Interstitial failed to show (e.g. emulator buffer/renderer): ${adError.message}")
                    _state.update { it.copy(isAdShowing = false, adTypeShowing = "") }
                    interstitialAd = null
                    startKidInteractiveAd("INTERSTITIAL", 3, onDismiss)
                }
            }
            try {
                ad.show(activity)
                interstitialAd = null
            } catch (e: Exception) {
                Log.e(TAG, "Exception showing interstitial: ${e.message}")
                interstitialAd = null
                startKidInteractiveAd("INTERSTITIAL", 3, onDismiss)
            }
        } else {
            // Ad not preloaded yet or network latency: fallback to kid interactive sponsor
            startKidInteractiveAd("INTERSTITIAL", 3, onDismiss)
            loadInterstitial()
        }
    }

    /**
     * Rewarded ads removed as per user instruction ("alisin natin ang rewarded saka credits free chat tayo").
     * Fallback gives free instant bonus stars safely without video streams.
     */
    fun showRewardedAd(activity: Activity?, onRewardEarned: (Int) -> Unit) {
        val rewardAmount = _state.value.rewardPerAd
        startKidInteractiveAd("REWARDED", 2) {
            grantReward(rewardAmount, onRewardEarned)
        }
    }

    private fun startKidInteractiveAd(type: String, durationSeconds: Int, onComplete: () -> Unit) {
        countdownRunnable?.let { mainHandler.removeCallbacks(it) }
        _state.update {
            it.copy(
                isAdShowing = true,
                adTypeShowing = type,
                adCountdownSeconds = durationSeconds,
                lastAdMessage = if (type == "REWARDED") "Free Tambay Bonus (+${_state.value.rewardPerAd} Stars!)" else "Sponsor break tapos na!"
            )
        }

        fun tick(remaining: Int) {
            if (remaining > 0) {
                _state.update { it.copy(adCountdownSeconds = remaining) }
                val next = Runnable { tick(remaining - 1) }
                countdownRunnable = next
                mainHandler.postDelayed(next, 1000)
            } else {
                _state.update { it.copy(isAdShowing = false, adCountdownSeconds = 0, adTypeShowing = "") }
                onComplete()
            }
        }
        tick(durationSeconds)
    }

    fun dismissCurrentAdManually() {
        countdownRunnable?.let { mainHandler.removeCallbacks(it) }
        _state.update { it.copy(isAdShowing = false, adCountdownSeconds = 0, adTypeShowing = "") }
    }

    private fun grantReward(rewardAmount: Int, onRewardEarned: (Int) -> Unit) {
        val newWatched = _state.value.adsWatchedToday + 1
        val newAllTime = _state.value.totalAdsWatchedAllTime + 1

        prefs.edit()
            .putInt("watched_today", newWatched)
            .putInt("all_time_watched", newAllTime)
            .apply()

        _state.update {
            it.copy(
                adsWatchedToday = newWatched,
                totalAdsWatchedAllTime = newAllTime,
                lastAdMessage = "Yehey! Nakatanggap ka ng +$rewardAmount Bituin!"
            )
        }
        onRewardEarned(rewardAmount)
    }

    // Admin Controls
    fun setBannerEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("banner_enabled", enabled).apply()
        _state.update { it.copy(isBannerEnabled = enabled) }
    }

    fun setInterstitialEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("interstitial_enabled", enabled).apply()
        _state.update { it.copy(isInterstitialEnabled = enabled) }
    }

    fun setRewardedEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("rewarded_enabled", enabled).apply()
        _state.update { it.copy(isRewardedEnabled = enabled) }
    }

    fun setSafeKidAdsMode(enabled: Boolean) {
        prefs.edit().putBoolean("safe_kid_ads_mode", enabled).apply()
        _state.update { it.copy(safeKidAdsMode = enabled) }
    }

    fun setMaxDailyAds(max: Int) {
        prefs.edit().putInt("max_daily", max).apply()
        _state.update { it.copy(maxDailyRewardedAds = max) }
    }

    fun setRewardPerAd(amount: Int) {
        prefs.edit().putInt("reward_per_ad", amount).apply()
        _state.update { it.copy(rewardPerAd = amount) }
    }

    fun resetDailyCounterNow() {
        prefs.edit()
            .putLong("last_reset_time", System.currentTimeMillis())
            .putInt("watched_today", 0)
            .apply()
        _state.update {
            it.copy(
                adsWatchedToday = 0,
                millisUntilReset = TWENTY_FOUR_HOURS_MS,
                lastAdMessage = "Na-reset na ang 24-hour ad counter para sa pagsusuri!"
            )
        }
    }

    fun formatRemainingTime(millis: Long): String {
        val totalSecs = (millis / 1000).coerceAtLeast(0)
        val hours = totalSecs / 3600
        val mins = (totalSecs % 3600) / 60
        return "${hours}h ${mins}m"
    }
}
