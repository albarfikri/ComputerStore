package com.albar.computerstore.others

import android.util.Base64
import java.lang.Math.toRadians
import java.text.NumberFormat
import java.util.*
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

    // Money Converter
    fun moneyConverter(number: Double): String {
        val localeID = Locale("IND", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        val rupiahFormat = numberFormat.format(number)
        val split = rupiahFormat.split(",")
        val length = split[0].length
        return split[0].substring(0, 2) + ". " + split[0].substring(2, length)
    }

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
}