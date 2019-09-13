package io.github.bkmioa.nexusrss.db

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.bkmioa.nexusrss.model.Tab
import io.reactivex.Single

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

}