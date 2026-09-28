package com.example.toothdecaystudyapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.toothdecaystudyapp.anim.Animations
import com.example.toothdecaystudyapp.dialogs.DialogContentPaper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max

class ResultsAdapter(
    private val results: List<TestResult>,
    private val scope: CoroutineScope
) : RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val acertosTV: TextView = view.findViewById(R.id.acertosTV)
        val testTimeTV: TextView = view.findViewById(R.id.testTimeTV)
        val timeRemainingTV: TextView = view.findViewById(R.id.timeTV)
        val taxaPorcentagemTV: TextView = view.findViewById(R.id.taxaAcertoTV)
        val dateTV: TextView = view.findViewById(R.id.dateTV)
        val nameTV: TextView = view.findViewById(R.id.nameTV)
        val histogramLayout = view.findViewById<LinearLayout>(R.id.histogramLayout)

        val showHistogram = view.findViewById<TextView>(R.id.showHistogramBT)
        val histogram = view.findViewById<LinearLayout>(R.id.histogram)
        val expandIcon = view.findViewById<ImageView>(R.id.expandIcon)
        val contentView = view.findViewById<RelativeLayout>(R.id.contentView)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.paper_result_item_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val context = holder.itemView.context

        val anim = Animations(context);

        holder.acertosTV.text = ""+results[position].acertos+"/"+results[position].numCases

        val maxHours = results[position].maxTime / 3600
        val maxMinutes = (results[position].maxTime % 3600) / 60
        val maxSeconds = results[position].maxTime % 60

        holder.testTimeTV.text = String.format("%d:%02d:%02d", maxHours, maxMinutes, maxSeconds)

        val remainingHours = results[position].secondsLeft / 3600
        val remainingMinutes = (results[position].secondsLeft % 3600) / 60
        val remainingSeconds = results[position].secondsLeft % 60

        holder.timeRemainingTV.text = String.format("%d:%02d:%02d", remainingHours, remainingMinutes, remainingSeconds)

        holder.taxaPorcentagemTV.text = "${(results[position].acertos.toFloat()/(results[position].numCases)*100).toInt()}%"

        holder.dateTV.text = results[position].date
        holder.nameTV.text = results[position].name



        var histogramCreated = false

        holder.showHistogram.setOnClickListener(){
            if(holder.histogram.visibility == View.VISIBLE){
                holder.histogram.visibility = View.GONE
                holder.showHistogram.setBackgroundResource(R.drawable.histogram_button_unselected)
            }else{
                if(!histogramCreated){

                    val numErrorList = listOf(
                        holder.histogramLayout.findViewById<TextView>(R.id.numBar0),
                        holder.histogramLayout.findViewById<TextView>(R.id.numBar1),
                        holder.histogramLayout.findViewById<TextView>(R.id.numBar2),
                        holder.histogramLayout.findViewById<TextView>(R.id.numBar3),
                        holder.histogramLayout.findViewById<TextView>(R.id.numBar4),
                        holder.histogramLayout.findViewById<TextView>(R.id.numBar5),
                        holder.histogramLayout.findViewById<TextView>(R.id.numBar6)
                    )

                    val histogramBar = listOf(
                        holder.histogramLayout.findViewById<TextView>(R.id.bar0),
                        holder.histogramLayout.findViewById<TextView>(R.id.bar1),
                        holder.histogramLayout.findViewById<TextView>(R.id.bar2),
                        holder.histogramLayout.findViewById<TextView>(R.id.bar3),
                        holder.histogramLayout.findViewById<TextView>(R.id.bar4),
                        holder.histogramLayout.findViewById<TextView>(R.id.bar5),
                        holder.histogramLayout.findViewById<TextView>(R.id.bar6)
                    )

                    val dialogContent = context?.let { DialogContentPaper(it) }

                    val y100 = holder.histogramLayout.findViewById<TextView>(R.id.y100)
                    val y75 = holder.histogramLayout.findViewById<TextView>(R.id.y75)
                    val y50 = holder.histogramLayout.findViewById<TextView>(R.id.y50)
                    val y25 = holder.histogramLayout.findViewById<TextView>(R.id.y25)

                    val maxResult = max(1,results[position].histogramResults.max())
                    y100.text = ""+maxResult
                    y75.text = ""+(maxResult*0.75)
                    y50.text = ""+(maxResult*0.50)
                    y25.text = ""+(maxResult*0.25)


                    results[position].histogramResults.forEachIndexed(){index,it->
                        Log.d("???","${it}, ${index}")
                        numErrorList[index].text= ""+it

                        val params = histogramBar[index].layoutParams
                        params.height = dpToPx(190*it/maxResult,context);
                        histogramBar[index].layoutParams = params

                        anim.scaleButtonAnim(histogramBar[index])

                        histogramBar[index].setOnClickListener(){
                            scope.launch {
                                if (dialogContent != null) {
                                    dialogContent.showContentDialog(R.array.icdas_table)
                                }
                            }
                        }

                    }
                    histogramCreated=true

                }
                holder.histogram.visibility = View.VISIBLE
                holder.showHistogram.setBackgroundResource(R.drawable.histogram_button_selected)
            }
        }


        holder.expandIcon.setOnClickListener(){
            if(holder.contentView.visibility == View.VISIBLE){
                holder.contentView.visibility = View.GONE
                holder.expandIcon.setImageResource(R.drawable.more_icon)
            }else{
                holder.contentView.visibility = View.VISIBLE;
                holder.expandIcon.setImageResource(R.drawable.less_icon)
            }
        }



    }

    override fun getItemCount(): Int {
        return results.size
    }

    fun dpToPx(dp: Int, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}