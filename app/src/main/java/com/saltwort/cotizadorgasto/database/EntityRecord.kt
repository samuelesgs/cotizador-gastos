package com.saltwort.cotizadorgasto.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("records")
data class EntityRecord (
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "balance") var balance: Float = 0f,
    @ColumnInfo(name = "detail") var detail : String = "",
    @ColumnInfo(name = "sign") var sign: String = "+",
    @ColumnInfo(name = "date") var date: String = ""
)

