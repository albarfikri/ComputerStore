package com.albar.computerstore.others

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.*

object Formula {
 const val SECRET_KEY = "abcdefghijklmnop"
    const val SECRET_IV = "ponmlkjihgfedcba"

    // Haversine
    private const val R = 6372.8

    // Euclidean
    private const val earthDegree = 111.322

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

    // Haversine Formula & Euclidean Formula
    // o -> origin, d -> destination
    fun haversineFormula(oLat: Double, oLon: Double, dLat: Double, dLon: Double): Double {
        val Δlat = Math.toRadians(dLat - oLat)
        val Δlon = Math.toRadians(dLon - oLon)
        val d = 2 * R * asin(
            sqrt(
                sin(Δlat / 2).pow(2.0) + cos(oLat) *
                        cos(dLat) * sin(Δlon / 2)
                    .pow(2.0)
            )
        )
        L = (d * 10000.0).roundToInt() / 10000.0
        return (d * 10000.0).roundToInt() / 10000.0
    }

    fun euclideanFormula(oLat: Double, oLon: Double, dLat: Double, dLon: Double): Double {
        val l = sqrt((dLat - oLat).pow(2) +
                (dLon - oLon).pow(2)) * earthDegree
        return (l * 10000.0).roundToInt() / 10000.0
    }

    fun speedCalculation(FV0: Double, FVw: Double, FFVsf: Double, FFVcs: Double): Double {
        return (FV0 - FVw) + FFVsf * FFVcs
    }

    private fun timeCalculation(L: Double = this.L, FV: Double): Double {
        return L / FV
    }

    private fun streetDensity(tk: Double, kj: Double): Double {
        return tk / kj
    }

    fun finalOutput(
        availableArea: Array<String>,
        computerAreaFromApi: String,
        distance: Double,
    ): Double {
        // Sudirman
        if (availableArea[0] == computerAreaFromApi) {
            FV = speedCalculation(57.0 , 0.0 ,  0.96, 0.94)
            tk = 3395.0
            kj = 4203.11
        }

        // Riau
        if (availableArea[1] == computerAreaFromApi) {
            FV = speedCalculation(42.0 , 3.0 ,  0.93, 0.94)
            tk = 1888.96
            kj = 2726.05
        }

        // Ahmad Yani
        if (availableArea[2] == computerAreaFromApi) {
            FV = speedCalculation(51.0 , 0.0,  0.95, 0.94)
            tk = 1675.0
            kj = 4022.37
        }

        // Durian
        if (availableArea[3] == computerAreaFromApi) {
            FV = speedCalculation(42.0 , 3.0,  0.93, 0.94)
            tk = 928.5
            kj = 1745.63
        }

        // Tambusai
        if (availableArea[4] == computerAreaFromApi) {
            FV = speedCalculation(55.0 , 0.0,  0.96, 0.94)
            tk = 1840.9
            kj = 2241.45
        }

        // Soekarno-Hatta
        if (availableArea[5] == computerAreaFromApi) {
            FV = speedCalculation(55.0 , 0.0,  0.96, 0.94)
            tk = 1996.0
            kj = 4205.78
        }

        val timeCalculation: Double = timeCalculation(distance, FV)
        val streetDensity: Double = streetDensity(tk, kj)
        return (distance + timeCalculation + streetDensity) / 3
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeUntilInMadrid(hour: Int): Duration {
        val timezone = ZoneId.of("UTC")
        val now = ZonedDateTime.now(timezone).truncatedTo(ChronoUnit.MINUTES)

        val targetTime = LocalTime.of(hour, 0)
        val targetDate =
            if (now.toLocalTime() <= targetTime) now.toLocalDate()
            else now.toLocalDate().plusDays(1)

        val then = ZonedDateTime.of(targetDate, targetTime, timezone)
        return Duration.between(now, then)
    }
}