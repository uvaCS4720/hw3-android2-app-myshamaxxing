package edu.nd.pmcburne.hwapp.one

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.nd.pmcburne.hwapp.one.data.GameEntity
import edu.nd.pmcburne.hwapp.one.GameViewModel
import java.text.SimpleDateFormat
import java.util.*

val Orange      = Color(0xFFFF6B35)
val TealFinal   = Color(0xFF14B8A6)
val GreenLive   = Color(0xFF22C55E)
val WinnerBg    = Color(0xFFFFF3E0)

//Main Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(viewModel: GameViewModel) {
    val games        by viewModel.games.collectAsState()
    val isLoading    by viewModel.isLoading.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val gender       by viewModel.gender.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                        viewModel.onDateSelected(sdf.format(Date(millis)))
                    }
                    showDatePicker = false
                }) { Text("OK", color = Orange) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = Orange)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Orange,
                    todayDateBorderColor = Orange
                )
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Orange)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Text(
                text = "College Basketball\nScores",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            OutlinedButton(
                onClick = { viewModel.fetchGames() },
                modifier = Modifier.align(Alignment.CenterEnd),
                border = BorderStroke(1.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier.size(15.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text("Refresh", fontSize = 13.sp)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("Select Date", fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = formatDisplayDate(selectedDate),
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Default.DateRange, null, tint = Orange)

                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color.Black,
                            disabledBorderColor = Orange,
                            disabledLeadingIconColor = Orange
                        )
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showDatePicker = true }
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text("League", fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                ) {
                    listOf(
                        "men"   to "Men's Basketball",
                        "women" to "Women's Basketball"
                    ).forEach { (value, label) ->
                        val selected = gender == value
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (selected) Orange else Color.White)
                                .clickable { viewModel.onGenderToggle(value) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (selected) Color.White else Color.DarkGray,
                                fontSize = 13.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        color = Orange,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                games.isEmpty() -> {
                    Text(
                        text = "No games found for this date.",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(games) { game ->
                            GameCard(game = game, gender = gender)
                        }
                        item { Spacer(Modifier.height(12.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun GameCard(game: GameEntity, gender: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            when (game.state) {
                "pre"  -> UpcomingContent(game)
                "in"   -> LiveContent(game, gender)
                "post" -> FinalContent(game, gender)
            }
        }
    }
}

@Composable
fun UpcomingContent(game: GameEntity) {
    Text(
        text = game.startTime,
        color = Orange,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.End
    )
    Spacer(Modifier.height(8.dp))
    TeamRow(name = game.awayTeam, label = "Away", score = null, winner = false)
    Spacer(Modifier.height(6.dp))
    TeamRow(name = game.homeTeam, label = "Home", score = null, winner = false)
}

@Composable
fun LiveContent(game: GameEntity, gender: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Badge(containerColor = GreenLive) {
            Text(
                "LIVE",
                color = Color.White,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "${periodLabel(game.period, gender)} • ${game.displayClock}",
            fontSize = 13.sp,
            color = Color.DarkGray
        )
    }
    Spacer(Modifier.height(10.dp))
    TeamRow(name = game.awayTeam, label = "Away", score = game.awayScore, winner = false)
    Spacer(Modifier.height(6.dp))
    TeamRow(name = game.homeTeam, label = "Home", score = game.homeScore, winner = false)
}

@Composable
fun FinalContent(game: GameEntity, gender: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Badge(containerColor = TealFinal) {
            Text(
                "FINAL",
                color = Color.White,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(periodLabel(game.period, gender), fontSize = 13.sp, color = Color.DarkGray)
    }
    Spacer(Modifier.height(10.dp))
    TeamRow(
        name = game.awayTeam,
        label = "Away",
        score = game.awayScore,
        winner = game.winnerSide == "away"
    )
    Spacer(Modifier.height(6.dp))
    TeamRow(
        name = game.homeTeam,
        label = "Home",
        score = game.homeScore,
        winner = game.winnerSide == "home"
    )

    if (game.winnerSide.isNotEmpty()) {
        val winnerName = if (game.winnerSide == "home") game.homeTeam else game.awayTeam
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = Color(0xFFF0F0F0))
        Spacer(Modifier.height(6.dp))
        Text(
            text = "$winnerName wins!",
            color = Orange,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TeamRow(name: String, label: String, score: String?, winner: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (winner) WinnerBg else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Initial avatar circle
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Orange.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.first().toString(),
                color = Orange,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
        if (score != null) {
            Text(
                text = score,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = if (winner) Orange else Color.Black
            )
        }
    }
}

fun formatDisplayDate(dateStr: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = sdf.parse(dateStr) ?: return dateStr
        SimpleDateFormat("MMMM d, yyyy", Locale.US).format(date)
    } catch (e: Exception) { dateStr }
}

fun periodLabel(period: Int, gender: String): String {
    return if (gender == "women") {
        when (period) { 1 -> "1st Qtr"; 2 -> "2nd Qtr"; 3 -> "3rd Qtr"; 4 -> "4th Qtr"; else -> "${period}Q" }
    } else {
        when (period) { 1 -> "1st Half"; 2 -> "2nd Half"; else -> "${period}H" }
    }
}
