package com.albar.computerstore.others

import java.nio.charset.StandardCharsets
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*
import javax.crypto.Cipher


object Tools {
    private val generator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA").also {
        it.initialize(512)
    }
    private val pair: KeyPair = generator.generateKeyPair()

    private val publicKey: PublicKey = pair.public

    private val privateKey: PrivateKey = pair.private

    var byteForMoment: ByteArray = byteArrayOf(0x01, 0x02, 0x03)

    var saveToDecrypt: String = ""

    fun encrypt(message: String): String {
        val encryptCipher: Cipher = Cipher.getInstance("RSA")
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val charsets = Charsets.UTF_8
        val secretMessageBytes = message.toByteArray(charsets)
        val encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes)
        byteForMoment = encryptedMessageBytes
        return Base64.getEncoder().encodeToString(encryptedMessageBytes)
    }

    fun decrypt(): String {
        val decryptCipher = Cipher.getInstance("RSA")
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedMessageBytes = decryptCipher.doFinal(byteForMoment)
        return String(decryptedMessageBytes, StandardCharsets.UTF_8)
    }
}