package com.example.taskcronometer.data

import java.util.Locale

/**
 * Singleton to calculate seconds from input of hour,minute and seconds,
 * and obtain from seconds hh:mm:ss string.
 */
object ParseTimeLeft {

    fun toSeconds(hour: Int, minute: Int, second: Int = 0): Int {
        return hour * 3600 + minute * 60 + second
    }

    fun toHHMM(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        return String.format(Locale.getDefault(),"%02d:%02d", hours, minutes)
    }

    fun toHHMMSS(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secondsLeft = seconds % 60
        return String.format(Locale.getDefault(),"%02d:%02d:%02d", hours, minutes, secondsLeft)
    }

}