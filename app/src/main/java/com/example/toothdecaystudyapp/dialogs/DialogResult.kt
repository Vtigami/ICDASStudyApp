package com.example.toothdecaystudyapp.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toothdecaystudyapp.CircleImageAdapter
import com.example.toothdecaystudyapp.R
import com.example.toothdecaystudyapp.RunConfig
import com.example.toothdecaystudyapp.SettingsDataStore
import com.example.toothdecaystudyapp.TestResult
import com.example.toothdecaystudyapp.anim.Animations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.math.max

class DialogResult(private val context: Context) {

    @SuppressLint("ClickableViewAccessibility")
    suspend fun showResultDialog(scope: CoroutineScope, answerList:MutableList<Int>, runConfig:RunConfig, numAcertos:Int, secondsLeft:Long): Int =
        suspendCancellableCoroutine { continuation ->

            val view = LayoutInflater.from(context)
                .inflate(R.layout.result_dialog_layout, null)

            val dialog = AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create()

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            var saving=false

            val carieTest = runConfig.carieTests

            val dataStore=SettingsDataStore(context)

            val acertosTv = view.findViewById<TextView>(R.id.acertosTV)
            val porcentagemTv = view.findViewById<TextView>(R.id.porcentagemTV)
            val timeTV = view.findViewById<TextView>(R.id.timeTV)
            val btnSave = view.findViewById<TextView>(R.id.saveButton)
            val btnExit = view.findViewById<TextView>(R.id.returnButton)
            val btnAgain = view.findViewById<TextView>(R.id.againButton)



            val anim = Animations(context)

            anim.scaleButtonAnim(btnSave)
            anim.scaleButtonAnim(btnExit)
            anim.scaleButtonAnim(btnAgain)

            acertosTv.text = "${numAcertos}/${answerList.size}"

            porcentagemTv.text = "${((numAcertos.toFloat()/answerList.size)*100).toInt()}%"


            val hours = secondsLeft / 3600
            val minutes = (secondsLeft % 3600) / 60
            val seconds = secondsLeft % 60


            timeTV.text = String.format("%d:%02d:%02d", hours, minutes, seconds)


            val recyclerView = view.findViewById<RecyclerView>(R.id.resultsList)

            recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            recyclerView.setHasFixedSize(true)

            val result = MutableList(answerList.size) {-1}
            val histogramResults = MutableList(7){0}



            for (i in 0 until answerList.size){

                var correct =-1
                if(answerList.get(i) == carieTest[i].carieType)
                    correct = 0
                else{
                    histogramResults[carieTest[i].carieType]++
                }


                result.set(i,correct)
            }


            val activity = context as? AppCompatActivity

            val adapter = CircleImageAdapter(carieTest,result,context) { position ->
                activity?.lifecycleScope?.launch {
                    val dialogPaint = DialogPaint(context,carieTest[position].carieImg,carieTest[position].carieType,answerList.get(position))
                    dialogPaint.showPaintDialog()
                }
            }

            recyclerView.adapter = adapter

            val histogramLayout = view.findViewById<LinearLayout>(R.id.histogramLayout)

            val showHistogram = view.findViewById<TextView>(R.id.showHistogramBT)
            val histogram = view.findViewById<LinearLayout>(R.id.histogram)

            showHistogram.setOnClickListener(){
                if(histogram.visibility == View.VISIBLE){
                    histogram.visibility = View.GONE
                    showHistogram.setBackgroundResource(R.drawable.histogram_button_unselected)
                }else{
                    histogram.visibility = View.VISIBLE
                    showHistogram.setBackgroundResource(R.drawable.histogram_button_selected)
                }
            }


            val numErrorList = listOf(
                histogramLayout.findViewById<TextView>(R.id.numBar0),
                histogramLayout.findViewById<TextView>(R.id.numBar1),
                histogramLayout.findViewById<TextView>(R.id.numBar2),
                histogramLayout.findViewById<TextView>(R.id.numBar3),
                histogramLayout.findViewById<TextView>(R.id.numBar4),
                histogramLayout.findViewById<TextView>(R.id.numBar5),
                histogramLayout.findViewById<TextView>(R.id.numBar6)
            )

            val histogramBar = listOf(
                histogramLayout.findViewById<TextView>(R.id.bar0),
                histogramLayout.findViewById<TextView>(R.id.bar1),
                histogramLayout.findViewById<TextView>(R.id.bar2),
                histogramLayout.findViewById<TextView>(R.id.bar3),
                histogramLayout.findViewById<TextView>(R.id.bar4),
                histogramLayout.findViewById<TextView>(R.id.bar5),
                histogramLayout.findViewById<TextView>(R.id.bar6)
            )

            val dialogContent = context?.let { DialogContentPaper(it) }

            val y100 = histogramLayout.findViewById<TextView>(R.id.y100)
            val y75 = histogramLayout.findViewById<TextView>(R.id.y75)
            val y50 = histogramLayout.findViewById<TextView>(R.id.y50)
            val y25 = histogramLayout.findViewById<TextView>(R.id.y25)

            val maxResult = max(1,histogramResults.max())
            y100.text = ""+maxResult
            y75.text = ""+(maxResult*0.75)
            y50.text = ""+(maxResult*0.50)
            y25.text = ""+(maxResult*0.25)


            histogramResults.forEachIndexed(){index,it->
                Log.d("???","${it}, ${index}")
                numErrorList[index].text= ""+it

                val params = histogramBar[index].layoutParams
                params.height = dpToPx(190*it/maxResult,context);
                histogramBar[index].layoutParams = params

                anim.scaleButtonAnim(histogramBar[index])

                histogramBar[index].setOnClickListener(){
                    scope.launch{
                        if (dialogContent != null) {
                            dialogContent.showContentDialog(R.array.icdas_table)
                        }
                    }
                }

            }
            val dialogConfirmation = DialogConfirmation(context)


            btnSave.setOnClickListener {
                scope.launch {
                    if(saving==false) {
                        saving=true
                        val confirmed =
                            dialogConfirmation.showConfirmDialog("SALVAR O TESTE?")

                        if (!confirmed) {
                            return@launch
                        }

                        val currentDateTime = SimpleDateFormat(
                            "HH:mm dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(Date())

                        val testResult = TestResult(
                            runConfig.user,
                            answerList.size,
                            numAcertos,
                            runConfig.maxTime * 60L,
                            secondsLeft,
                            runConfig.name,
                            currentDateTime,
                            histogramResults
                        );

                        dataStore.addTestResult(testResult)
                        Toast.makeText(
                            context,
                            "Resultado Salvo",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            }

            btnExit.setOnClickListener {
                scope.launch {
                    val confirmed =
                        dialogConfirmation.showConfirmDialog("SAIR PARA O MENU?")

                    if (!confirmed) {
                        return@launch
                    }
                    if (continuation.isActive) {
                        continuation.resume(2)
                    }
                    dialog.dismiss()
                }
            }

            btnAgain.setOnClickListener(){
                scope.launch {
                    val confirmed =
                        dialogConfirmation.showConfirmDialog("INICIAR UM NOVO TESTE?")

                    if (!confirmed) {
                        return@launch
                    }
                    if (continuation.isActive) {
                        continuation.resume(0)
                    }
                    dialog.dismiss()
                }
            }

            continuation.invokeOnCancellation {
                dialog.dismiss()
            }

            dialog.setCanceledOnTouchOutside(false);

            dialog.show()
        }

    fun dpToPx(dp: Int, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }





}