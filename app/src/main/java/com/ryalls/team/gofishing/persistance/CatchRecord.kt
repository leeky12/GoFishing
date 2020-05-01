/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ryalls.team.gofishing.persistance

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A basic class representing an entity that is a row in a one-column database table.
 *
 * @ Entity - You must annotate the class as an entity and supply a table name if not class name.
 * @ PrimaryKey - You must identify the primary key.
 * @ ColumnInfo - You must supply the column name if it is different from the variable name.
 *
 * See the documentation for the full rich set of annotations.
 * https://developer.android.com/topic/libraries/architecture/room.html
 */

@Entity(tableName = "word_table")
data class CatchRecord(@ColumnInfo(name = "species") var species: String) {
    @PrimaryKey(autoGenerate = true)
    var catchID: Int = 0

    @ColumnInfo(name = "location")
    var location: String = ""

    @ColumnInfo(name = "date")
    var date: String = ""

    @ColumnInfo(name = "weight")
    var weight: String = ""

    @ColumnInfo(name = "length")
    var length: String = ""

    @ColumnInfo(name = "comments")
    var comments: String = ""

    @ColumnInfo(name = "lure")
    var lure: String = ""

    @ColumnInfo(name = "structure")
    var structure: String = ""

    @ColumnInfo(name = "conditions")
    var conditions: String = ""

    @ColumnInfo(name = "depth")
    var depth: String = ""

    @ColumnInfo(name = "hook")
    var hook: String = ""

    @ColumnInfo(name = "groundBait")
    var groundBait: String = ""

    @ColumnInfo(name = "boatspeed")
    var boatspeed : String = ""

    @ColumnInfo(name = "tides")
    var tides: String = ""

    @ColumnInfo(name = "rod")
    var rod: String = ""

    @ColumnInfo(name = "reel")
    var reel: String = ""

    @ColumnInfo(name = "line")
    var line: String = ""
}

