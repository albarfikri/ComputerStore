package com.albar.computerstore.testing

import kotlin.math.*
import kotlin.random.Random

fun main() {
    val begin = System.currentTimeMillis()
    val originLat = 0.5122466
    val originLng = 101.4505945

    val destLatLng = arrayListOf<LatLng>()

    for (data in 0..1000000) {
        destLatLng.add(LatLng(desLat = random(data), desLng = random(data)))
    }

    destLatLng.forEach { latLng ->
        println(
            "Jarak Pengguna ke lokasi tujuan : " +
                    "${euclideanFormula(originLat, originLng, latLng.desLat, latLng.desLng)} km"
        )
    }
    val end = System.currentTimeMillis();
    val tDelta: Long = end - begin
    print("Elapsed time to executed code ${tDelta/1000.0} seconds")
}

data class LatLng(
    val desLat: Double = 0.0,
    val desLng: Double = 0.0
)

fun random(seed: Int): Double {
    val leftLimit = 0F
    val rightLimit = 100F
    return leftLimit + Random(seed).nextDouble() * (rightLimit - leftLimit)
}

fun haversineAlgorithm(oLat: Double, oLon: Double, dLat: Double, dLon: Double): Double {
    val Δlat = Math.toRadians(dLat - oLat)
    val Δlon = Math.toRadians(dLon - oLon)
    val R = 6372.8
    val d = 2 * R * asin(
        sqrt(
            sin(Δlat / 2).pow(2.0) + cos(oLat) *
                    cos(dLat) * sin(Δlon / 2)
                .pow(2.0)
        )
    )
    return d
}


fun euclideanFormula(oLat: Double, oLon: Double, dLat: Double, dLon: Double): Double {
    val earthDegree = 111.322
    val l = sqrt((dLat - oLat).pow(2) +
            (dLon - oLon).pow(2)) * earthDegree
    return (l * 10000.0).roundToInt() / 10000.0
}
