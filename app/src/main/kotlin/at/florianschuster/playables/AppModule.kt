package at.florianschuster.playables

import android.content.Context
import android.content.pm.ApplicationInfo
import android.webkit.WebSettings
import at.florianschuster.playables.core.model.ClientInfo
import at.florianschuster.playables.detail.detailModule
import at.florianschuster.playables.main.mainModule
import at.florianschuster.playables.playables.playablesModule
import at.florianschuster.playables.search.searchModule
import com.squareup.leakcanary.LeakCanary
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val appModule = module {
    single { LeakCanary.install(androidApplication()) }
    single { provideAppBuildInfo(context = androidContext()) }
}

private fun provideAppBuildInfo(context: Context): ClientInfo = ClientInfo(
    appName = context.resources.getString(R.string.app_name),
    debug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0,
    buildType = BuildConfig.BUILD_TYPE,
    flavor = BuildConfig.FLAVOR,
    version = ClientInfo.Version(
        code = BuildConfig.VERSION_CODE,
        name = BuildConfig.VERSION_NAME
    ),
    userAgent = WebSettings.getDefaultUserAgent(context)
)

internal val appModules = listOf(appModule, mainModule, playablesModule, searchModule, detailModule)
