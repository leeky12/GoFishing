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

import androidx.lifecycle.LiveData
import androidx.room.*


/**
 * The Room Magic is in this file, where you map a Java method call to an SQL query.
 *
 * When you are using complex data types, such as Date, you have to also supply type converters.
 * To keep this example basic, no types that require type converters are used.
 * See the documentation at
 * https://developer.android.com/topic/libraries/architecture/room.html#type-converters
 */

@Dao
interface CatchDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from catch_table ORDER BY catchID DESC")
    fun getAllCatchByCatchID(): LiveData<List<CatchRecord>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(catchRecord: CatchRecord)

    @Update
    fun update(catchRecord: CatchRecord)

    @Query("DELETE FROM catch_table")
    fun deleteAll()

    @Query("DELETE FROM catch_table where catchID = :catchID")
    fun deleteRecord(catchID: Int)

    @Query("SELECT * FROM catch_table where catchId = :catchID")
    fun getRecord(catchID: Int): CatchRecord

    @Query("SELECT catchID, date, latitude, longitude, species  FROM catch_table")
    fun getCatchLocations(): List<MapData>

    @Query("SELECT species FROM catch_table")
    fun getSpeciesList(): List<String>

}
