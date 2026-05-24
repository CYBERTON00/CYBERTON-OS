package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tech_phases")
data class TechPhase(
    @PrimaryKey val id: Int,
    val name: String,
    val subtitle: String,
    val description: String,
    val isCompleted: Boolean = false,
    val progress: Int = 0,
    val technologies: String = "", // Comma-separated or simple string representation
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
