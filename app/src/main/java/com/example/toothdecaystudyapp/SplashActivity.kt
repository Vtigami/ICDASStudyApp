package com.example.toothdecaystudyapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.toothdecaystudyapp.anim.Animations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val anim = Animations(this)
        val context = this

        val settingsDataStore = SettingsDataStore(this)

        val confirmOnSelectCB = findViewById<CheckBox>(R.id.confirmOnSelectCB)

        confirmOnSelectCB.setOnClickListener {
            lifecycleScope.launch {
                settingsDataStore.setConfirmationOnSelection(confirmOnSelectCB.isChecked)
            }
        }

        lifecycleScope.launch {
            settingsDataStore.confirmationOnSelection.collect { enabled ->
                confirmOnSelectCB.isChecked = enabled
            }
        }

        val returnButton = findViewById<ImageView>(R.id.returnButton)

        anim.scaleButtonAnim(returnButton)

        returnButton.setOnClickListener(){
            finish()
        }

        val textTV = findViewById<TextView>(R.id.text)
        val icon = findViewById<ImageView>(R.id.icon)
        val startButton = findViewById<LinearLayout>(R.id.buttonStart)
        startButton.isClickable=false


        val rotate = ObjectAnimator.ofFloat(icon, View.ROTATION, 0f, 360f).apply {
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
        }

        val scaleX = ObjectAnimator.ofFloat(icon, View.SCALE_X, 1f, 0.4f, 1f).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }

        val scaleY = ObjectAnimator.ofFloat(icon, View.SCALE_Y, 1f, 0.4f, 1f).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }

        val animLoading = AnimatorSet().apply {
            playTogether(rotate, scaleX, scaleY)
            start()
        }

        val dataStore = SettingsDataStore(this)

        var testCreated=false

        val editText = findViewById<EditText>(R.id.nameET)

        val filter = InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                val c = source[i]
                if (!c.isLetterOrDigit() && !c.isWhitespace()) {
                    return@InputFilter ""
                }
            }
            null
        }

        editText.filters = arrayOf(filter, InputFilter.LengthFilter(96))

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(testCreated==true&&editText.text.toString().length>=4){
                    startButton.isClickable=true
                    startButton.background = AppCompatResources. getDrawable(context,R.drawable.green_button)
                    anim.setChangeTouch(startButton,R.drawable.green_button,R.drawable.green_button_pressed)

                    animLoading.cancel()
                    icon.rotation = 0f
                    icon.scaleX = 1f
                    icon.scaleY = 1f
                    icon.setImageResource(R.drawable.iniciar_icon)
                }else{
                    startButton.isClickable=false
                    startButton.background = AppCompatResources. getDrawable(context,R.drawable.disabled_button)
                    anim.disableChangeTouch(startButton)
                    icon.setImageResource(R.drawable.loading4p)
                    animLoading.start()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })



        CoroutineScope(Dispatchers.Main).launch {
            val selectedIndex = dataStore.getConfigSelected()
            var runConfigs : SavedRunConfigs
            if(selectedIndex==0){
                runConfigs = SavedRunConfigs("Padrão",10,10)
            }else {
                runConfigs = dataStore.loadConfigs()[selectedIndex - 1]
            }

            val paths = listOf("stage1","stage2","stage3","stage4","stage5","stage6","higido")

            val qtList = createQtdForEachStage(runConfigs.numCarieTests)

            Log.d("teste","size = ${qtList.size}")

            val carieTests = mutableListOf<CarieTest>()

            qtList.forEachIndexed(){index, it->

                val imagesPath = getRandomImagesFromAssets(context,paths[index],it)
                imagesPath.forEach{it->
                    carieTests.add(CarieTest(index,it))
                }

            }

            carieTests.shuffle()

            carieTests.forEach(){it->
                Log.d("teste","path: ${it.carieImg} type: ${it.carieType}")
            }

            startButton.isClickable=false

            startButton.setOnClickListener() {

                val runConfig = RunConfig(runConfigs.name,carieTests,runConfigs.maxTime,editText.text.toString())

                val intent = Intent(context, CariePredictActivity::class.java)
                intent.putExtra("run_config", runConfig)
                startActivity(intent)
                finish()
            }

            if(editText.text.toString().length>=4){
                startButton.isClickable=true
                startButton.background = AppCompatResources. getDrawable(context,R.drawable.green_button)
                anim.setChangeTouch(startButton,R.drawable.green_button,R.drawable.green_button_pressed)

                animLoading.cancel()
                icon.rotation = 0f
                icon.scaleX = 1f
                icon.scaleY = 1f
                icon.setImageResource(R.drawable.iniciar_icon)
            }else{
                startButton.isClickable=false
            }

            textTV.text="TESTE CRIADO"
            testCreated=true





        }




    }

    fun createQtdForEachStage(numCarieTests:Int):List<Int>{

        val startNum = max((numCarieTests-1)/7,0)
        val qtToAdd = (numCarieTests)-(startNum*7)

        val list = MutableList(7) { startNum }


        for(i in 0 until qtToAdd){
            var num = Random.nextInt(0, 7);

            while(list[num]>=startNum+2){
                num=nextPos(num)
            }

            list[num]=list[num]+1

        }

        return list
    }


    fun nextPos(pos:Int):Int{
        return (pos+1)%7
    }

    fun getRandomImagesFromAssets(context: Context, folderName: String, count: Int): List<String> {
        val assetManager = context.assets

        val files = assetManager.list(folderName)?.toList() ?: emptyList()

        return files
            .filter { it.endsWith(".png", true) || it.endsWith(".jpg", true) || it.endsWith(".jpeg", true) }
            .shuffled()
            .take(count)
            .map { "$folderName/$it" }
    }

}