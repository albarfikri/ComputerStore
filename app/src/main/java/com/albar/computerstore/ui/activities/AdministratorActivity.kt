package com.albar.computerstore.ui.activities

import android.os.Bundle
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.albar.computerstore.R
import com.albar.computerstore.databinding.ActivityAdministratorBinding
import com.albar.computerstore.others.toastShort
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdministratorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdministratorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdministratorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        popupMenu()
    }

    private fun popupMenu() {
        val popupMenu = PopupMenu(applicationContext, binding.ivSettings)
        popupMenu.inflate(R.menu.popup_menu)
        popupMenu.setOnMenuItemClickListener { clicked ->
            when (clicked.itemId) {
                R.id.nav_edit -> {
                    toastShort("Head up to Edit")
                    true
                }
                R.id.nav_logout -> {
                    toastShort("Head up to Exit")
                    true
                }
                else -> true

            }
        }

        binding.ivSettings.setOnClickListener {
            try {
                val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val menu = popup.get(popupMenu)
                menu.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                popupMenu.show()
            }

            true
        }
    }
}