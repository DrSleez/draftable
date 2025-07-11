package de.karelwhite.draftable.unittest

import de.karelwhite.draftable.domain.model.Host
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

@DisplayName("Model Host Class Tests")
internal class ModelHostTest {

    private val defaultHostName = "Karel"

    @Test
    @DisplayName("Should create host with a name and automatically generate ID")
    fun `create host with name generates id`() {
        val host = Host(name = defaultHostName)

        assertNotNull(host.id, "ID should be generated and not null")
        assertTrue(isValidUUID(host.id), "ID should be a valid UUID string")
        assertEquals(defaultHostName, host.name, "Name should be set correctly")
    }

    @Test
    @DisplayName("Should allow providing a custom ID")
    fun `create host with custom id`() {
        val customId = "custom-host-id-123"
        val host = Host(id = customId, name = defaultHostName)

        assertEquals(customId, host.id, "Custom ID should be used")
        assertEquals(defaultHostName, host.name, "Name should be set correctly")
    }

    @Nested
    @DisplayName("Mutability Tests")
    inner class MutabilityTests {

        @Test
        @DisplayName("Should allow changing mutable property 'id'")
        fun `allow changing id`() {
            val host = Host(name = defaultHostName)
            val originalId = host.id
            val newId = "new-custom-id-456"

            host.id = newId

            assertEquals(newId, host.id, "ID should be updatable")
            assertNotEquals(originalId, host.id, "ID should be different from the original generated ID")
        }
    }

    @Test
    @DisplayName("Two instances with same data should be equal")
    fun `equality check for data class`() {
        // Da 'name' die einzige Eigenschaft im Primärkonstruktor neben der (potenziell unterschiedlichen) ID ist,
        // und 'id' auch Teil des Primärkonstruktors ist, müssen für Gleichheit beide übereinstimmen.
        val host1 = Host(id = "fixed-id-789", name = "UniqueHostName")
        val host2 = Host(id = "fixed-id-789", name = "UniqueHostName")
        val host3 = Host(id = "another-id-000", name = "UniqueHostName") // Gleicher Name, andere ID
        val host4 = Host(id = "fixed-id-789", name = "AnotherHostName")   // Gleiche ID, anderer Name

        assertEquals(host1, host2, "Two hosts with the same ID and name should be equal")
        assertEquals(host1.hashCode(), host2.hashCode(), "HashCodes should be equal for equal objects")

        assertNotEquals(host1, host3, "Hosts with different IDs should not be equal")
        assertNotEquals(host1, host4, "Hosts with different names should not be equal")
    }

    @Test
    @DisplayName("Copy function should create a new instance with optionally modified properties")
    fun `copy function test`() {
        val originalHost = Host(id = "original-copy-id", name = "OriginalHostForCopy")

        // Kopie mit denselben Daten
        val copiedHostSameData = originalHost.copy()
        assertEquals(originalHost, copiedHostSameData, "Copied host with same data should be equal")
        assertNotSame(originalHost, copiedHostSameData, "Copied host should be a new instance (different object reference)")

        // Kopie mit geändertem Namen
        val copiedHostModifiedName = originalHost.copy(name = "CopiedAndModifiedHostName")
        assertEquals("CopiedAndModifiedHostName", copiedHostModifiedName.name, "Name should be updated in the copy")
        assertEquals(originalHost.id, copiedHostModifiedName.id, "ID should remain the same if not specified in copy")
        assertNotEquals(originalHost, copiedHostModifiedName)


        // Kopie mit geänderter ID
        val copiedHostModifiedId = originalHost.copy(id = "new-copied-id")
        assertEquals("new-copied-id", copiedHostModifiedId.id, "ID should be updated in the copy")
        assertEquals(originalHost.name, copiedHostModifiedId.name, "Name should remain the same if not specified in copy")
        assertNotEquals(originalHost, copiedHostModifiedId)
    }

    // Hilfsfunktion zur UUID-Validierung
    private fun isValidUUID(uuidString: String): Boolean {
        return try {
            UUID.fromString(uuidString)
            true
        } catch (e: IllegalArgumentException) {
            println(e.message)
            false
        }
    }
}

