package io.github.bkmioa.nexusrss.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.model.DownloadNodeModel
import io.github.bkmioa.nexusrss.model.Tab

@Database(exportSchema = false, version = AppDatabase.DB_VERSION, entities = [Tab::class, DownloadNodeModel::class])
@TypeConverters(StringArrayConverter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val DB_NAME = "nexus-rss"
        const val DB_VERSION = 4

        fun initData(database: SupportSQLiteDatabase) {
            database.execSQL(
                "INSERT INTO tab (title,path, options,`order`,isShow,columnCount) VALUES\n" +
                        "('MOVIE','movie.php','',0,1,1),\n" +
                        "('TV','torrents.php','cat402,cat403,cat435,cat438',1,1,2),\n" +
                        "('ANIME','torrents.php','cat405',2,1,1),\n" +
                        "('MUSIC','music.php','',3,1,1)"
            )
        }
        fun migrations() = arrayOf(
            object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE TAB ADD COLUMN columnCount INT NOT NULL DEFAULT 1")
                }
            },
            object : Migration(2, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("CREATE TABLE IF NOT EXISTS `download_node` (`name` TEXT NOT NULL, `host` TEXT NOT NULL, `userName` TEXT NOT NULL, `password` TEXT NOT NULL, `type` TEXT NOT NULL, `defaultPath` TEXT, `id` INTEGER PRIMARY KEY AUTOINCREMENT)")

                    if (Settings.REMOTE_USERNAME.isNotBlank() && Settings.REMOTE_URL.isNotBlank()) {
                        database.execSQL(
                            "INSERT OR REPLACE INTO `download_node` (`name`,`host`,`userName`,`password`,`type`) VALUES (?,?,?,?,?)",
                            arrayOf(DownloadNodeModel.TYPE_UTORRENT, Settings.REMOTE_URL, Settings.REMOTE_USERNAME, Settings.REMOTE_PASSWORD, DownloadNodeModel.TYPE_UTORRENT)
                        )
                    }
                }
            },
            object : Migration(3, 4) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("DELETE FROM tab")
                    database.execSQL("ALTER TABLE TAB ADD COLUMN path TEXT NOT NULL")
                    initData(database)
                }

            }
        )
    }

    abstract fun appDao(): AppDao
    abstract fun downloadDao(): DownloadDao
}