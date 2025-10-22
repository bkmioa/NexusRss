package io.github.bkmioa.nexusrss.db

import androidx.room.TypeConverter

class SetConverter {
    @TypeConverter
    fun toString(set: Set<String>) = set.joinToString(",")

    @TypeConverter
    fun toList(str: String): Set<String> = if (str.isEmpty()) {
        emptySet()
    } else {
        str.split(",").toSet()
    }
}
