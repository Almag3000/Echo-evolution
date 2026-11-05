package echo.evolution

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import echo.simulation.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { EchoScreen() }
    }
}

@Composable
fun EchoScreen() {
    val simulation = remember { Simulation(42L) }
    var world by remember { mutableStateOf(simulation.initial()) }
    var creator by remember { mutableStateOf(false) }
    var speed by remember { mutableFloatStateOf(1f) }
    var vision by remember { mutableFloatStateOf(12f) }
    var curiosity by remember { mutableFloatStateOf(0.2f) }
    var aggression by remember { mutableFloatStateOf(0.2f) }
    val averageEnergy = world.agents.map { it.energy }.average().takeIf { !it.isNaN() } ?: 0.0
    val maxGeneration = world.agents.maxOfOrNull { it.generation } ?: 0
    MaterialTheme(colorScheme = darkColorScheme()) {
        Column(
            Modifier.fillMaxSize().background(Color(0xFF0B1020)).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("ECHØ EVOLUTION", style = MaterialTheme.typography.headlineMedium)
            Text("Tick ${world.tick}  •  Life ${world.agents.size}  •  Food ${world.food.size}")
            Text("Average energy %.1f  •  Generation %d".format(averageEnergy, maxGeneration))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { creator = false }) { Text("Observer") }
                Button(onClick = { creator = true }) { Text("Creator") }
            }
            Canvas(Modifier.fillMaxWidth().aspectRatio(1f).background(Color(0xFF101C30))) {
                world.food.forEach {
                    drawCircle(Color(0xFF76D275), radius = 3.dp.toPx(),
                        center = Offset((it.x / 64.0 * size.width).toFloat(), (it.y / 64.0 * size.height).toFloat()))
                }
                world.agents.forEach {
                    drawCircle(if (it.generation > 0) Color(0xFFF3A7FF) else Color(0xFF39D7EF),
                        radius = 5.dp.toPx(),
                        center = Offset((it.x / 64.0 * size.width).toFloat(), (it.y / 64.0 * size.height).toFloat()))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { world = simulation.step(world) }) { Text("Step") }
                Button(onClick = { repeat(20) { world = simulation.step(world) } }) { Text("+20 ticks") }
                OutlinedButton(onClick = { world = simulation.initial() }) { Text("Reset") }
            }
            if (creator) {
                Text("Genome editor", style = MaterialTheme.typography.titleLarge)
                Text("Speed: %.1f".format(speed))
                Slider(value = speed, onValueChange = { speed = it }, valueRange = 0.2f..2.5f)
                Text("Vision: %.1f".format(vision))
                Slider(value = vision, onValueChange = { vision = it }, valueRange = 2f..30f)
                Text("Curiosity: %.2f".format(curiosity))
                Slider(value = curiosity, onValueChange = { curiosity = it })
                Text("Aggression: %.2f (reserved for future combat)".format(aggression))
                Slider(value = aggression, onValueChange = { aggression = it })
                Button(onClick = {
                    world = simulation.spawn(world, Genome(speed.toDouble(), vision.toDouble(), aggression.toDouble(), curiosity.toDouble()))
                }) { Text("Spawn with this genome") }
                Button(onClick = { world = simulation.addFood(world) }) { Text("Add food") }
            } else Text("Observe evolution: purple organisms are descendants.")
        }
    }
}
