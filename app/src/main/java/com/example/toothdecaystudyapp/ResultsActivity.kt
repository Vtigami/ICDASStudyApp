package com.example.toothdecaystudyapp

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toothdecaystudyapp.anim.Animations
import kotlinx.coroutines.launch

class ResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_results)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dataStore = SettingsDataStore(this)
        var resultList: MutableList<TestResult>

        val anim = Animations(this)

        val returnButton = findViewById<ImageView>(R.id.returnButton)

        anim.scaleButtonAnim(returnButton)

        returnButton.setOnClickListener(){
            finish()
        }



        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        lifecycleScope.launch {
            resultList = dataStore.loadTestResults()

            recyclerView.adapter =
                ResultsAdapter(
                    resultList,
                    lifecycleScope
                )
        }

    }
}