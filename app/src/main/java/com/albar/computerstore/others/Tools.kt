package com.albar.computerstore.others

import android.util.Base64
import java.lang.Math.toRadians
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.math.asin
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt


object Tools {
    private const val SECRET_KEY = "abcdefghijklmnop"
    private const val SECRET_IV = "ponmlkjihgfedcba"
    private var DISTANCE = 0.0

    // Haversine
    private const val R = 6372.8

    // Euclidean
    private const val earthDegree = 111.322

    // Encrypt Formula
    fun String.encryptCBC(): String {
        val iv = IvParameterSpec(SECRET_IV.toByteArray())
        val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv)
        val crypted = cipher.doFinal(this.toByteArray())
        val encodedByte = Base64.encode(crypted, Base64.DEFAULT)
        return String(encodedByte)
    }

    // Decrypt Formula
    fun String.decryptCBC(): String {
        val decodedByte: ByteArray = Base64.decode(this, Base64.DEFAULT)
        val iv = IvParameterSpec(SECRET_IV.toByteArray())
        val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv)
        val output = cipher.doFinal(decodedByte)
        return String(output)
    }

    // Haversine Formula & Euclidean Formula
    // o -> origin, d -> destination

    fun haversineFormula(oLat: Double, oLon: Double, dLat: Double, dLon: Double): Double {
        val lat1 = toRadians(oLat)
        val lat2 = toRadians(dLat)
        val Δlat = toRadians(dLat - oLat)
        val Δlon = toRadians(dLon - oLon)

        val d = 2 * R * asin(
            sqrt(
                kotlin.math.sin(Δlat / 2).pow(2.0) + kotlin.math.cos(oLat) *
                        kotlin.math.cos(dLat) * kotlin.math.sin(Δlon / 2)
                    .pow(2.0)
            )
        )
        DISTANCE = (d * 100.0).roundToInt() / 100.0
        return (d * 100.0).roundToInt() / 100.0
    }

    fun euclideanFormula(oLat: Double, oLon: Double, dLat: Double, dLon: Double): Double {
        val d = sqrt((dLat - oLat).pow(2) + (dLon - oLon).pow(2)) * earthDegree

        return d
    }

    fun speedCalculation(FV0: Double, FVw: Double, FFVsf: Double, FFVcs: Double): Double {
        return (FV0 - FVw) + FFVsf * FFVcs
    }

    fun timeCalculation(L: Double = DISTANCE, FV: Double): Double {
        return L / FV
    }

    fun streetDensity(tk: Double, kj: Double): Double {
        return tk / kj
    }

    fun finalOutputWithHaversine(
        distance: Double,
        timeCalculation: Double,
        streetDensity: Double
    ): Double {
        return (distance + timeCalculation + streetDensity) / 3

    }

    fun finalOutputWithEuclidean() {

    }
}