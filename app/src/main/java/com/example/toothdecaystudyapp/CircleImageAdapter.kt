package com.example.toothdecaystudyapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.toothdecaystudyapp.anim.Animations
import com.example.toothdecaystudyapp.dialogs.DialogPaint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CircleImageAdapter(
    private val carieTest:MutableList<CarieTest>,
    private val result: List<Int>,
    private val context: Context,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<CircleImageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val frame = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.circle_image_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = carieTest.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = carieTest[position].carieImg

        // Load image
        Glide.with(holder.imageView.context)
            .load(url)
            .centerCrop()
            .into(holder.imageView)

        if(result[position]==0)
            holder.frame.setBackgroundResource(R.drawable.circle_correct)
        else
            holder.frame.setBackgroundResource(R.drawable.circle_incorrect)

        val anim = Animations(context)

        anim.scaleButtonAnim(holder.imageView)

        holder.imageView.setOnClickListener {

                onClick(position)


        }
    }
}