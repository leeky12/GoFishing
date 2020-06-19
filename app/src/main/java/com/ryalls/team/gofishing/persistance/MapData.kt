package com.ryalls.team.gofishing.persistance

import androidx.room.ColumnInfo

data class MapData(
    @ColumnInfo(name = "catchID")
    var catchID: Int = 0,
    @ColumnInfo(name = "longitude")
    val longitude: String,
    @ColumnInfo(name = "latitude")
    val latitude: String,
    @ColumnInfo(name = "species")
    val species: String,
    @ColumnInfo(name = "date")
    val date: String
)