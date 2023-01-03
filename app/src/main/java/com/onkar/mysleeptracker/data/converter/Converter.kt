package com.onkar.mysleeptracker.data.converter

import androidx.room.TypeConverter
import java.text.SimpleDateFormat

/**
 * Created by Onkar Pendse  pendseomkar92@gmail.com
 *  on 01-08-2022.
 *  For More Information Contact me!!!.
 *          !!  Thank You  !!
 */
class Converter {

    @TypeConverter
    fun fromTimestamp(timeStamp: Long?): String? {
        return timeStamp?.let { FORMATTER.format(timeStamp) }
    }

    @TypeConverter
    fun dateToTimestamp(timeStamp: String?): Long? {
        return timeStamp?.let { FORMATTER.parse(it)?.time }
    }

    companion object {

        val FORMATTER = SimpleDateFormat("yyy-MM-dd")
    }

}