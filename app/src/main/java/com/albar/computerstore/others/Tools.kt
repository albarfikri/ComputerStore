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
        val Δlat = toRadians(dLat - oLat)
        val Δlon = toRadians(dLon - oLon)

        val d = 2 * R * asin(
            sqrt(
                kotlin.math.sin(Δlat / 2).pow(2.0) + kotlin.math.cos(oLat) *
                        kotlin.math.cos(dLat) * kotlin.math.sin(Δlon / 2)
                    .pow(2.0)
            )
        )
        L = (d * 100.0).roundToInt() / 100.0
        return (d * 100.0).roundToInt() / 100.0
    }

    fun euclideanFormula(oLat: Double, oLon: Double, dLat: Double, dLon: Double): Double {
        return sqrt((dLat - oLat).pow(2) + (dLon - oLon).pow(2)) * earthDegree
    }

    // Parameter Kecepatan Arus Bebas
    private var FV0: Double = 0.0 // Kecepatan arus bebas dasar (km/jam)
    private var FVw: Double = 0.0 // Penyesuaian kecepatan akibat lebar jalur (km/jam)
    private var FFVsf: Double = 0.0 // Penyesuaian hambatan samping dan lebar bahu jalan
    private var FFVcs: Double = 0.0 // Penyesuaian ukuran kota 994.59 juta

    // Parameter kalkulasi waktu
    private var L: Double = 0.0 // Panjang Rute menggunakan haversine atau euclidean
    private var FV: Double = 0.0 // Kecepatan arus bebas -> output dari kalkulasi speed calculation

    // streetDensity
    private var tk: Double = 0.0 // Total kendaraan
    private var kj: Double = 0.0 // Kapasitas jalan

    private fun speedCalculation(FV0: Double, FVw: Double, FFVsf: Double, FFVcs: Double): Double {
        return (FV0 - FVw) + FFVsf * FFVcs
    }

    private fun timeCalculation(L: Double = this.L, FV: Double): Double {
        return L / FV
    }

    private fun streetDensity(tk: Double, kj: Double): Double {
        return tk / kj
    }

    fun finalOutputWithHaversine(
        availableArea: Array<String>,
        computerAreaFromApi: String,
        distance: Double,
    ): Double {
        // Sudirman
        if (availableArea[0] == computerAreaFromApi) {
            FV0 = 57.0
            FVw = 0.0
            FFVsf = 0.0
            FFVcs = 0.94
            tk = 3395.0
            kj = 4203.11
        }

        // Riau
        if (availableArea[1] == computerAreaFromApi) {
            FV0 = 42.0
            FVw = 3.0
            FFVsf = 0.0
            FFVcs = 0.94
            tk = 1888.96
            kj = 2726.05
        }

        // Ahmad Yani
        if (availableArea[2] == computerAreaFromApi) {
            FV0 = 51.0
            FVw = 0.0
            FFVsf = 0.0
            FFVcs = 0.94
            tk = 1675.0
            kj = 4022.37
        }
        // Durian
        if (availableArea[3] == computerAreaFromApi) {
            FV0 = 42.0
            FVw = 3.0
            FFVsf = 0.0
            FFVcs = 0.94
            tk = 928.5
            kj = 1745.63
        }
        // Tambusai
        if (availableArea[4] == computerAreaFromApi) {
            FV0 = 55.0
            FVw = 0.0
            FFVsf = 0.0
            FFVcs = 0.94
            tk = 1840.9
            kj = 2241.45
        }

        // Soekarno-Hatta
        if (availableArea[5] == computerAreaFromApi) {
            FV0 = 55.0
            FVw = 0.0
            FFVsf = 0.0
            FFVcs = 0.94
            tk = 1996.0
            kj = 4205.78
        }

        FV = speedCalculation(FV0, FVw, FFVsf, FFVcs)

        val timeCalculation: Double = timeCalculation(distance, FV)

        val streetDensity: Double = streetDensity(tk, kj)

        return (timeCalculation + streetDensity) / 3
    }

    fun finalOutputWithEuclidean() {

    }
}