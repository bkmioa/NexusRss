package io.github.bkmioa.nexusrss.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.bkmioa.nexusrss.model.DownloadNodeModel
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdateNode(node: DownloadNodeModel)

    @Query("select * from download_node where id = :id")
    suspend fun getOne(id: Long): DownloadNodeModel

    @Query("select * from download_node")
    fun getAll(): List<DownloadNodeModel>

    @Query("select * from download_node")
    fun getAllLiveData(): LiveData<List<DownloadNodeModel>>

    @Query("select * from download_node")
    fun getAllFlow(): Flow<List<DownloadNodeModel>>

    @Delete
    fun delete(node: DownloadNodeModel)
}