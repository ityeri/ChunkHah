package com.github.ityeri.chunkHah.utils

import kotlin.random.Random
import kotlin.random.nextInt

class GaussianRandom {
    companion object {
        fun gaussianRandom(center: Int, range: Int): Int? {
            val probabilityTable: MutableList<Int> = mutableListOf()

            var currentValue: Int = center - range
            for (i in 0 until range) {
                probabilityTable.add(i)
                currentValue++
            }

            for (i in range downTo 0) {
                probabilityTable.add(i)
                currentValue++
            }
            val total = probabilityTable.sum()

            val randomInt = Random.nextInt(0 until total)

            var currentWeight = 0
            currentValue = center - range

            for (weight in probabilityTable) {
                currentWeight += weight
                if (randomInt <= currentWeight) {
                    return currentValue
                }
                currentValue ++
            }
            return null
        }

    }
}