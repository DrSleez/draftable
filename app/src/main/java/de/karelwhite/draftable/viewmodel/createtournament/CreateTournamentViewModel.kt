package de.karelwhite.draftable.viewmodel.createtournament


import androidx.compose.ui.unit.max
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.karelwhite.draftable.domain.model.Player
import de.karelwhite.draftable.domain.model.Tournament
import de.karelwhite.draftable.domain.repository.HostRepository
import de.karelwhite.draftable.domain.repository.TournamentRepository
import de.karelwhite.draftable.viewmodel.settings.SettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.math.*

@HiltViewModel
class CreateTournamentViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepository,
    private val hostRepository: HostRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateTournamentState())
    val uiState = _uiState.asStateFlow()

    init {
        createTournamentId()
    }

    fun onEvent(event: CreateTournamentEvent) {
        when (event) {
            CreateTournamentEvent.LoadInitialCreateTournament -> {
                createTournamentId()
            }

            CreateTournamentEvent.SaveTournament -> {
                if (checkTournamentData()) {
                    saveTournament()
                }
            }

            is CreateTournamentEvent.SetPlayers -> {
                _uiState.update { currentState ->
                    currentState.copy(players = event.players)
                }
            }

            is CreateTournamentEvent.SetTournamentName -> {
                _uiState.update { currentState ->
                    currentState.copy(tournamentName = event.name)
                }
            }

            CreateTournamentEvent.ToastMessageShown -> {
                _uiState.update { it.copy(toastMessage = null) }
            }

            is CreateTournamentEvent.AddPlayerByName -> {
                if (event.playerName.isBlank()) {
                    _uiState.update { it.copy(toastMessage = "Spielername darf nicht leer sein.") }
                    return
                }
                if (_uiState.value.players.any { it.name.equals(event.playerName, ignoreCase = true) }) {
                    _uiState.update { it.copy(toastMessage = "'${event.playerName}' ist bereits in der Liste.") }
                    return
                }

                val newPlayer = Player(name = event.playerName, tournamentId = _uiState.value.createdTournamentId) // Erstelle ein neues Player-Objekt

                _uiState.update { currentState ->
                    currentState.copy(players = currentState.players + newPlayer)
                }
                calculateNumberOfRounds()
            }
            is CreateTournamentEvent.RemovePlayer -> {
                _uiState.update { currentState ->
                    val updatedPlayers = currentState.players.toMutableList()
                    updatedPlayers.remove(event.player)
                    currentState.copy(players = updatedPlayers.toList())
                }
                calculateNumberOfRounds()
            }
        }
    }

    private fun calculateNumberOfRounds() {
        val numberOfPlayers = _uiState.value.players.size
        var rounds = 0

        if (numberOfPlayers < 4) {
            _uiState.update { currentState ->
                    currentState.copy(numberOfRounds = "0", error = "Es werden mindestens 4 Spieler benötigt. Aktuell: $numberOfPlayers")
                }
        } else if (numberOfPlayers <= 8) {
            rounds = 3
        } else {
            val baseRounds = ceil(log2(numberOfPlayers.toDouble())).toInt()
            rounds = max(4, baseRounds + 1)
        }
        _uiState.update { currentState ->
            currentState.copy(numberOfRounds = rounds.toString())
        }
    }

    private fun saveTournament() {
        viewModelScope.launch {
            try {
                val hostId = hostRepository.getHost()?.id
                if (hostId == null) {
                    _uiState.update { it.copy(toastMessage = "Es konnte kein Host gefunden werden.") }
                    return@launch
                }
                val newTournament = Tournament(id = _uiState.value.createdTournamentId, name = _uiState.value.tournamentName, players = _uiState.value.players, hostPlayerId = hostId)
                tournamentRepository.createTournament(newTournament)
                _uiState.update { it.copy(toastMessage = "Turnier erfolgreich gespeichert.") }
            } catch (e: Exception){
                _uiState.update { it.copy(toastMessage = "Fehler beim Speichern des Turniers: ${e.message}") }
            }
        }
    }

    private fun checkTournamentData(): Boolean {
        val currentTournamentName = _uiState.value.tournamentName
        val currentPlayers = _uiState.value.players

        if (currentPlayers.isEmpty()) {
            return false
        }

        if (currentTournamentName.isBlank()) {
            _uiState.update { it.copy(toastMessage = "Bitte gib einen Turniernamen ein.") }
            return false
        }

        if (currentPlayers.size < 4) {
            _uiState.update { it.copy(toastMessage = "Es werden mindestens 4 Spieler benötigt. Aktuell: ${currentPlayers.size}") }
            return false
        }

        _uiState.update { it.copy(error = null) }
        println("Alle felder erfolgreich")
        return true
    }

    private fun createTournamentId() {
        if (_uiState.value.createdTournamentId.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(createdTournamentId = UUID.randomUUID().toString(), error = null)
            }
        }
    }
}