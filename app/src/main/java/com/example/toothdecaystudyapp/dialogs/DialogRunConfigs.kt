package com.example.toothdecaystudyapp.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.toothdecaystudyapp.CarieTest
import com.example.toothdecaystudyapp.ConfigurationsActivity
import com.example.toothdecaystudyapp.R
import com.example.toothdecaystudyapp.SavedRunConfigs
import com.example.toothdecaystudyapp.SettingsDataStore
import com.example.toothdecaystudyapp.anim.Animations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DialogRunConfigs(private val context: Context) {

    /*
    * 0 = continue
    * 1 = restartcontext
    * 2 = return to Menu
    *
    *
    * */

    @SuppressLint("ClickableViewAccessibility")
    suspend fun showRunConfigsDialog(scope: CoroutineScope,runConfigsList:MutableList<SavedRunConfigs>,selected:Int): Int =
        suspendCancellableCoroutine { continuation ->

            val view = LayoutInflater.from(context)
                .inflate(R.layout.grid_run_config_dialog_layout, null)

            val dialog = AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create()

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val anim = Animations(context)

            val dataStore = SettingsDataStore(context)

            val btClose = view.findViewById<ImageView>(R.id.close_button)
            val grid = view.findViewById<GridLayout>(R.id.grid)
            val listViews :MutableList<View> = mutableListOf()

            var selectedNum = selected

            val default = createLayoutRunConfig(grid)
            fillLayout(default,"Padrão",10,10)

            listViews.add(default)

            grid.addView(default)


            runConfigsList.forEach(){it->
                val config = createLayoutRunConfig(grid)
                fillLayout(config,it.name,it.numCarieTests,it.maxTime)


                listViews.add(config)
                grid.addView(config)

            }

            setBackSelected(listViews[selectedNum])

            listViews.forEachIndexed(){index,item->
                anim.scaleLessButtonAnim(item)
                item.setOnClickListener {

                    setAllButtonsToUnselected(listViews)

                    setBackSelected(it)

                    val currentIndex = listViews.indexOf(it)

                    CoroutineScope(Dispatchers.Main).launch {
                        selectedNum=currentIndex
                        dataStore.saveConfigSelected(currentIndex)
                    }
                }
                val delRunConfig = item.findViewById<ImageView>(R.id.delBT)
                if(index==0){
                    delRunConfig.visibility=View.INVISIBLE
                }else {

                    val dialogConfirmation = DialogConfirmation(context)
                    anim.scaleLessButtonAnim(delRunConfig)
                    delRunConfig.setOnClickListener {
                        scope.launch {
                            val confirmed =
                                dialogConfirmation.showConfirmDialog("DELETAR "+ runConfigsList[index-1].name+"?")

                            if (!confirmed) {
                                return@launch
                            }
                            val currentIndex = listViews.indexOf(item)

                            Log.d("index", "" + currentIndex)

                            if (currentIndex > 0) {

                                CoroutineScope(Dispatchers.Main).launch {

                                    dataStore.deleteConfig(currentIndex - 1)

                                    Log.d("indexes", "" + currentIndex + " " + selectedNum)

                                    if (currentIndex < selectedNum) {
                                        selectedNum--
                                        dataStore.saveConfigSelected(selectedNum)
                                    } else if (currentIndex == selectedNum) {
                                        selectedNum = 0
                                        dataStore.saveConfigSelected(selectedNum)
                                        setBackSelected(listViews[selectedNum])
                                    }

                                    grid.removeView(item)
                                    listViews.remove(item)
                                }
                            }
                        }
                    }
                }
            }




            anim.scaleButtonAnim(btClose)

            btClose.setOnClickListener(){
                dialog.dismiss()
            }


            dialog.setOnDismissListener {
                if (continuation.isActive) {
                    continuation.resume(0)
                }
            }

            continuation.invokeOnCancellation {
                dialog.dismiss()
            }

            dialog.show()
        }

    fun createLayoutRunConfig(parent:GridLayout):View{

        val includedView = LayoutInflater.from(context).inflate(
            R.layout.item_list_layout,
            parent,
            false
        )


        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = dpToPx(240,context)
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
        }

        includedView.layoutParams = params

        return includedView
    }

    fun fillLayout(layout: View,name:String,numCases:Int,time:Int):View{
        val nameTF = layout.findViewById<TextView>(R.id.nameTF)
        val numCasesTF = layout.findViewById<TextView>(R.id.numCases)
        val timeTF = layout.findViewById<TextView>(R.id.time)

        nameTF.setText(name)
        numCasesTF.setText(""+numCases)
        timeTF.setText(minutesToHHmm(time))

        return layout

    }


    fun dpToPx(dp: Int, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    fun minutesToHHmm(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return String.format("%02d:%02d", hours, minutes)
    }

    fun setAllButtonsToUnselected(list:MutableList<View>){
        list.forEach(){
            setBackUnselected(it)
        }

    }

    fun setBackSelected(view:View){
        view.setBackgroundResource(R.drawable.selected_item_list_back)
        val back1 = view.findViewById<LinearLayout>(R.id.back1)
        val back2 = view.findViewById<LinearLayout>(R.id.back2)
        back1.setBackgroundResource(R.drawable.selected_transparent_border)
        back2.setBackgroundResource(R.drawable.selected_transparent_border)

    }

    fun setBackUnselected(view:View){
        view.setBackgroundResource(R.drawable.item_list_back)
        val back1 = view.findViewById<LinearLayout>(R.id.back1)
        val back2 = view.findViewById<LinearLayout>(R.id.back2)
        back1.setBackgroundResource(R.drawable.unselected_transparent_border)
        back2.setBackgroundResource(R.drawable.unselected_transparent_border)

    }


}