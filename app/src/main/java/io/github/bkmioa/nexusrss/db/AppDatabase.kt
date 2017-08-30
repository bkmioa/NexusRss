package io.github.bkmioa.nexusrss.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import io.github.bkmioa.nexusrss.model.Option
import io.github.bkmioa.nexusrss.model.Tab

@Database(version = AppDatabase.DB_VERSION,
        entities = arrayOf(
                Tab::class
        ))
@TypeConverters(StringArrayConverter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val DB_NAME = "nexus-rss"
        const val DB_VERSION = 1
    }

    abstract fun appDao(): AppDao
}