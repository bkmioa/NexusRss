package io.github.bkmioa.nexusrss.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.bkmioa.nexusrss.model.DownloadNodeModel

@Dao
interface DownloadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdateNode(node: DownloadNodeModel)

    @Query("select * from download_node")
    fun getAll(): List<DownloadNodeModel>

    @Query("select * from download_node")
    fun getAllLiveData(): LiveData<List<DownloadNodeModel>>

    @Delete
    fun delete(node: DownloadNodeModel)
}