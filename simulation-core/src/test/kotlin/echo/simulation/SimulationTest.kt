package echo.simulation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SimulationTest {
    @Test fun sameSeedSameWorld() {
        val a = Simulation(123L)
        val b = Simulation(123L)
        var wa = a.initial()
        var wb = b.initial()
        repeat(100) { wa = a.step(wa); wb = b.step(wb) }
        assertEquals(wa, wb)
    }

    @Test fun agentsRemainWithinWorld() {
        val sim = Simulation(77L)
        var world = sim.initial()
        repeat(200) { world = sim.step(world) }
        assertTrue(world.agents.all { it.x in 0.0..64.0 && it.y in 0.0..64.0 })
    }

    @Test fun populationIsCapped() {
        val sim = Simulation(3L)
        var world = sim.initial()
        repeat(300) { world = sim.step(world) }
        assertTrue(world.agents.size <= 300)
    }
}
