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

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.Serializer
import kotlinx.serialization.internal.SerialClassDescImpl
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

@Serializer(forClass = LocalDate::class)
object LocalDateSerializer : KSerializer<LocalDate> {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyy-MM-dd")

    override val descriptor: SerialDescriptor = SerialClassDescImpl("LocalDate")

    override fun serialize(encoder: Encoder, obj: LocalDate) {
        encoder.encodeString(dateFormatter.format(obj))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.from(dateFormatter.parse(decoder.decodeString()))
    }
}