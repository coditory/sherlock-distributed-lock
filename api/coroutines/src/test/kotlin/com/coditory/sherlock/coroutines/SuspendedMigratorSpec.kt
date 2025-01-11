package com.coditory.sherlock.coroutines

import com.coditory.sherlock.base.UpdatableFixedClock.defaultUpdatableFixedClock
import com.coditory.sherlock.coroutines.migrator.SherlockMigrator
import com.coditory.sherlock.inmem.coroutines.InMemorySherlock
import com.coditory.sherlock.migrator.ChangeSet
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.CopyOnWriteArrayList

class SuspendedMigratorSpec {
    @Test
    fun `should run annotated change sets with suspended functions`() = runTest {
        val changeSets = TwoChangeSets()
        SherlockMigrator.builder(createSherlock())
            .addAnnotatedChangeSets(changeSets)
            .migrate()
        assertEquals(changeSets.executed, listOf(TwoChangeSets.FIRST, TwoChangeSets.SECOND))
    }

    @Test
    fun `should run suspended change sets`() = runTest {
        val executed = CopyOnWriteArrayList<String>()
        val firstChange = "first"
        val secondChange = "second"
        SherlockMigrator.builder(createSherlock())
            .addChangeSet(
                firstChange,
                suspend {
                    executed.add(firstChange)
                    delay(1)
                },
            )
            .addChangeSet(
                secondChange,
                suspend {
                    executed.add(secondChange)
                    delay(1)
                },
            )
            .migrate()
        assertEquals(executed, listOf(firstChange, secondChange))
    }

    private fun createSherlock(): Sherlock {
        return InMemorySherlock.builder()
            .withOwnerId("locks_test_instance")
            .withClock(defaultUpdatableFixedClock())
            .build()
    }

    class TwoChangeSets {
        val executed = mutableListOf<String>()

        @ChangeSet(order = 2, id = SECOND)
        suspend fun removeIndex() {
            executed.add(SECOND)
            delay(1)
        }

        @ChangeSet(order = 1, id = FIRST)
        suspend fun addIndex() {
            executed.add(FIRST)
            delay(1)
        }

        companion object {
            const val FIRST = "add index - annotated"
            const val SECOND = "remove index - annotated"
        }
    }
}
