package de.karelwhite.draftable.domain.viewmodel.mytournaments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.karelwhite.draftable.domain.businesslogic.viewmodel.mytournaments.MyTournamentsState
import de.karelwhite.draftable.domain.repository.HostRepository
import de.karelwhite.draftable.domain.repository.TournamentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyTournamentsViewModel @Inject constructor(
    private val tournamentRepository : TournamentRepository,
    private val hostRepository: HostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyTournamentsState())
    val uiState: StateFlow<MyTournamentsState> = _uiState.asStateFlow()


    fun onEvent(event: MyTournamentsEvent){
        when(event){
            is MyTournamentsEvent.DeleteTournament -> {
                deleteTournament(event.tournamentId)
            }
            MyTournamentsEvent.LoadTournaments -> {
                loadHostAndAssociatedTournaments()
            }
            MyTournamentsEvent.ToastMessageShown -> {
                _uiState.update { it.copy(toastMessage = null) }
            }
            MyTournamentsEvent.RefreshTournaments -> {
                val isInitialLoad = _uiState.value.host == null
                if(isInitialLoad){
                    loadHostAndAssociatedTournaments()
                } else {
                    _uiState.value.host?.id?.let { hostId ->
                        _uiState.update { it.copy(isRefreshing = true) }
                        loadTournamentsForHost(hostId)
                    } ?: run {
                        loadHostAndAssociatedTournaments()
                    }
                }
            }
        }
    }

    init {
        //loadHostAndAssociatedTournaments()
    }

    private fun deleteTournament(tournamentId: String) {
        viewModelScope.launch {
            try {
                tournamentRepository.deleteTournament(tournamentId)
                _uiState.update { currentState ->
                    currentState.copy(
                        tournaments = currentState.tournaments.filter { it.id != tournamentId },
                        toastMessage = "Turnier mit ID $tournamentId erfolgreich gelöscht"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        toastMessage = "Fehler beim Löschen des Turniers mit ID $tournamentId: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadHostAndAssociatedTournaments() {
        if(_uiState.value.isRefreshing){
            //_uiState.update { it.copy(isRefreshing = true, error = null) }
        } else {
            _uiState.update { it.copy(isLoading = true, error = null) }
        }
        viewModelScope.launch {
            try {
                val host = hostRepository.getHost()
                if (host != null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = true,
                            isRefreshing = false,
                            host = host
                        )
                    }
                    loadTournamentsForHost(host.id)
                } else {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            host = null,
                            error = "Kein Host gefunden."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = "Fehler beim Laden des Hosts: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadTournamentsForHost(hostId: String) {
        if(_uiState.value.isRefreshing){
           // _uiState.update { it.copy(isRefreshing = true, error = null) }
        } else {
            _uiState.update { it.copy(isLoading = true, error = null) }
        }

        viewModelScope.launch {
            try {
                val tournaments = tournamentRepository.getAllTournamentsByHostId(hostId)
                if (tournaments == null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            tournaments = emptyList(),
                        )
                    }
                } else {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            tournaments = tournaments,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading= false,
                        isRefreshing = false,
                        error = "Fehler beim Laden der Turniere: ${e.message}"
                    )
                }
            }
        }
    }
}