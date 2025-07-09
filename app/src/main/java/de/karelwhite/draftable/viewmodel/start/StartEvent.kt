package de.karelwhite.draftable.viewmodel.start

sealed interface StartEvent {
    object LoadInitialSettings : StartEvent
}