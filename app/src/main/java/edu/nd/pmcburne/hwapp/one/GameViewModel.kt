package edu.nd.pmcburne.hwapp.one

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.nd.pmcburne.hwapp.one.data.GameEntity
import edu.nd.pmcburne.hwapp.one.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application)

    private val todayDate: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return sdf.format(Date())
        }

    private val _selectedDate = MutableStateFlow(todayDate)
    val selectedDate: StateFlow<String> = _selectedDate

    private val _gender = MutableStateFlow("men")
    val gender: StateFlow<String> = _gender

    private val _games = MutableStateFlow<List<GameEntity>>(emptyList())
    val games: StateFlow<List<GameEntity>> = _games

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline

    init {
        fetchGames()
    }

    fun fetchGames() {
        viewModelScope.launch {
            _isLoading.value = true
            _games.value = repository.getGames(_selectedDate.value, _gender.value)
            _isLoading.value = false
        }
    }

    fun onDateSelected(newDate: String) {
        _selectedDate.value = newDate
        fetchGames()
    }

    fun onGenderToggle(newGender: String) {
        _gender.value = newGender
        fetchGames()
    }
}
