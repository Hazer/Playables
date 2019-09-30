/*
 * Copyright 2019 Florian Schuster.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.florianschuster.playables

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import at.florianschuster.playables.core.coreModules
import at.florianschuster.playables.base.ui.baseUIModule
import coil.Coil
import coil.ImageLoader
import coil.ImageLoaderBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class PlayablesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) return

        instance = this

        Timber.plant(Timber.DebugTree())
        AndroidThreeTen.init(this)

        Coil.setDefaultImageLoader(ImageLoader(this) { allowHardware(false) })

        startKoin {
            androidContext(this@PlayablesApp)
            androidLogger(Level.INFO)
            modules(coreModules + baseUIModule + appModules)
        }
    }

    companion object {
        lateinit var instance: PlayablesApp
            private set
    }
}
