package io.github.bkmioa.nexusrss.db

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.bkmioa.nexusrss.model.Tab
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM tab")
    fun getAllTab(): LiveData<Array<Tab>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTab(vararg tabs: Tab): Array<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateTab(vararg tabs: Tab): Array<Long>

    @Delete
    fun deleteTab(tab: Tab): Int

    @Query("SELECT * FROM tab where isShow = 1 order by `order`")
    fun getActivateTabs(): Flow<List<Tab>>

    @Query("SELECT * FROM tab order by `order`")
    fun getAllTabFlow(): Flow<List<Tab>>

}