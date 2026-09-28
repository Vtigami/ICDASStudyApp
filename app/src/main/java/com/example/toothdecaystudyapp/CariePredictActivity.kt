package com.example.toothdecaystudyapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.toothdecaystudyapp.anim.Animations
import com.example.toothdecaystudyapp.dialogs.DialogConfirmation
import com.example.toothdecaystudyapp.dialogs.DialogMenu
import com.example.toothdecaystudyapp.dialogs.DialogResult
import com.example.toothdecaystudyapp.extra.TonePlayer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch


class CariePredictActivity : AppCompatActivity() {

    var confirmOnSelect=false
    var volume = 0f

    var runIndex = 0
    lateinit var runConfig:RunConfig
    lateinit var answerList :MutableList<Int>
    var numAcertos =0

    lateinit var timer:CountDownTimer
    var timeLeftMillis: Long = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carie_predict)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        runConfig =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("run_config", RunConfig::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra("run_config")
            }!!

        val runIndexMax = runConfig?.carieTests?.size ?: 0


        answerList = MutableList(runIndexMax) {  -1 }



        val anim = Animations(this)
        val settingsDataStore = SettingsDataStore(this)

        lifecycleScope.launch {
            settingsDataStore.confirmationOnSelection.collect { enabled ->
                confirmOnSelect=enabled
            }
        }

        lifecycleScope.launch {
            settingsDataStore.volume.collect { savedVolume ->
                volume = savedVolume
            }
        }

        val bottomSheet = findViewById<MaterialCardView>(R.id.bottomSheetButtons)
        val bottomSheetLayout = bottomSheet.findViewById<RelativeLayout>(R.id.includeButtons)
        val topDownButton = bottomSheetLayout.findViewById<ImageButton>(R.id.bottom_button)
        val confirmButton = bottomSheetLayout.findViewById<TextView>(R.id.confirmButton)
        val pauseButton = findViewById<ImageView>(R.id.pauseButton)
        val denteImg = findViewById<ImageView>(R.id.dente)
        val denteNum = findViewById<TextView>(R.id.denteNum)
        val maxDenteNum = findViewById<TextView>(R.id.maxDenteNum)

        var selectValue = 6


        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isHideable = false

        bottomSheetBehavior.peekHeight = dpToInt(0)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        topDownButton.alpha=0f;

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
                val visiblePercent = slideOffset * 100f
                val alpha = slideOffset.coerceIn(0f, 1f)


                topDownButton.alpha = 1-alpha
            }
        })

        topDownButton.setOnClickListener(){
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        
        val buttons = listOf(bottomSheetLayout.findViewById<TextView>(R.id.level1Button),bottomSheetLayout.findViewById<TextView>(R.id.level2Button),bottomSheetLayout.findViewById<TextView>(R.id.level3Button),
            bottomSheetLayout.findViewById<TextView>(R.id.level4Button),bottomSheetLayout.findViewById<TextView>(R.id.level5Button),bottomSheetLayout.findViewById<TextView>(R.id.level6Button),bottomSheetLayout.findViewById<TextView>(R.id.carieFreeButton));

        val tones =  listOf(
            261.63, // Do (C4)
            293.66, // Re (D4)
            329.63, // Mi (E4)
            349.23, // Fa (F4)
            392.00, // So (G4)
            440.00, // La (A4)
            493.88, // Ti (B4)
            523.25  // Do (C5)
        )

        val player = TonePlayer()
        buttons.forEachIndexed { index, it ->
            it.setOnTouchListener { v, event ->
                when (event.action) {

                    MotionEvent.ACTION_DOWN -> {
                        v.animate()
                            .scaleX(0.85f)
                            .scaleY(0.85f)
                            .setDuration(50)
                            .start()

                        player.playTone(tones[index],volume)

                        setAllButtonsToNormal(buttons)
                        selectValue=index
                        it.setBackgroundResource(R.drawable.selected_button)
                        true
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(350)
                            .setInterpolator(OvershootInterpolator(4f))
                            .start()


                        true
                    }

                    else -> false
                }
            }



            val dialogConfirmation = DialogConfirmation(this)
            val dialogMenu = DialogMenu(this)


            anim.scaleButtonAnim(confirmButton)
            anim.scaleButtonAnim(pauseButton)


            runConfig?.carieTests?.elementAt(runIndex)?.carieImg?.let { path ->
                Glide.with(denteImg.context)
                    .load("file:///android_asset/$path")
                    .into(denteImg)
            }

            denteNum.text = ""+(runIndex+1)
            maxDenteNum.text = ""+runIndexMax


            confirmButton.setOnClickListener(){
                lifecycleScope.launch {

                    //verify confirm
                    if (confirmOnSelect) {
                        val confirmed = dialogConfirmation.showConfirmDialog("Selecionar "+getSelectedValueName(selectValue)+"?")

                        if (!confirmed) {
                            return@launch
                        }
                    }

                    if (runConfig != null) {

                        answerList.set(runIndex,selectValue)
                        if(selectValue==runConfig.carieTests.elementAt(runIndex).carieType){
                            numAcertos++
                            //doSomething
                        }
                    }
                    if(runIndex<runIndexMax-1) {
                        nextCase()
                    }else{
                        endTest()
                    }
                }
            }

            //pause button
            pauseButton.setOnClickListener(){
                lifecycleScope.launch {
                    val menuOption = dialogMenu.showMenuDialog(lifecycleScope)?:0

                    lifecycleScope.launch {
                        settingsDataStore.volume.collect { savedVolume ->
                            volume = savedVolume
                        }
                    }

                    if(menuOption == 1) {
                        val confirmed = dialogConfirmation.showConfirmDialog("DESEJA REALMENTE REINICIAR O TESTE?")

                        if (!confirmed) {
                            return@launch
                        }
                        val intent = Intent(this@CariePredictActivity, SplashActivity::class.java)
                        startActivity(intent)
                        finish()

                    }else if(menuOption == 2){
                        val confirmed = dialogConfirmation.showConfirmDialog("DESEJA REALMENTE SAIR PARA O MENU?")

                        if (!confirmed) {
                            return@launch
                        }
                        finish()
                    }
                }
            }

        }


        val progressBar = findViewById<ProgressBar>(R.id.timerBar)
        val timerTV = findViewById<TextView>(R.id.timer)
        val totalTimeMillis = runConfig?.maxTime!!*  60 * 1000L

        progressBar.max = 100


        timer = object : CountDownTimer(totalTimeMillis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeftMillis=millisUntilFinished
                val totalSeconds = millisUntilFinished / 1000

                val hours = totalSeconds / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60

                val timeFormatted = String.format("%d:%02d:%02d", hours, minutes, seconds)
                timerTV.text = timeFormatted
                val progress = ((totalTimeMillis - millisUntilFinished) * 100 / totalTimeMillis).toInt()
                progressBar.progress = progress
            }

            override fun onFinish() {
                timerTV.text = "0:00:00"

                progressBar.progress = 100
                lifecycleScope.launch {
                    endTest()
                }
            }

        }.start()


    }

    fun startTest(){

        val denteImg = findViewById<ImageView>(R.id.dente)
        val denteNum = findViewById<TextView>(R.id.denteNum)

        runIndex =0
        runConfig?.carieTests?.elementAt(runIndex)?.carieImg?.let { path ->
            Glide.with(denteImg.context)
                .load("file:///android_asset/$path")
                .into(denteImg)
        }
        denteNum.text = ""+(runIndex+1)
        numAcertos=0

        timer.start()
    }

    fun nextCase(){

        val denteImg = findViewById<ImageView>(R.id.dente)
        val denteNum = findViewById<TextView>(R.id.denteNum)

        runIndex++
        runConfig?.carieTests?.elementAt(runIndex)?.carieImg?.let { path ->

            Glide.with(denteImg.context)
                .load("file:///android_asset/$path")
                .into(denteImg)
        }
        denteNum.text = ""+(runIndex+1)
    }

    suspend fun endTest(){



        val dialogResult = DialogResult(this)

        timer.cancel()
        var secondsLeft = (runConfig?.maxTime!!*60)-(timeLeftMillis/1000)

        val returnOption= dialogResult.showResultDialog(lifecycleScope,answerList,runConfig,numAcertos,secondsLeft)

        if(returnOption==0) {

            val intent = Intent(this@CariePredictActivity, SplashActivity::class.java)
            startActivity(intent)
            finish()

        }else if(returnOption == 1) {
            startTest()

        }else if(returnOption == 2){
            finish()

        }
    }

    fun getSelectedValueName(value:Int):String{
        if(value == 6)
            return "HÍGIDO"
        return "ESTÁGIO "+(value+1).toString()
    }

    fun setAllButtonsToNormal(list:List<TextView>){
        list.forEach(){
            it.setBackgroundResource(R.drawable.unselected_button)
        }

    }


    fun dpToInt(i: Int):Int{
        return (i*resources.displayMetrics.density).toInt()
    }

 
}