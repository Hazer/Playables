package at.florianschuster.playables

import android.app.Application
import at.florianschuster.control.configuration.configureControl
import at.florianschuster.playables.core.android.coreAndroidModule
import com.squareup.leakcanary.LeakCanary
import at.florianschuster.playables.core.coreModule
import coil.Coil
import coil.ImageLoader
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class PlayablesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) return

        Timber.plant(Timber.DebugTree())
        Coil.setDefaultImageLoader(ImageLoader(this) { allowHardware(false) })

        configureControl {
            errors(Timber::e)
            operations(logger = { Timber.v(it) })
        }

        startKoin {
            androidContext(this@PlayablesApp)
            androidLogger(Level.INFO)
            modules(appModules + coreAndroidModule + coreModule)
        }
    }
}
