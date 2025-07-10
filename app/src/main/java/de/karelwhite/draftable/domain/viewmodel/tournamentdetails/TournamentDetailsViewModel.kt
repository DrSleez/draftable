package de.karelwhite.draftable.domain.viewmodel.tournamentdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.karelwhite.draftable.domain.businesslogic.RoundGenerator
import de.karelwhite.draftable.domain.repository.TournamentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TournamentDetailsViewModel @Inject constructor(
    private val tournamentRepository : TournamentRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(TournamentDetailsState())
    val uiState: StateFlow<TournamentDetailsState> = _uiState.asStateFlow()

    private val tournamentId: String? = savedStateHandle.get<String>("tournamentId")

    fun onEvent(event: TournamentDetailsEvent){
        when(event){
            TournamentDetailsEvent.GoNextRound -> {
                generateNextRoundAndUpdateTournament()
            }
            TournamentDetailsEvent.LoadTournamentDetails -> {
                loadTournament(tournamentId!!)
            }
            TournamentDetailsEvent.StartTournament -> {
                startTournamentAndGenerateFirstRound()
            }
            TournamentDetailsEvent.HideLeaderboard -> {
                _uiState.update { it.copy(isLeaderboardVisible = false) }
            }
            TournamentDetailsEvent.ShowLeaderboard -> {
                _uiState.update { it.copy(isLeaderboardVisible = true) }
            }
            is TournamentDetailsEvent.SubmitMatchResult -> {
                val matchToUpdate = _uiState.value.selectedMatchForDialog
                if (matchToUpdate != null) {
                    setMatchResult( // Die setMatchResult Methode muss auch draws: Int akzeptieren
                        matchId = matchToUpdate.id,
                        p1Wins = event.player1Wins,
                        p2Wins = event.player2Wins,
                        draws = event.draws
                    )
                }
                _uiState.update {
                    it.copy(
                        isMatchResultDialogVisible = false,
                        selectedMatchForDialog = null
                    )
                }
            }
            TournamentDetailsEvent.HideMatchResultDialog -> {
                _uiState.update {
                    it.copy(
                        isMatchResultDialogVisible = false,
                        selectedMatchForDialog = null // Wichtig: Zurücksetzen
                    )
                }
            }
            is TournamentDetailsEvent.ShowMatchResultDialog ->  {
                // Prüfen, ob das Match gültig für die Ergebniseingabe ist
                if (!event.match.isFinished && event.match.player2Id != null) {
                    _uiState.update {
                        it.copy(
                            isMatchResultDialogVisible = true,
                            selectedMatchForDialog = event.match
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isMatchResultDialogVisible = false,
                            selectedMatchForDialog = null
                        )
                    }
                }
            }
        }
    }

    init {
        if (tournamentId != null) {
            loadTournament(tournamentId)
        } else {
            // Handle den Fall, dass keine ID übergeben wurde (sollte nicht passieren bei korrekter Navigation)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Fehler: Turnier-ID nicht gefunden."
                )
            }
        }
    }

    private fun startTournamentAndGenerateFirstRound() {
        val currentTournament = _uiState.value.tournament ?: return
        val tournamentWithFirstRound = RoundGenerator.generateFirstRound(currentTournament)
        println(tournamentWithFirstRound)

        viewModelScope.launch {
            try {
                tournamentRepository.updateTournament(tournamentWithFirstRound)
                _uiState.update {
                    it.copy(
                        tournament = tournamentWithFirstRound,
                        error = null,
                        isLoading = false // Ladezustand beenden
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Fehler beim Starten des Turniers und Erstellen der ersten Runde: ${e.message}", isLoading = false)
                }
            }
        }
    }

    private fun generateNextRoundAndUpdateTournament() {
        val currentTournament = _uiState.value.tournament ?: return

        if (!currentTournament.isStarted || currentTournament.isFinished) {
            _uiState.update { it.copy(error = "Turnier ist nicht aktiv oder bereits beendet.") }
            return
        }
        if (currentTournament.currentRound >= currentTournament.numberOfRounds) {
            _uiState.update { it.copy(error = "Maximale Rundenanzahl bereits erreicht.", tournament = currentTournament.copy(isFinished = true)) }
            viewModelScope.launch {
                try {
                    tournamentRepository.updateTournament(currentTournament.copy(isFinished = true))
                } catch (e: Exception) { /* Fehlerbehandlung */ }
            }
            return
        }

        val currentRoundMatches = currentTournament.matches
            ?.filter { it.roundNumber == currentTournament.currentRound && it.player2Id != null } // Freilose ignorieren
        if (currentRoundMatches?.any { !it.isFinished } == true) {
            _uiState.update { it.copy(error = "Bitte zuerst alle Matches der aktuellen Runde (${currentTournament.currentRound}) abschließen.") }
            return
        }


        val tournamentWithNextRound = RoundGenerator.generateNextRound(currentTournament)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) } // Ladezustand anzeigen
            try {
                tournamentRepository.updateTournament(tournamentWithNextRound)
                _uiState.update {
                    it.copy(
                        tournament = tournamentWithNextRound,
                        error = null,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Fehler beim Erstellen der nächsten Runde: ${e.message}", isLoading = false)
                }
            }
        }
    }

    private fun setMatchResult(matchId: String, p1Wins: Int, p2Wins: Int, draws: Int) {
        val currentTournament = _uiState.value.tournament ?: return
        val matchToUpdate = currentTournament.matches.find { it.id == matchId }

        if (matchToUpdate == null) {
            _uiState.update { it.copy(error = "Match nicht gefunden.") }
            return
        }
        if (matchToUpdate.isFinished) {
            _uiState.update { it.copy(error = "Ergebnis für dieses Match wurde bereits eingetragen.") }
            return
        }


        val updatedMatch = matchToUpdate.copy(
            player1Wins = p1Wins,
            player2Wins = p2Wins,
            isFinished = true,
            draws = draws
        )

        // Aktualisiere die Spielerstatistiken basierend auf diesem Match-Ergebnis
        // WICHTIG: Das Tournament Objekt muss hier mit den aktualisierten Spielerdaten versehen werden,
        // BEVOR das gesamte Tournament Objekt gespeichert wird.
        val tournamentWithUpdatedPlayers = RoundGenerator.updatePlayerStatsFromMatches(currentTournament, updatedMatch)

        // Ersetze das alte Match durch das aktualisierte Match in der Liste des Turniers
        val updatedMatchesList = tournamentWithUpdatedPlayers.matches.map {
            if (it.id == updatedMatch.id) updatedMatch else it
        }

        val finalTournamentState = tournamentWithUpdatedPlayers.copy(matches = updatedMatchesList)


        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                tournamentRepository.updateTournament(finalTournamentState)
                _uiState.update {
                    it.copy(
                        tournament = finalTournamentState,
                        error = null,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Fehler beim Speichern des Match-Ergebnisses: ${e.message}", isLoading = false)
                }
            }
        }
    }

    private fun loadTournament(tournamentId: String) {
        _uiState.update { it.copy(isLoading = true, error = null)}

        viewModelScope.launch {
            try {
                val tournament = tournamentRepository.getTournamentById(tournamentId)
                if (tournament == null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            tournament = null,
                        )
                    }
                } else {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            tournament = tournament,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading= false,
                        error = "Fehler beim Laden der Turniere: ${e.message}"
                    )
                }
            }
        }
    }
}