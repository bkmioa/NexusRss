package io.github.bkmioa.nexusrss.db

import android.arch.persistence.room.TypeConverter
import java.util.*

class StringArrayConverter {
    @TypeConverter
    fun toString(array: Array<String>): String =
            Arrays.toString(array).removePrefix("[").removeSuffix("]")

    @TypeConverter
    fun toArray(str: String): Array<String> = str.split(",").toTypedArray()
}
