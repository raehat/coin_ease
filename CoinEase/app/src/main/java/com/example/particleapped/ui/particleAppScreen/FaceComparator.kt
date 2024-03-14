package com.example.particleapped.ui.particleAppScreen

import android.graphics.Bitmap

object FaceComparator {
    fun Bitmap.calculateDHash(): String {
        val resizedBitmap = Bitmap.createScaledBitmap(this, 9, 8, true)
        val hashBuilder = StringBuilder()
        for (y in 0 until 8) {
            for (x in 0 until 8) {
                val leftPixel = resizedBitmap.getPixel(x, y)
                val rightPixel = resizedBitmap.getPixel(x + 1, y)
                hashBuilder.append(if (leftPixel < rightPixel) '1' else '0')
            }
        }
        return hashBuilder.toString()
    }

    fun hammingDistance(hash1: String, hash2: String): Int {
        require(hash1.length == hash2.length) { "Hashes must be of equal length" }
        var distance = 0
        for (i in hash1.indices) {
            if (hash1[i] != hash2[i]) {
                distance++
            }
        }
        return distance
    }

    fun areFacesSimilar(bitmap1: Bitmap, bitmap2: Bitmap): Boolean {
        val hash1 = bitmap1.calculateDHash()
        val hash2 = bitmap2.calculateDHash()
        val distance = hammingDistance(hash1, hash2)
        // Define a threshold based on your requirements
        val threshold = 10
        return distance <= threshold
    }

}
