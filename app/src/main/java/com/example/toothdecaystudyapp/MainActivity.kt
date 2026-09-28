package com.example.toothdecaystudyapp

import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.toothdecaystudyapp.anim.Animations
import com.example.toothdecaystudyapp.dialogs.DialogRunConfigs
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val startButton = findViewById<LinearLayout>(R.id.buttonIniciar)
        val customTestButton = findViewById<LinearLayout>(R.id.buttonCustomTest)
        val configurationsButton = findViewById<LinearLayout>(R.id.buttonConfigurations)
        val contentButton = findViewById<LinearLayout>(R.id.buttonConteudo)
        val selectParameters = findViewById<LinearLayout>(R.id.buttonSelectConfig)
        val anim = Animations(this)

        anim.setChangeTouch(startButton,R.drawable.green_button,R.drawable.green_button_pressed)
        anim.scaleLessButtonAnim(customTestButton)
        anim.setChangeTouch(configurationsButton,R.drawable.orange_button,R.drawable.orange_button_pressed)
        anim.setChangeTouch(contentButton,R.drawable.purple_button,R.drawable.purple_button_pressed)
        anim.scaleLessButtonAnim(selectParameters)


        configurationsButton.setOnClickListener(){
            val intent = Intent(this, ResultsActivity::class.java)
            startActivity(intent)

        }

        startButton.setOnClickListener(){
            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
        }

        customTestButton.setOnClickListener(){
            val intent = Intent(this, CreateCustomTestActivity::class.java)
            startActivity(intent)
        }

        contentButton.setOnClickListener(){
            val intent = Intent(this, ContentActivity::class.java)
            startActivity(intent)
        }

        val dialogSelectParams = DialogRunConfigs(this)
        val dataStore = SettingsDataStore(this)

        selectParameters.setOnClickListener(){
            lifecycleScope.launch {



                val runConfigs = dataStore.loadConfigs()

                val menuOption = dialogSelectParams.showRunConfigsDialog(lifecycleScope,runConfigs,dataStore.getConfigSelected())


            }
        }




    }

//    fun getCarieTest():CarieTest{
//
//        val num = Random.nextInt(0, 7);
//
//        return CarieTest(num,getImg(num)
//
//        );
//    }

    fun getImg(num:Int):Int{
        return when(num){
            0 -> R.drawable.dente_s1
            1 -> R.drawable.dente_s2
            2 -> R.drawable.dente_s3
            3 -> R.drawable.dente_s4
            4 -> R.drawable.dente_s5
            5 -> R.drawable.dente_s6
            6 -> R.drawable.dente2
            else -> {R.drawable.dente2}
        }
    }

}