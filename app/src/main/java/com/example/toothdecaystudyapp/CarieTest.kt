package com.example.toothdecaystudyapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CarieTest(
    var carieType:Int,
    var carieImg:String
) : Parcelable
