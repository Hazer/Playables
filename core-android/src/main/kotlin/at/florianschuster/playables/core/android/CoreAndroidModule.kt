package at.florianschuster.playables.core.android

import at.florianschuster.playables.core.android.local.RoomGamesDatabase
import at.florianschuster.playables.core.android.provider.AndroidDispatcherProvider
import at.florianschuster.playables.core.android.provider.AndroidNetworkConnectivityProvider
import at.florianschuster.playables.core.local.GamesDatabase
import at.florianschuster.playables.core.provider.DispatcherProvider
import at.florianschuster.playables.core.provider.NetworkConnectivityProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreAndroidModule = module {
    single<GamesDatabase> { RoomGamesDatabase(context = androidContext()) }

    single<DispatcherProvider> { AndroidDispatcherProvider() }
    single<NetworkConnectivityProvider> { AndroidNetworkConnectivityProvider(context = androidContext()) }
}