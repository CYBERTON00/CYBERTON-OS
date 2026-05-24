package com.example.data.database

import androidx.room.*
import com.example.data.entity.TechPhase
import kotlinx.coroutines.flow.Flow

@Dao
interface TechPhaseDao {
    @Query("SELECT * FROM tech_phases ORDER BY id ASC")
    fun getAllPhases(): Flow<List<TechPhase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhases(phases: List<TechPhase>)

    @Update
    suspend fun updatePhase(phase: TechPhase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(phase: TechPhase)
}
