package edu.nd.pmcburne.hwapp.one.data

data class ApiResponse(
    val events: List<Event>
)

data class Event(
    val id: String,
    val date: String,
    val competitions: List<Competition>
)

data class Competition(
    val date: String,
    val competitors: List<Competitor>,
    val status: CompetitionStatus
)

data class Competitor(
    val homeAway: String,       // "home" or "away"
    val winner: Boolean?,
    val team: Team,
    val score: String?
)

data class Team(
    val displayName: String,
    val shortDisplayName: String?
)

data class CompetitionStatus(
    val displayClock: String,   // e.g. "17:23"
    val period: Int,            // half or quarter
    val type: StatusType
)

data class StatusType(
    val name: String,
    val state: String,          // "pre"=upcoming, "in"=live, "post"=final
    val completed: Boolean,
    val description: String     // "Scheduled", "In Progress", "Final"
)
