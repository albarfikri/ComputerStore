package com.albar.computerstore.others

import android.content.Context
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import com.albar.computerstore.others.Formula.SECRET_IV
import com.albar.computerstore.others.Formula.SECRET_KEY
import com.google.android.gms.maps.model.LatLng
import java.text.NumberFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object Tools {
    // Money Converter to Rupiah -> Rp.x.xxx
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

    fun decode(points: String): List<LatLng> {
        val len = points.length
        val path: MutableList<LatLng> = ArrayList(len / 2)
        var index = 0
        var lat = 0
        var lng = 0
        while (index < len) {
            var result = 1
            var shift = 0
            var b: Int
            do {
                b = points[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            result = 1
            shift = 0
            do {
                b = points[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            path.add(LatLng(lat * 1e-5, lng * 1e-5))
        }
        return path
    }

    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}