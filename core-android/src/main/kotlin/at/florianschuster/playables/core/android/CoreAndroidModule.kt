package at.florianschuster.playables.core.android

import at.florianschuster.playables.core.android.local.RoomDatabase
import at.florianschuster.playables.core.android.provider.AndroidNetworkConnectivityProvider
import at.florianschuster.playables.core.local.Database
import at.florianschuster.playables.core.provider.NetworkConnectivityProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreAndroidModule = module {
    single<Database> { RoomDatabase() }

    single<NetworkConnectivityProvider> { AndroidNetworkConnectivityProvider(context = androidContext()) }
}