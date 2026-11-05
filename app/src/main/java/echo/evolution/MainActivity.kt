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
    MaterialTheme(colorScheme = darkColorScheme()) {
        Column(
            Modifier.fillMaxSize().background(Color(0xFF0B1020)).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("ECHØ EVOLUTION", style = MaterialTheme.typography.headlineMedium)
            Text("Tick: ${world.tick}  •  Life: ${world.agents.size}  •  Food: ${world.food.size}")
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
                    drawCircle(Color(0xFF39D7EF), radius = 5.dp.toPx(),
                        center = Offset((it.x / 64.0 * size.width).toFloat(), (it.y / 64.0 * size.height).toFloat()))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { world = simulation.step(world) }) { Text("Step") }
                Button(onClick = { repeat(20) { world = simulation.step(world) } }) { Text("+20 ticks") }
                OutlinedButton(onClick = { world = simulation.initial() }) { Text("Reset") }
            }
            if (creator) {
                Text("Creator tools", style = MaterialTheme.typography.titleLarge)
                Button(onClick = {
                    if (world.agents.size < 300) {
                        world = world.copy(
                            agents = world.agents + Agent(world.nextId, 32.0, 32.0, 65.0),
                            nextId = world.nextId + 1
                        )
                    }
                }) { Text("Spawn organism") }
                Button(onClick = {
                    if (world.food.size < 200) world = world.copy(food = world.food + Food(32.0, 32.0))
                }) { Text("Add food") }
            } else Text("Observe emergence. Step through time and inspect population changes.")
        }
    }
}
