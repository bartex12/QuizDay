package com.bartex.quizday.room.dao

import androidx.room.*
import com.bartex.quizday.room.tables.RoomState

@Dao
interface StateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(state: RoomState)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg states: RoomState)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stats:List<RoomState>)

    @Update
    fun update(state: RoomState)
    @Update
    fun update(vararg state: RoomState)
    @Update
    fun update(states:List<RoomState>)

    @Delete
    fun delete(state: RoomState)
    @Delete
    fun delete(vararg state: RoomState)
    @Delete
    fun delete(states:List<RoomState>)

    @Query("SELECT*FROM RoomState")
    fun getAll():List<RoomState>

    @Query("SELECT*FROM RoomState WHERE region = :region")
    fun getRegionStates(region:String):List<RoomState>

    @Query("SELECT*FROM RoomState WHERE name = :name")
    fun getFlagByState(name:String):RoomState

    @Query("SELECT*FROM RoomState WHERE nameRus = :nameRus")
    fun getFlagByStateRus(nameRus:String):RoomState

    @Query("SELECT flag FROM RoomState WHERE name = :name")
    fun getFlagOnlyByState(name:String):String

    @Query("SELECT flag FROM RoomState WHERE nameRus = :nameRus")
    fun getFlagOnlyByStateRus(nameRus:String):String
}