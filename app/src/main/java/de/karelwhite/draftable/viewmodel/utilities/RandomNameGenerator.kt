package de.karelwhite.draftable.viewmodel.utilities

object RandomNameGenerator {
    private val randomHostNames = listOf(
        "Nexus Hub", "Orion Gateway", "Sirius Port", "Nova Station", "Celestial Spire",
        "Cosmic Anchor", "Stardust Relay", "Galactic Core", "Quantum Link", "Void Beacon"
    )
    fun getRandomHostName(): String = randomHostNames.random()
}