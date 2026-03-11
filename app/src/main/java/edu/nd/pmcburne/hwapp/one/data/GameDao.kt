package edu.nd.pmcburne.hwapp.one.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameEntity>)

    @Query("SELECT * FROM games WHERE gameDate = :date AND gender = :gender")
    suspend fun getGames(date: String, gender: String): List<GameEntity>
}