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

package at.florianschuster.playables.core.remote

import at.florianschuster.playables.core.model.AppInfo
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit

internal val remoteModule = module {
    factory { HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY } }
    factory { provideUserAgentInterceptor(appInfo = get()) }
    single {
        provideOkHttpClient(
            loggingInterceptor = get(),
            userAgentInterceptor = get(),
            appInfo = get()
        )
    }
    single { provideApi<RAWGApi>(okHttpClient = get(), json = get(), appInfo = get()) }
}

private fun provideUserAgentInterceptor(appInfo: AppInfo) = UserAgentInterceptor(
    appName = appInfo.appName,
    version = "${appInfo.version.code}",
    agent = appInfo.userAgent
)

private fun provideOkHttpClient(
    loggingInterceptor: HttpLoggingInterceptor,
    userAgentInterceptor: UserAgentInterceptor,
    appInfo: AppInfo
) = OkHttpClient().newBuilder().apply {
    if (appInfo.debug) addInterceptor(loggingInterceptor)
    addInterceptor(userAgentInterceptor)
}.build()

private inline fun <reified T> provideApi(
    okHttpClient: OkHttpClient,
    json: Json,
    appInfo: AppInfo
): T = Retrofit.Builder().apply {
    baseUrl(appInfo.baseUrl)
    client(okHttpClient)
    addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
}.build().create(T::class.java)