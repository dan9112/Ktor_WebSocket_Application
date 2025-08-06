package com.example.ktor_websocket_application.data.remote.serializers

import com.example.ktor_websocket_application.domain.model.Message
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal object MessageSerializer: KSerializer<Message> {
    override val descriptor =
        buildClassSerialDescriptor(serialName = Message::class.java.simpleName) {
            element<String>(elementName = "text")
            element<Long>(elementName = "time")
            element<String>(elementName = "user_name")
            element<String>(elementName = "id")
        }

    override fun serialize(encoder: Encoder, value: Message) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: error(message = "This class can be serialized only by JSON")

        val json = buildJsonObject {
            put(key = "text", value = value.text)
            put(
                key = "time",
                value = value
                    .time
                    .toInstant(timeZone = TimeZone.Companion.currentSystemDefault())
                    .toEpochMilliseconds()
            )
            put(key = "user_name", value = value.userName)
            put(key = "id", value = value.id)
        }
        jsonEncoder.encodeJsonElement(json)
    }

    override fun deserialize(decoder: Decoder): Message {
        val jsonDecoder = decoder as? JsonDecoder
            ?: error(message = "This class can be serialized only by JSON")

        val element = jsonDecoder.decodeJsonElement().jsonObject

        val text = element["text"]
            ?.jsonPrimitive
            ?.contentOrNull
            ?: error(message = "Missing text")
        val time = element["timestamp"]
            ?.jsonPrimitive
            ?.longOrNull
            ?.let(block = Instant.Companion::fromEpochMilliseconds)
            ?.toLocalDateTime(timeZone = TimeZone.Companion.currentSystemDefault())
            ?: error(message = "Missing time")

        val userName = element["username"]
            ?.jsonPrimitive
            ?.contentOrNull
            ?: error(message = "Missing user name")

        val id = element["id"]
            ?.jsonPrimitive
            ?.contentOrNull
            ?: error(message = "Missing id")

        return object : Message {
            override val id = id
            override val text = text
            override val time = time
            override val userName = userName
        }
    }
}
