package de.karelwhite.draftable.domain.model

import java.util.UUID

data class Host(
    var id: String = UUID.randomUUID().toString(),
    val name: String
)