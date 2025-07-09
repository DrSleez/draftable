package de.karelwhite.draftable.data.repository.local

import de.karelwhite.draftable.data.repository.room.HostDao
import de.karelwhite.draftable.data.repository.room.MatchDao
import de.karelwhite.draftable.data.repository.room.MatchEntity
import de.karelwhite.draftable.data.repository.room.PlayerDao
import de.karelwhite.draftable.data.repository.room.PlayerEntity
import de.karelwhite.draftable.data.repository.room.TournamentDao
import de.karelwhite.draftable.data.repository.room.TournamentEntity
import de.karelwhite.draftable.data.repository.room.toDomain
import de.karelwhite.draftable.data.repository.room.toEntity
import de.karelwhite.draftable.domain.model.Tournament
import de.karelwhite.draftable.domain.repository.TournamentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TournamentRepositoryRoomImp @Inject constructor(
    private val playerDao: PlayerDao,
    private val matchDao: MatchDao,
    private val tournamentDao: TournamentDao) : TournamentRepository  {

    override suspend fun createTournament(tournament: Tournament) {
        withContext(Dispatchers.IO){
            tournamentDao.insertTournament(tournament.toEntity())
            tournament.players.map { playerDao.insertPlayer(it.toEntity()) }
            if (tournament.matches != null){
                val entityMatches = tournament.matches
                entityMatches?.map {
                    matchDao.insertMatch(it.toEntity())
                }
            }
        }
    }

    override suspend fun getAllTournaments(): List<Tournament>? {
        val allTournaments : List<TournamentEntity>? = tournamentDao.getAllTournaments()
        val domainTournaments : MutableList<Tournament> = mutableListOf()
        if (!allTournaments.isNullOrEmpty()) {
            allTournaments.map { tournament ->
                val players = playerDao.getPlayersForTournament(tournament.id)
                val matches = matchDao.getAllMatchesForTournament(tournament.id)
                if (players != null && matches != null) {
                    domainTournaments.add(tournament.toDomain(players, matches))
                }
                return domainTournaments
            }
        }
        return null
    }

    override suspend fun getAllTournamentsByHostId(hostId: String): List<Tournament>? {
        val allHostTournaments : List<TournamentEntity>? = tournamentDao.getAllTournamentsByHostId(hostId)
        val domainTournaments : MutableList<Tournament> = mutableListOf()
        if (!allHostTournaments.isNullOrEmpty()) {
            allHostTournaments.map { tournament ->
                val players = playerDao.getPlayersForTournament(tournament.id)
                val matches = matchDao.getAllMatchesForTournament(tournament.id)
                if (players != null && matches != null) {
                    domainTournaments.add(tournament.toDomain(players, matches))
                }
                return domainTournaments
            }
        }
        return null
    }

    override suspend fun getTournamentById(id: String): Tournament? {
        val tournament = tournamentDao.getTournamentById(id)
        if (tournament != null) {
            val players = playerDao.getPlayersForTournament(tournament.id)
            val matches = matchDao.getAllMatchesForTournament(tournament.id)
            if (players != null && matches != null) {
                return tournament.toDomain(players, matches)
            }
        }
        return null
    }

    override suspend fun updateTournament(tournament: Tournament) {
        tournamentDao.updateTournament(tournament.toEntity())
        tournament.players.map { playerDao.updatePlayer(it.toEntity()) }
        if (tournament.matches != null){
            val entityMatches = tournament.matches
            entityMatches?.map {
                matchDao.updateMatch(it.toEntity())
            }
        }
    }

    override suspend fun deleteTournament(id: String) {
        tournamentDao.getTournamentById(id).let { tournament ->
            if (tournament != null) {
                tournamentDao.deleteTournament(tournament)
            }
        }
    }

    override suspend fun startTournament(tournament: Tournament) {
        tournamentDao.getTournamentById(tournament.id).let {
            if (it != null) {
                it.isStarted = true
                tournamentDao.updateTournament(it)
            }
        }
    }

    override suspend fun stopTournament(tournament: Tournament) {
        tournamentDao.getTournamentById(tournament.id).let {
            if (it != null) {
                it.isStarted = false
                tournamentDao.updateTournament(it)
            }
        }
    }
}