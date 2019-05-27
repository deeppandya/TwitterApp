package com.example.twitterapp

import android.content.Context
import android.graphics.Paint
import android.util.DisplayMetrics

object Utils {

    const val NEXT_RESULTS = "next_results"
    const val SEARCH_METADATA = "search_metadata"
    const val TWEET = "tweet"
    const val STATUSES = "statuses"
    val LANGUAGE = hashMapOf("français" to "fr", "english" to "en","Français" to "fr", "English" to "en")

    fun getDisplayMetrics(context: Context): DisplayMetrics {
        val resources = context.resources
        return resources.displayMetrics
    }

    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (getDisplayMetrics(context).densityDpi / 160f)
    }

    fun getTextWidth(paint: Paint, text: String): Float {
        return paint.measureText(text)
    }
}
