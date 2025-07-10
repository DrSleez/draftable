package de.karelwhite.draftable.domain.viewmodel.tournamentdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
            TournamentDetailsEvent.GoNextRound -> TODO()
            TournamentDetailsEvent.LoadTournamentDetails -> TODO()
            is TournamentDetailsEvent.SetMatchResult -> TODO()
            TournamentDetailsEvent.StartTournament -> TODO()
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