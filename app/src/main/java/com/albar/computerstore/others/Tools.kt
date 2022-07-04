package com.albar.computerstore.others

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object Tools {
    private const val SECRET_KEY = "abcdefghijklmnop"
    private const val SECRET_IV = "ponmlkjihgfedcba"

     fun String.encryptCBC(): String {
        val iv = IvParameterSpec(SECRET_IV.toByteArray())
        val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv)
        val crypted = cipher.doFinal(this.toByteArray())
        val encodedByte = Base64.encode(crypted, Base64.DEFAULT)
        return String(encodedByte)
    }

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