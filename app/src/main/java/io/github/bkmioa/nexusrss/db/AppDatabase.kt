package io.github.bkmioa.nexusrss.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.bkmioa.nexusrss.model.Option
import io.github.bkmioa.nexusrss.model.Tab

@Database(version = AppDatabase.DB_VERSION, entities = [Tab::class])
@TypeConverters(StringArrayConverter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val DB_NAME = "nexus-rss"
        const val DB_VERSION = 2
    }

    abstract fun appDao(): AppDao
}