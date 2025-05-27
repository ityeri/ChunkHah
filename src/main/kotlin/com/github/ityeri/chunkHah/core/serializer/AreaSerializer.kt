package com.github.ityeri.chunkHah.core.serializer

import com.github.ityeri.chunkHah.core.Area
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.CompositeDecoder
import java.util.*

object AreaSerializer : KSerializer<Area> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Area") {
            element<String>("playerUUID")
            element<Boolean>("isBind")
            element<Int>("x")
            element<Int>("z")
        }

    override fun serialize(encoder: Encoder, value: Area) {
        val composite = encoder.beginStructure(descriptor)
        composite.encodeStringElement(descriptor, 0, value.playerUUID.toString())
        composite.encodeBooleanElement(descriptor, 1, value.isEnabled)

        composite.encodeIntElement(descriptor, 2, value.x)
        composite.encodeIntElement(descriptor, 3, value.z)

        composite.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): Area {
        val composite = decoder.beginStructure(descriptor)
        var playerUUID = UUID.randomUUID()
        var enabled = true

        var x = 0
        var z = 0

        while (true) {
            when (val index = composite.decodeElementIndex(descriptor)) {
                0 -> playerUUID = UUID.fromString(composite.decodeStringElement(descriptor, index))
                1 -> enabled = composite.decodeBooleanElement(descriptor, index)

                2 -> x = composite.decodeIntElement(descriptor, index)
                3 -> z = composite.decodeIntElement(descriptor, index)

                CompositeDecoder.DECODE_DONE -> break
            }
        }

        composite.endStructure(descriptor)

        return Area(playerUUID, x, z, enabled)
    }

}