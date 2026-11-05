package echo.simulation

import kotlin.math.*
import kotlin.random.Random

data class Genome(
    val speed: Double = 1.0,
    val vision: Double = 12.0,
    val aggression: Double = 0.2,
    val curiosity: Double = 0.2
) {
    fun normalized() = copy(
        speed = speed.coerceIn(0.2, 2.5),
        vision = vision.coerceIn(2.0, 30.0),
        aggression = aggression.coerceIn(0.0, 1.0),
        curiosity = curiosity.coerceIn(0.0, 1.0)
    )
}
data class Agent(
    val id: Int, val x: Double, val y: Double, val energy: Double,
    val age: Int = 0, val genome: Genome = Genome(), val generation: Int = 0
)
data class Food(val x: Double, val y: Double)
data class World(val tick: Int, val agents: List<Agent>, val food: List<Food>, val nextId: Int)
data class Config(
    val width: Double = 64.0, val height: Double = 64.0,
    val metabolicCost: Double = 0.4, val foodEnergy: Double = 20.0,
    val foodReach: Double = 1.0, val reproductionThreshold: Double = 90.0,
    val maxAgents: Int = 300, val maxFood: Int = 200
)

/** Pure Kotlin seeded simulation; reproducible for identical seeds and commands. */
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

    fun spawn(world: World, genome: Genome): World {
        if (world.agents.size >= config.maxAgents) return world
        return world.copy(
            agents = world.agents + Agent(world.nextId, config.width / 2, config.height / 2, 65.0, genome = genome.normalized()),
            nextId = world.nextId + 1
        )
    }

    fun addFood(world: World): World {
        if (world.food.size >= config.maxFood) return world
        return world.copy(food = world.food + Food(config.width / 2, config.height / 2))
    }

    fun step(world: World): World {
        val rng = Random(seed xor (world.tick.toLong() * -7046029254386353131L))
        val available = world.food.toMutableList()
        val survivors = mutableListOf<Agent>()
        var nextId = world.nextId
        var births = 0
        for (agent in world.agents.sortedBy { it.id }) {
            val gene = agent.genome.normalized()
            val target = available.filter { hypot(it.x - agent.x, it.y - agent.y) <= gene.vision }
                .minByOrNull { hypot(it.x - agent.x, it.y - agent.y) }
            val angle = if (target == null || rng.nextDouble() < gene.curiosity * 0.1)
                rng.nextDouble(0.0, PI * 2)
            else atan2(target.y - agent.y, target.x - agent.x)
            val nx = (agent.x + cos(angle) * gene.speed).coerceIn(0.0, config.width)
            val ny = (agent.y + sin(angle) * gene.speed).coerceIn(0.0, config.height)
            var energy = agent.energy - config.metabolicCost - gene.speed * 0.08 - gene.vision * 0.005
            val foodIndex = available.indexOfFirst { hypot(it.x - nx, it.y - ny) <= config.foodReach }
            if (foodIndex >= 0) {
                available.removeAt(foodIndex)
                energy += config.foodEnergy
            }
            if (energy <= 0.0) continue
            val updated = agent.copy(x = nx, y = ny, energy = energy, age = agent.age + 1)
            if (energy >= config.reproductionThreshold &&
                world.agents.size + births < config.maxAgents
            ) {
                survivors += updated.copy(energy = energy / 2)
                val childGenome = gene.copy(
                    speed = gene.speed + rng.nextDouble(-0.12, 0.12),
                    vision = gene.vision + rng.nextDouble(-1.0, 1.0),
                    aggression = gene.aggression + rng.nextDouble(-0.05, 0.05),
                    curiosity = gene.curiosity + rng.nextDouble(-0.05, 0.05)
                ).normalized()
                survivors += Agent(nextId++, nx, ny, energy / 2, genome = childGenome, generation = agent.generation + 1)
                births++
            } else survivors += updated
        }
        if (available.size < config.maxFood && world.tick % 5 == 0) {
            available += Food(rng.nextDouble(config.width), rng.nextDouble(config.height))
        }
        return World(world.tick + 1, survivors, available, nextId)
    }
}
