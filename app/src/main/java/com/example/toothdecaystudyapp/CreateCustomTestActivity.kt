package com.example.toothdecaystudyapp

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.toothdecaystudyapp.anim.Animations
import kotlinx.coroutines.launch
import kotlin.math.max

class CreateCustomTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_custom_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val anim = Animations(this)

        val returnButton = findViewById<ImageView>(R.id.returnButton)

        anim.scaleButtonAnim(returnButton)

        returnButton.setOnClickListener(){
            finish()
        }

        val dataStoreManager = SettingsDataStore(this)

        val editText = findViewById<EditText>(R.id.nameET)
        val numCasesET = findViewById<EditText>(R.id.numCasesET)
        val numMinutesET = findViewById<EditText>(R.id.numMinutesET)

        val increaseCases = findViewById<ImageView>(R.id.increaseCases)
        val increaseTime = findViewById<ImageView>(R.id.increaseTime)
        val decreaseTime = findViewById<ImageView>(R.id.decreaseTime)
        val decreaseCases = findViewById<ImageView>(R.id.decreaseCases)

        val confirmButton = findViewById<TextView>(R.id.confirmButton)

        val filter = InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                val c = source[i]
                if (!c.isLetterOrDigit() && !c.isWhitespace()) {
                    return@InputFilter ""
                }
            }
            null
        }

        editText.filters = arrayOf(filter, InputFilter.LengthFilter(32))

        anim.scaleButtonAnim(increaseCases)
        anim.scaleButtonAnim(increaseTime)
        anim.scaleButtonAnim(decreaseTime)
        anim.scaleButtonAnim(decreaseCases)
        anim.setChangeTouch(confirmButton,R.drawable.green_button,R.drawable.green_button_pressed)

        fixETTextSize(numCasesET)
        fixETTextSize(numMinutesET)
        fixETTextSize(editText)

        increaseCases.setOnClickListener(){
            var currentValue = numCasesET.text.toString().toInt()
            if(currentValue<70){
                numCasesET.setText(""+(currentValue+1))
            }
        }

        decreaseCases.setOnClickListener(){
            var currentValue = numCasesET.text.toString().toInt()
            if(currentValue>1){
                numCasesET.setText(""+(currentValue-1))
            }
        }

        numCasesET.setOnFocusChangeListener { _, hasFocus ->

            if (!hasFocus) {
                var currentValue = numCasesET.text.toString().toInt()
                if(currentValue>70){
                    numCasesET.setText("70")
                }else if (currentValue<1){
                    numCasesET.setText("1")
                }

            }
        }


        increaseTime.setOnClickListener(){
            var currentValue = numMinutesET.text.toString().toInt()
            if(currentValue<995){
                numMinutesET.setText(""+(currentValue/5+1)*5)
            }
        }

        decreaseTime.setOnClickListener(){
            var currentValue = numMinutesET.text.toString().toInt()
            if(currentValue>5){
                numMinutesET.setText(""+ ((currentValue-1)/5)*5)
            }
        }

        numMinutesET.setOnFocusChangeListener { _, hasFocus ->

            if (!hasFocus) {
                var currentValue = numMinutesET.text.toString().toInt()
                if(currentValue>999){
                    numMinutesET.setText("999")
                }else if (currentValue<1){
                    numMinutesET.setText("1")
                }

            }
        }

        var running = false

        confirmButton.setOnClickListener(){
            if(!running) {
                lifecycleScope.launch {

                    var currentNumCases = numCasesET.text.toString().toInt()
                    if (currentNumCases > 70) {
                        currentNumCases = 70
                    } else if (currentNumCases < 1) {
                        currentNumCases = 1
                    }

                    var currentTime = numMinutesET.text.toString().toInt()
                    if (currentTime > 999) {
                        currentTime = 999
                    } else if (currentTime < 1) {
                        currentTime = 1
                    }

                    // ADD
                    dataStoreManager.addConfig(
                        SavedRunConfigs(
                            name = editText.text.toString().ifEmpty { "Teste" },
                            numCarieTests = currentNumCases,
                            maxTime = currentTime
                        )
                    )

                    // LOAD ALL
                    val configs = dataStoreManager.loadConfigs()

                    // USE
                    configs.forEach {

                        Log.d("CONFIG", it.name)
                    }

                    finish()

                }
            }
        }



    }

    fun fixETTextSize(editText: EditText){
        editText.post {

            val h = editText.height

            val textSize = h * 0.40f

            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        }
    }


}