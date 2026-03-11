package edu.nd.pmcburne.hwapp.one.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val gameId: String,
    val gameDate: String,
    val gender: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: String,
    val awayScore: String,
    val state: String,
    val period: Int,
    val displayClock: String,
    val startTime: String,
    val winnerSide: String
)