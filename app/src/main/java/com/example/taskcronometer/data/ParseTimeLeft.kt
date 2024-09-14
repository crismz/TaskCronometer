package com.example.taskcronometer.data

import java.util.Locale

/**
 * Singleton to calculate seconds from input of hour,minute and seconds,
 * and obtain from seconds hh:mm:ss string.
 */
object ParseTimeLeft {

    fun toMilliSeconds(hour: Int, minute: Int, second: Int = 0): Long {
        return (hour * 3600 + minute * 60 + second) * 1000L
    }

    fun toHHMM(milliSeconds: Long): String {
        val hours = ((milliSeconds / 1000) / 3600)
        val minutes = ((milliSeconds / 1000) % 3600) / 60
        return String.format(Locale.getDefault(),"%02d:%02d", hours, minutes)
    }

    fun toHHMMSS(milliSeconds: Long): String {
        val hours = ((milliSeconds / 1000) / 3600)
        val minutes = ((milliSeconds / 1000) % 3600) / 60
        val seconds = ((milliSeconds / 1000) % 3600) % 60
        return String.format(Locale.getDefault(),"%02d:%02d:%02d", hours, minutes, seconds)
    }

}