package echo.simulation

import kotlin.math.hypot
import kotlin.random.Random

data class Agent(val id: Int, val x: Double, val y: Double, val energy: Double, val age: Int = 0)
data class Food(val x: Double, val y: Double)
data class World(val tick: Int, val agents: List<Agent>, val food: List<Food>, val nextId: Int)

data class Config(
    val width: Double = 64.0,
    val height: Double = 64.0,
    val metabolicCost: Double = 0.4,
    val foodEnergy: Double = 20.0,
    val foodReach: Double = 1.0,
    val reproductionThreshold: Double = 90.0,
    val maxAgents: Int = 300,
    val maxFood: Int = 200
)

/**
 * Deterministic prototype. No Android dependencies.
 * Every random choice derives from seed and tick, so identical input worlds
 * and configurations produce identical next worlds.
 */
class Simulation(private val seed: Long, private val config: Config = Config()) {
    fun initial(agentCount: Int = 20, foodCount: Int = 80): World {
        require(agentCount in 0..config.maxAgents && foodCount in 0..config.maxFood)
        val rng = Random(seed)
        val agents = (0 until agentCount).map {
            Agent(it, rng.nextDouble(config.width), rng.nextDouble(config.height), 50.0)
        }
        val food = (0 until foodCount).map {
            Food(rng.nextDouble(config.width), rng.nextDouble(config.height))
        }
        return World(0, agents, food, agentCount)
    }

    fun step(world: World): World {
        val rng = Random(seed xor (world.tick.toLong() * -7046029254386353131L))
        val available = world.food.toMutableList()
        val survivors = mutableListOf<Agent>()
        var nextId = world.nextId

        for (agent in world.agents.sortedBy { it.id }) {
            val target = available.minByOrNull { hypot(it.x - agent.x, it.y - agent.y) }
            val angle = if (target == null) rng.nextDouble(0.0, Math.PI * 2)
                        else kotlin.math.atan2(target.y - agent.y, target.x - agent.x)
            val nx = (agent.x + kotlin.math.cos(angle)).coerceIn(0.0, config.width)
            val ny = (agent.y + kotlin.math.sin(angle)).coerceIn(0.0, config.height)
            var energy = agent.energy - config.metabolicCost
            val foodIndex = available.indexOfFirst { hypot(it.x - nx, it.y - ny) <= config.foodReach }
            if (foodIndex >= 0) {
                available.removeAt(foodIndex)
                energy += config.foodEnergy
            }
            if (energy <= 0.0) continue
            val updated = agent.copy(x = nx, y = ny, energy = energy, age = agent.age + 1)
            if (energy >= config.reproductionThreshold && survivors.size + world.agents.size < config.maxAgents) {
                survivors += updated.copy(energy = energy / 2)
                survivors += Agent(nextId++, nx, ny, energy / 2)
            } else survivors += updated
        }
        if (available.size < config.maxFood && world.tick % 5 == 0) {
            available += Food(rng.nextDouble(config.width), rng.nextDouble(config.height))
        }
        return World(world.tick + 1, survivors, available, nextId)
    }
}
