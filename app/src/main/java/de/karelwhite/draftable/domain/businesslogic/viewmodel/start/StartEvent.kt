package de.karelwhite.draftable.domain.viewmodel.start

sealed interface StartEvent {
    object LoadInitialSettings : StartEvent
}