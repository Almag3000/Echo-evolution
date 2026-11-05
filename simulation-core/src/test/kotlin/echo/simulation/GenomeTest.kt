package echo.simulation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GenomeTest {
    @Test fun genesAreClamped() {
        val gene = Genome(-10.0, 100.0, 2.0, -1.0).normalized()
        assertEquals(0.2, gene.speed)
        assertEquals(30.0, gene.vision)
        assertEquals(1.0, gene.aggression)
        assertEquals(0.0, gene.curiosity)
    }

    @Test fun creatorSpawnsChosenGenome() {
        val sim = Simulation(42L)
        val world = sim.initial(0, 0)
        val next = sim.spawn(world, Genome(speed = 2.0, vision = 25.0))
        assertEquals(1, next.agents.size)
        assertEquals(2.0, next.agents.single().genome.speed)
        assertEquals(25.0, next.agents.single().genome.vision)
    }

    @Test fun descendantsInheritTraits() {
        val sim = Simulation(17L)
        val base = sim.initial(0, 0)
        val world = sim.spawn(base, Genome(speed = 1.5)).copy(
            agents = listOf(Agent(0, 32.0, 32.0, 100.0, genome = Genome(speed = 1.5)))
        )
        val next = sim.step(world)
        assertTrue(next.agents.any { it.generation == 1 })
        assertTrue(next.agents.all { it.genome.speed in 0.2..2.5 })
    }
}
