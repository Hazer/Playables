package at.florianschuster.playables.core.android

import android.content.Context.MODE_PRIVATE
import at.florianschuster.playables.core.android.local.RoomGamesDatabase
import at.florianschuster.playables.core.android.local.SharedPrefsSettings
import at.florianschuster.playables.core.android.provider.AndroidDispatcherProvider
import at.florianschuster.playables.core.android.provider.FirebaseAnalyticsProvider
import at.florianschuster.playables.core.android.provider.AndroidNetworkConnectivityProvider
import at.florianschuster.playables.core.local.GamesDatabase
import at.florianschuster.playables.core.local.Settings
import at.florianschuster.playables.core.provider.AnalyticsProvider
import at.florianschuster.playables.core.provider.DispatcherProvider
import at.florianschuster.playables.core.provider.NetworkConnectivityProvider
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreAndroidModule = module {
    single<GamesDatabase> { RoomGamesDatabase(context = androidContext()) }

    single<DispatcherProvider> { AndroidDispatcherProvider() }
    single<NetworkConnectivityProvider> { AndroidNetworkConnectivityProvider(context = androidContext()) }
    single<Settings> {
        SharedPrefsSettings(
            prefs = androidContext().getSharedPreferences("settings", MODE_PRIVATE)
        )
    }
    single<AnalyticsProvider> {
        FirebaseAnalyticsProvider(
            analytics = FirebaseAnalytics.getInstance(androidContext()),
            settings = get()
        )
    }
}