package com.saltwort.cotizadorgasto.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoRecord {
    @Query("SELECT * FROM records")
    suspend fun getAll(): List<EntityRecord>

    @Query("SELECT * FROM records WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<EntityRecord>

    @Query("SELECT * FROM records WHERE uid = :uid")
    fun findByName(uid : Int): EntityRecord?

    @Insert
    suspend fun insertAll(record: EntityRecord)

    @Query("SELECT * FROM records WHERE date BETWEEN :startDate AND :endDate")
    fun getRecordsByDateRange(startDate: String, endDate: String): List<EntityRecord>


    @Delete
    fun delete(user: EntityRecord)
}