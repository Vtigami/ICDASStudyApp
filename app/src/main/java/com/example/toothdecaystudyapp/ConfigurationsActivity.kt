package com.example.toothdecaystudyapp

import android.content.res.Resources
import android.os.Bundle
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.DrawableUtils
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import kotlinx.coroutines.launch

class ConfigurationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_configurations)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar);
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setTitle("")
        }

        toolbar.navigationIcon?.setTint(getColor(R.color.back_arrow))

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }

        val settingsDataStore = SettingsDataStore(this)

        val slider = findViewById<Slider>(R.id.slider)
        slider.setCustomThumbDrawable(R.drawable.dente2)

        val confirmOnSelectCB = findViewById<CheckBox>(R.id.confirmOnSelectCB)

        confirmOnSelectCB.setOnClickListener {
            lifecycleScope.launch {
                settingsDataStore.setConfirmationOnSelection(confirmOnSelectCB.isChecked)
            }
        }

        slider.addOnChangeListener { _, value, fromUser ->
            if (!fromUser) return@addOnChangeListener

            lifecycleScope.launch {
                settingsDataStore.setVolume(value / 100f)
            }
        }





        lifecycleScope.launch {
            settingsDataStore.confirmationOnSelection.collect { enabled ->
                confirmOnSelectCB.isChecked = enabled
            }
        }

        lifecycleScope.launch {
            settingsDataStore.volume.collect { volume ->
                slider.value = volume * 100f
            }
        }






    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.toolbar) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }



}