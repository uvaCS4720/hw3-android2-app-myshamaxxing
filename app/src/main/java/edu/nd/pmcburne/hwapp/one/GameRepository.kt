package edu.nd.pmcburne.hwapp.one

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import edu.nd.pmcburne.hwapp.one.data.AppDatabase
import edu.nd.pmcburne.hwapp.one.data.GameEntity
import edu.nd.pmcburne.hwapp.one.RetrofitInstance
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class GameRepository(private val context: Context) {

    private val dao = AppDatabase.getDatabase(context).gameDao()

    suspend fun getGames(date: String, gender: String): List<GameEntity> {
        return if (isOnline()) {
            try {
                val parts = date.split("-")
                val response = RetrofitInstance.api.getScoreboard(
                    gender = gender,
                    year = parts[0],
                    month = parts[1],
                    day = parts[2]
                )

                val entities = response.events.mapNotNull { event ->
                    val comp = event.competitions.firstOrNull() ?: return@mapNotNull null
                    val home = comp.competitors.find { it.homeAway == "home" } ?: return@mapNotNull null
                    val away = comp.competitors.find { it.homeAway == "away" } ?: return@mapNotNull null
                    val status = comp.status

                    val winnerSide = when {
                        home.winner == true -> "home"
                        away.winner == true -> "away"
                        else -> ""
                    }

                    val startTime = try {
                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.US)
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                        val parsed: Date = sdf.parse(comp.date) ?: Date()
                        val outSdf = SimpleDateFormat("h:mm a", Locale.US)
                        outSdf.timeZone = TimeZone.getDefault()
                        outSdf.format(parsed)
                    } catch (e: Exception) { "" }

                    GameEntity(
                        gameId = event.id,
                        gameDate = date,
                        gender = gender,
                        homeTeam = home.team.displayName,
                        awayTeam = away.team.displayName,
                        homeScore = home.score ?: "0",
                        awayScore = away.score ?: "0",
                        state = status.type.state,
                        period = status.period,
                        displayClock = status.displayClock,
                        startTime = startTime,
                        winnerSide = winnerSide
                    )
                }

                dao.insertGames(entities)
                entities
            } catch (e: Exception) {
                dao.getGames(date, gender)
            }
        } else {
            dao.getGames(date, gender)
        }
    }

    private fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
