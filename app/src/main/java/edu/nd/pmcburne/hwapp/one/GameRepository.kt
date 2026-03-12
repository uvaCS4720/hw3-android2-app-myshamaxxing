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

import android.util.Log


class GameRepository(private val context: Context) {

    private val dao = AppDatabase.getDatabase(context).gameDao()

    suspend fun getGames(date: String, gender: String): List<GameEntity> {
        return if (isOnline()) {
            try {
                val parts = date.split("-")
                Log.d("BBALL", "Fetching: gender=$gender, year=${parts[0]}, month=${parts[1]}, day=${parts[2]}")

                val response = RetrofitInstance.api.getScoreboard(
                    gender = gender,
                    year = parts[0],
                    month = parts[1],
                    day = parts[2]
                )

                val entities = (response.games ?: emptyList()).mapNotNull { wrapper ->
                    val g = wrapper.game ?: return@mapNotNull null
                    val home = g.home ?: return@mapNotNull null
                    val away = g.away ?: return@mapNotNull null

                    val state = when (g.gameState?.lowercase()) {
                        "final" -> "post"
                        "live", "in-progress", "inprogress" -> "in"
                        else -> "pre"
                    }

                    val winnerSide = when {
                        home.winner == true -> "home"
                        away.winner == true -> "away"
                        else -> ""
                    }

                    Log.d("BBALL", "Game: ${away.names?.short} vs ${home.names?.short} | state=$state | finalMsg=${g.finalMessage}")

                    GameEntity(
                        gameId = g.gameID,
                        gameDate = date,
                        gender = gender,
                        homeTeam = home.names?.short ?: home.names?.char6 ?: "Home",
                        awayTeam = away.names?.short ?: away.names?.char6 ?: "Away",
                        homeScore = home.score ?: "0",
                        awayScore = away.score ?: "0",
                        state = state,
                        period = 0,
                        displayClock = g.contestClock ?: g.currentPeriod ?: "",
                        startTime = g.startTime ?: "",
                        winnerSide = winnerSide
                    )

                }


                Log.d("BBALL", "Entities built: ${entities.size}")
                dao.insertGames(entities)
                entities

            } catch (e: Exception) {
                Log.e("BBALL", "API call FAILED: ${e.message}")
                e.printStackTrace()
                dao.getGames(date, gender)
            }
        } else {
            Log.d("BBALL", "No internet — loading from database")
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
