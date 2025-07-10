package de.karelwhite.draftable.domain.viewmodel.tournamentdetails

import de.karelwhite.draftable.domain.model.Match
import de.karelwhite.draftable.domain.model.Player

sealed interface TournamentDetailsEvent {
    object LoadTournamentDetails : TournamentDetailsEvent
    data class SetMatchResult(val match: Match) : TournamentDetailsEvent
    object StartTournament : TournamentDetailsEvent
    object GoNextRound : TournamentDetailsEvent
}