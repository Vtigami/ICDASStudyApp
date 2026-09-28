package com.example.toothdecaystudyapp

import android.os.Bundle
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import com.example.toothdecaystudyapp.anim.Animations
import com.example.toothdecaystudyapp.dialogs.DialogContentPaper
import kotlinx.coroutines.launch

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class RightBookFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_right_book, container, false)
    }

    private lateinit var gestureDetector: GestureDetector

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gestureDetector = GestureDetector(requireContext(),
            object : GestureDetector.SimpleOnGestureListener() {

                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {

                    val diffX = e2.x - (e1?.x ?: 0f)



                    if (diffX > 100) {
                        swipeRight()
                        return true
                    }

                    return false
                }
            })

        view.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }


        val anim = Animations(requireContext())

        val previousButton = view.findViewById<ImageView>(R.id.previousPageButton)

        anim.scaleButtonAnim(previousButton)

        previousButton.setOnClickListener(){
            swipeRight()
        }

        val listButtons = listOf(R.id.estagio3Button,R.id.estagio4Button,R.id.estagio5Button,R.id.estagio6Button)

        val dialogContent = context?.let { DialogContentPaper(it) }

        listButtons.forEachIndexed(){index, it ->
            view.findViewById<LinearLayout>(it).setOnClickListener(){
                lifecycleScope.launch {
                    if (dialogContent != null) {
                        dialogContent.showContentDialog(index+4)
                    }
                }
            }
        }

    }

    private fun swipeRight() {
        parentFragmentManager.popBackStack()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RightBookFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RightBookFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}