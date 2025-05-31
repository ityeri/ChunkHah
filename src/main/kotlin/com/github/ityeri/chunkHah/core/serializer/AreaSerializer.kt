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

            element<Boolean>("isEnteredOver")
            element<Boolean>("isEnteredNether")
            element<Boolean>("isEnteredEnd")
        }

    override fun serialize(encoder: Encoder, value: Area) {
        val composite = encoder.beginStructure(descriptor)
        composite.encodeStringElement(descriptor, 0, value.playerUUID.toString())
        composite.encodeBooleanElement(descriptor, 1, value.isEnabled)

        composite.encodeIntElement(descriptor, 2, value.x)
        composite.encodeIntElement(descriptor, 3, value.z)

        composite.encodeBooleanElement(descriptor, 4, value.isEnteredOver)
        composite.encodeBooleanElement(descriptor, 5, value.isEnteredNether)
        composite.encodeBooleanElement(descriptor, 6, value.isEnteredEnd)

        composite.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): Area {
        val composite = decoder.beginStructure(descriptor)

        var playerUUID = UUID.randomUUID()
        var enabled = true

        var x = 0
        var z = 0

        var isEnteredOver = false
        var isEnteredNether = false
        var isEnteredEnd =  false

        while (true) {
            when (val index = composite.decodeElementIndex(descriptor)) {
                0 -> playerUUID = UUID.fromString(composite.decodeStringElement(descriptor, index))
                1 -> enabled = composite.decodeBooleanElement(descriptor, index)

                2 -> x = composite.decodeIntElement(descriptor, index)
                3 -> z = composite.decodeIntElement(descriptor, index)

                4 -> isEnteredOver = composite.decodeBooleanElement(descriptor, index)
                5 -> isEnteredNether = composite.decodeBooleanElement(descriptor, index)
                6 -> isEnteredEnd = composite.decodeBooleanElement(descriptor, index)

                CompositeDecoder.DECODE_DONE -> break
            }
        }

        composite.endStructure(descriptor)

        return Area(
            playerUUID, null, x, z, enabled,
            isEnteredOver, isEnteredNether, isEnteredEnd
        )
    }

}