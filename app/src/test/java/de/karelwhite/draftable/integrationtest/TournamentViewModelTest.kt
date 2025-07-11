package de.karelwhite.draftable.integrationtest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import androidx.lifecycle.SavedStateHandle
import de.karelwhite.draftable.domain.model.Tournament
import de.karelwhite.draftable.domain.repository.TournamentRepository
import de.karelwhite.draftable.domain.viewmodel.tournamentdetails.TournamentDetailsViewModel

@ExperimentalCoroutinesApi
class TournamentDetailsViewModelTest {

    private lateinit var viewModel: TournamentDetailsViewModel
    private val mockTournamentRepository: TournamentRepository = mock()
    private val testDispatcher = StandardTestDispatcher() // Oder UnconfinedTestDispatcher

    // Testdaten
    private val testTournamentId = "test-tournament-123"

    private val testHostId = "test-host-id"
    private val initialTournament =
        Tournament(id = testTournamentId, name = "Initial Tournament", players = emptyList(), hostPlayerId = testHostId)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Setze den Main Dispatcher für viewModelScope
        val savedStateHandle = SavedStateHandle(mapOf("tournamentId" to testTournamentId))
        viewModel = TournamentDetailsViewModel(mockTournamentRepository, savedStateHandle)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain() // Setze den Main Dispatcher zurück
    }

    @Test
    fun `loadTournament on init success updates uiState with tournament`() = runTest {
        whenever(mockTournamentRepository.getTournamentById(testTournamentId)).thenReturn(initialTournament)

        // ViewModel wird im init geladen, wir müssen nur den State beobachten
        // Bei StandardTestDispatcher müssen wir ggf. advanceUntilIdle() aufrufen
        // oder den Dispatcher direkt ausführen. Bei UnconfinedTestDispatcher läuft es oft direkter.

        // Initialisierung des ViewModels (passiert im setUp)
        // Warte bis alle Coroutinen im Dispatcher abgearbeitet sind
        advanceUntilIdle() // Wichtig bei StandardTestDispatcher

        val state = viewModel.uiState.value // Oder .first() wenn du auf eine spezifische Emission wartest

        assertNotNull(state.tournament)
        assertEquals(testTournamentId, state.tournament?.id)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }
    // ... weitere Tests für GoNextRound, StartTournament, Fehlerfälle etc. ...
}
