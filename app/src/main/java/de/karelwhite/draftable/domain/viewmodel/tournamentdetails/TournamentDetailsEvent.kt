package de.karelwhite.draftable.domain.viewmodel.tournamentdetails

import de.karelwhite.draftable.domain.model.Match
import de.karelwhite.draftable.domain.model.Player

sealed interface TournamentDetailsEvent {
    object LoadTournamentDetails : TournamentDetailsEvent

    data class SubmitMatchResult(
        val player1Wins: Int,
        val player2Wins: Int,
        val draws: Int
    ) : TournamentDetailsEvent

    data class ShowMatchResultDialog(val match: Match) : TournamentDetailsEvent

    object HideMatchResultDialog : TournamentDetailsEvent
    object StartTournament : TournamentDetailsEvent
    object GoNextRound : TournamentDetailsEvent
    object ShowLeaderboard : TournamentDetailsEvent
    object HideLeaderboard : TournamentDetailsEvent
}