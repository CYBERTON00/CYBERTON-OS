package com.example.data.repository

import com.example.data.database.TechPhaseDao
import com.example.data.entity.TechPhase
import kotlinx.coroutines.flow.Flow

class TechPhaseRepository(private val techPhaseDao: TechPhaseDao) {
    val allPhases: Flow<List<TechPhase>> = techPhaseDao.getAllPhases()

    suspend fun insertPhases(phases: List<TechPhase>) {
        techPhaseDao.insertPhases(phases)
    }

    suspend fun updatePhase(phase: TechPhase) {
        techPhaseDao.updatePhase(phase)
    }

    suspend fun insertOrReplace(phase: TechPhase) {
        techPhaseDao.insertOrReplace(phase)
    }
}
