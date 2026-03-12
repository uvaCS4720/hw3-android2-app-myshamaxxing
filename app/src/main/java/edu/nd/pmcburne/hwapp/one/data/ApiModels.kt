package edu.nd.pmcburne.hwapp.one.data

data class ApiResponse(
    val games: List<GameWrapper>? = null
)

data class GameWrapper(
    val game: GameData? = null
)

data class GameData(
    val gameID: String = "",
    val away: TeamData? = null,
    val home: TeamData? = null,
    val finalMessage: String? = null,
    val gameState: String? = null,
    val startTime: String? = null,
    val startDate: String? = null,
    val currentPeriod: String? = null,
    val contestClock: String? = null
)

data class TeamData(
    val score: String? = null,
    val names: TeamNames? = null,
    val winner: Boolean? = null
)

data class TeamNames(
    val char6: String? = null,
    val short: String? = null,
    val full: String? = null
)
