package de.karelwhite.draftable.domain.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.karelwhite.draftable.domain.model.Host
import de.karelwhite.draftable.domain.repository.HostRepository
import de.karelwhite.draftable.domain.viewmodel.utilities.RandomNameGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Hilfsobjekt für zufällige Namen (kann auch außerhalb der Klasse sein)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val hostRepository: HostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHostSettings()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.SetName -> {
                _uiState.update { currentState ->
                    currentState.copy(name = event.name)
                }
            }

            SettingsEvent.SaveSettings -> {
                saveSettings()
            }

            SettingsEvent.LoadInitialSettings -> {
                loadHostSettings()
            }
        }
    }

    private fun loadHostSettings() {
        _uiState.update { it.copy(isLoading = true, error = null) } // Ladezustand setzen

        viewModelScope.launch {
            try {
                val host = hostRepository.getHost() // Suspend-Funktion
                if (host != null) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            host = host,
                            name = host.name // Initialisiere das Namensfeld mit dem Hostnamen
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

    private fun saveSettings() {
        val currentHost = _uiState.value.host
        val currentName = _uiState.value.name

        if (currentHost == null) {
            _uiState.update { it.copy(error = "Kein Host zum Speichern vorhanden.") }
            return
        }

        // Zeige einen Lade-/Speicherzustand, wenn gewünscht
        // _uiState.update { it.copy(isLoading = true) } // Optional: Visuelles Feedback für Speichern

        viewModelScope.launch {
            try {
                val updatedHost = currentHost.copy(name = currentName)
                hostRepository.updateHost(updatedHost) // Annahme: updateHost ist eine suspend fun

                _uiState.update { currentState ->
                    currentState.copy(
                        host = updatedHost,
                        error = null

                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = "Fehler beim Speichern des Hosts in den Einstellungen: ${e.message}"
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
                        name = newHost.name,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        host = null,
                        name = "",
                        error = "Fehler beim Erstellen eines neuen Hosts: ${e.message}"
                    )
                }
            }
        }


    }
}