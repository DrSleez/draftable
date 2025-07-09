package de.karelwhite.draftable.viewmodel.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.karelwhite.draftable.domain.model.Host
import de.karelwhite.draftable.domain.repository.HostRepository
import de.karelwhite.draftable.viewmodel.settings.SettingsState
import de.karelwhite.draftable.viewmodel.utilities.RandomNameGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val hostRepository: HostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StartState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHost()
    }

    fun onEvent(event: StartEvent) {
        when (event) {
            is StartEvent.LoadInitialSettings -> {
                loadHost()
            }
        }
    }
    private fun loadHost() {
        _uiState.update { it.copy(isLoading = true, error = null) } // Ladezustand setzen

        viewModelScope.launch {
            try {
                val host = hostRepository.getHost() // Suspend-Funktion
                if (host != null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            host = host,
                        )
                    }
                } else {
                    createNewHostWithRandomData()
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Fehler beim Laden des Hosts in den Einstellung: ${e.message}"
                    )
                }
            }
        }
    }
    private fun createNewHostWithRandomData() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            } // Zeige Ladezustand während der Erstellung
            try {
                val newHostName = RandomNameGenerator.getRandomHostName()
                val newHost = Host(name = newHostName)
                hostRepository.createHost(newHost)

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        host = newHost,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        host = null,
                        error = "Fehler beim Erstellen eines neuen Hosts: ${e.message}"
                    )
                }
            }
        }


    }
}