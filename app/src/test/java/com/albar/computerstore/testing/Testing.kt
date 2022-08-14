package com.albar.computerstore.testing

import kotlin.math.*
import kotlin.random.Random

fun main() {
    val begin = System.currentTimeMillis()
    val originLat = 0.5122466
    val originLng = 101.4571391

    val destLatLng: ArrayList<LatLng> = arrayListOf(
        LatLng(
            desLat = 0.5082327,
            desLng = 101.4401780
        ),
        LatLng(
            desLat = 0.5064540,
            desLng = 101.4340503
        ),
        LatLng(
            desLat = 0.5062402,
            desLng = 101.4336315
        ),
        LatLng(
            desLat = 0.5076289,
            desLng = 101.4386748
        ),
        LatLng(
            desLat = 0.5082573,
            desLng = 101.4401638
        ),
        LatLng(
            desLat = 0.506454,
            desLng = 101.4275003
        ),
        LatLng(
            desLat = 0.5081898,
            desLng = 101.4399889
        ),
        LatLng(
            desLat = 0.5082373,
            desLng = 101.4401563
        ),
        LatLng(
            desLat = 0.507656,
            desLng = 101.4478113
        ),
        LatLng(
            desLat = 0.5134308,
            desLng = 101.4334551
        )
    )

//    for (data in 0..1000000) {
//        destLatLng.add(LatLng(desLat = random(data), desLng = random(data)))
//    }

    destLatLng.forEach { latLng ->
        println(
            "Jarak Pengguna ke lokasi tujuan : " +
                    "${haversineAlgorithm(originLat, originLng, latLng.desLat, latLng.desLng)} km"
        )
    }

    val end = System.currentTimeMillis();
    val tDelta: Long = end - begin
    print("Elapsed time to executed code ${tDelta / 1000.0} seconds")
}

data class LatLng(
    val desLat: Double = 0.0,
    val desLng: Double = 0.0
)

fun random(seed: Int): Double {
    val leftLimit = 10F
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
    return (d * 1000.0).roundToInt() / 1000.0
}


fun euclideanFormula(oLat: Double, oLon: Double, dLat: Double, dLon: Double): Double {
    val earthDegree = 111.322
    val l = sqrt(
        (dLat - oLat).pow(2) +
                (dLon - oLon).pow(2)
    ) * earthDegree
    return (l * 10000.0).roundToInt() / 10000.0
}
