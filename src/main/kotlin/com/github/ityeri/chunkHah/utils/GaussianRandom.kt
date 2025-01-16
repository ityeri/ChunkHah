package com.github.ityeri.chunkHah.utils

import kotlin.random.Random
import kotlin.random.nextInt

class GaussianRandom {
    companion object {
        fun gaussianRandom(center: Int, range: Int): Int {
            val probabilityTable: HashMap<Int, Int> = hashMapOf()

            var currentValue: Int = center - range
            for (i in 0 until range) {
                probabilityTable.set(i, currentValue)
                currentValue++
            }

            for (i in range downTo 0) {
                probabilityTable.set(i, currentValue)
                currentValue++
            }
            val total = probabilityTable.keys.sum()

            val randomInt = Random.nextInt(0 until total)

            var currentWeight = 0

            for ((weight, value) in probabilityTable) {
                currentWeight += weight
                if (randomInt <= currentWeight) {
                    return value
                }
            }
        }

    }
}