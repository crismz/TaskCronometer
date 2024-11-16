package com.example.taskcronometer.utilities

import java.util.Locale

/**
 * Helper used for conversions of time and some calculations
 */
object TimeHelper {

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

    fun obtainActualTimeRunning(now: Long, timeRunning: Long, lastTimeResumed: Long): Long {
        val timePass: Long =  now - lastTimeResumed
        val actualTimeRunning = timeRunning + timePass
        return actualTimeRunning
    }

    fun obtainHours(milliSeconds: Long): Int {
        return ((milliSeconds / 1000) / 3600).toInt()
    }

    fun obtainMinutes(milliSeconds: Long): Int {
        return (((milliSeconds / 1000) % 3600) / 60).toInt()
    }

}