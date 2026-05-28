package com.ledger.app.data.db.dao

import androidx.room.*
import com.ledger.app.data.db.entity.RecurringConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringConfigDao {

    @Query("SELECT * FROM recurring_configs WHERE isActive = 1 ORDER BY nextEpochDay ASC")
    fun getActive(): Flow<List<RecurringConfigEntity>>

    @Query("SELECT * FROM recurring_configs WHERE id = :id")
    suspend fun getById(id: String): RecurringConfigEntity?

    @Query("SELECT * FROM recurring_configs WHERE nextEpochDay <= :todayEpochDay AND isActive = 1")
    suspend fun getDue(todayEpochDay: Long): List<RecurringConfigEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(config: RecurringConfigEntity)

    @Update
    suspend fun update(config: RecurringConfigEntity)

    @Query("UPDATE recurring_configs SET isActive = 0 WHERE id = :id")
    suspend fun deactivate(id: String)

    @Delete
    suspend fun delete(config: RecurringConfigEntity)
}
