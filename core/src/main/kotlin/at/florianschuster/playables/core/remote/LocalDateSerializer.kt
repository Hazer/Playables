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