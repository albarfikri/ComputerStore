package com.albar.computerstore.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.albar.computerstore.databinding.ActivitySplashScreenBinding
import com.albar.computerstore.others.AppUtility
import com.albar.computerstore.others.Constants
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class SplashScreenActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermission()
    }

    // Checking permissions with EasyPermissions
    private fun requestPermission() {
        if (AppUtility.hasLocationPermission(this)) {
            navigate()
            return
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this, "You need to accept to location permissions to use this app.",
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept to location permissions to use this app.",
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    private fun navigate() {
        Handler(Looper.myLooper()!!).postDelayed({
            val intent = Intent(this, OnBoardingActivity::class.java)
            startActivity(intent)
        }, 1000L)
    }

    private fun askingForPermissionAgain() {
        Handler(Looper.myLooper()!!).postDelayed({
            requestPermission()
        }, 1500L)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(this, "accepted", Toast.LENGTH_SHORT).show()
        if (requestCode == Constants.REQUEST_CODE_LOCATION_PERMISSION) {
            navigate()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
            askingForPermissionAgain()
        } else {
            requestPermission()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {}

    override fun onRationaleDenied(requestCode: Int) {
        Toast.makeText(this, "App cannot work without permission", Toast.LENGTH_SHORT)
            .show()
        askingForPermissionAgain()
    }

    // AndroidFramework Functions.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}