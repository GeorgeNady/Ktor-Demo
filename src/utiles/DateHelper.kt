package com.george.utiles

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

}