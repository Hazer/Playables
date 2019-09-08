/*
 * Copyright 2019 Florian Schuster (https://florianschuster.at/).
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

import okhttp3.Interceptor
import okhttp3.Response

class UserAgentInterceptor(
    appName: String,
    version: String,
    agent: String
) : Interceptor {
    private val userAgent = "${appName}/${version} $agent"

    override fun intercept(chain: Interceptor.Chain): Response {
        val userAgentRequest = chain.request().newBuilder().apply {
            header("User-Agent", userAgent)
        }.build()
        return chain.proceed(userAgentRequest)
    }
}