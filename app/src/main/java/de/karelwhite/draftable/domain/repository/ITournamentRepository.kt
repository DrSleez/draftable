package de.karelwhite.draftable.domain.repository

import de.karelwhite.draftable.domain.model.DiscoverableTournament
import de.karelwhite.draftable.domain.model.Tournament
import kotlinx.coroutines.flow.Flow

interface ITournamentRepository {
    // --- Tournament Creation & Management (Host) ---
    suspend fun createTournament(tournament: Tournament): String
    suspend fun addPlayerToTournament(tournamentId: String, playerName: String): Result<Unit>
    suspend fun startTournament(tournamentId: String): Result<Tournament>
    suspend fun generateNextRound(tournamentId: String): Result<Tournament> // Changed to return Tournament for consistency
    suspend fun updateMatchResult(tournamentId: String, matchId: String, player1Wins: Int, player2Wins: Int, draws: Int): Result<Unit>

    // --- Data Observation ---
    fun getTournamentDetails(tournamentId: String): Flow<Tournament?> // Not suspend
    fun getCurrentTournament(): Flow<Tournament?> // Not suspend

    suspend fun getTournamentById(tournamentId: String): Tournament?

    // --- Tournament Discovery & Joining (Client) ---
    fun startDiscovery(): Flow<List<DiscoverableTournament>> // Not suspend
    fun stopDiscovery() // Can be suspend if it involves async cleanup, but often not.
    suspend fun requestToJoinTournament(endpointId: String, playerName: String): Result<Tournament>
    fun listenForTournamentUpdates(endpointIdForHost: String): Flow<Tournament> // Not suspend

    // --- Common ---
    suspend fun leaveTournament()
}