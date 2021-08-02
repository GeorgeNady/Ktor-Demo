package com.george.utiles

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object DateHelper {

    private val date = getCurrentDateTime()
    private val getDateTimeNow = date.toString("yyyy/MM/dd HH:mm:ss")

    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    fun getTimeAgo(createdAt: Long): String {
        val userDateFormat: DateFormat = SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy")
        val dateFormatNeeded: DateFormat = SimpleDateFormat("MM/dd/yyyy HH:MM:SS")
        var date: Date? = null
        date = Date(createdAt)
        var crdate1 = dateFormatNeeded.format(date)

        // Date Calculation
        val dateFormat: DateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
        crdate1 = SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date)

        // get current date time with Calendar()
        val cal = Calendar.getInstance()
        val currenttime = dateFormat.format(cal.time)
        var CreatedAt: Date? = null
        var current: Date? = null
        try {
            CreatedAt = dateFormat.parse(crdate1)
            current = dateFormat.parse(currenttime)
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        // Get msec from each, and subtract.
        val diff = current!!.time - CreatedAt!!.time
        val diffSeconds = diff / 1000
        val diffMinutes = diff / (60 * 1000) % 60
        val diffHours = diff / (60 * 60 * 1000) % 24
        val diffDays = diff / (24 * 60 * 60 * 1000)
        var time: String? = null
        if (diffDays > 0) {
            time = if (diffDays == 1L) {
                "$diffDays day ago "
            } else {
                "$diffDays days ago "
            }
        } else {
            if (diffHours > 0) {
                time = if (diffHours == 1L) {
                    "$diffHours hr ago"
                } else {
                    "$diffHours hrs ago"
                }
            } else {
                if (diffMinutes > 0) {
                    time = if (diffMinutes == 1L) {
                        "$diffMinutes min ago"
                    } else {
                        "$diffMinutes mins ago"
                    }
                } else {
                    if (diffSeconds > 0) {
                        time = "$diffSeconds secs ago"
                    }
                }
            }
        }
        return time!!
    }


}