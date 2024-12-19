package com.saltwort.cotizadorgasto.database

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(entities = [EntityRecord::class], version = 1, exportSchema = true)
abstract class AppDatabase: RoomDatabase() {
    abstract fun recordDao(): DaoRecord


}