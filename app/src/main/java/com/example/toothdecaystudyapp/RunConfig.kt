package com.example.toothdecaystudyapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RunConfig(
    var name:String,
    var carieTests : MutableList<CarieTest>,
    var maxTime :Int,
    var user:String
) : Parcelable;

@Parcelize
data class SavedRunConfigs(
    var name:String,
    var numCarieTests : Int,
    var maxTime :Int
) : Parcelable;

@Parcelize
data class TestResult(
    var name:String,
    var numCases:Int,
    var acertos:Int,
    var maxTime: Long,
    var secondsLeft: Long,
    var testeName:String,
    var date:String,
    var histogramResults:MutableList<Int>
):Parcelable;

