package io.github.bkmioa.nexusrss.db

import androidx.room.TypeConverter

class StringArrayConverter {
    @TypeConverter
    fun toString(array: Array<String>) = array.sortedArray().joinToString(",")

    @TypeConverter
    fun toArray(str: String): Array<String> = str.split(",").toTypedArray().sortedArray()
}
