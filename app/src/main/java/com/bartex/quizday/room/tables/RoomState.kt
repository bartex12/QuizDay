package com.bartex.quizday.room.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

/* RoomState будет представлять таблицу State*/
@Entity
class  RoomState(
    var capital :String,
    var flag :String,
    @PrimaryKey var name :String,
    var region :String,
    var nameRus:String,
    var capitalRus:String,
    var regionRus:String,
    var mistake:Int = 0
)