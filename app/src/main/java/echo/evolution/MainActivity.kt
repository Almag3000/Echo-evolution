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
import androidx.compose.ui.unit.sp
import echo.simulation.*
import kotlinx.coroutines.delay

private val background = Color(0xFF0B1020)
private val textColor = Color(0xFFF4F5FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(11, 16, 32)
        window.navigationBarColor = android.graphics.Color.rgb(11, 16, 32)
        window.decorView.systemUiVisibility = 0
        setContent { EchoScreen() }
    }
}

@Composable
fun EchoScreen() {
    val simulation = remember { Simulation(42L) }
    var world by remember { mutableStateOf(simulation.initial()) }
    var creator by remember { mutableStateOf(false) }
    var playing by remember { mutableStateOf(false) }
    var ticksPerFrame by remember { mutableIntStateOf(1) }
    var speed by remember { mutableFloatStateOf(1f) }
    var vision by remember { mutableFloatStateOf(12f) }
    var curiosity by remember { mutableFloatStateOf(0.2f) }
    var aggression by remember { mutableFloatStateOf(0.2f) }
    val averageEnergy = world.agents.map { it.energy }.average().takeIf { !it.isNaN() } ?: 0.0
    val maxGeneration = world.agents.maxOfOrNull { it.generation } ?: 0

    LaunchedEffect(playing, ticksPerFrame) {
        while (playing) {
            delay(160L)
            repeat(ticksPerFrame) { world = simulation.step(world) }
        }
    }
    MaterialTheme(colorScheme = darkColorScheme(
        background = background,
        surface = Color(0xFF142138),
        onBackground = textColor,
        onSurface = textColor
    )) {
        Column(
            Modifier.fillMaxSize().background(background)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("ECHØ EVOLUTION", color = textColor, style = MaterialTheme.typography.headlineMedium)
            Text("Шаг: ${world.tick}   •   Существ: ${world.agents.size}   •   Еды: ${world.food.size}", color = textColor)
            Text("Средняя энергия: %.1f   •   Поколение: %d".format(averageEnergy, maxGeneration), color = textColor)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { creator = false }, enabled = creator) { Text("Наблюдение") }
                Button(onClick = { creator = true }, enabled = !creator) { Text("Создание") }
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
            Text("● Голубые — первые существа    ● Фиолетовые — потомки    ● Зелёные — пища", color = textColor, fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { playing = !playing }) { Text(if (playing) "Пауза" else "Старт") }
                OutlinedButton(onClick = { ticksPerFrame = when (ticksPerFrame) { 1 -> 4; 4 -> 16; else -> 1 } }) {
                    Text("×${ticksPerFrame}")
                }
                OutlinedButton(onClick = { playing = false; world = simulation.initial() }) { Text("Сброс") }
            }
            if (creator) {
                Text("Редактор генома", color = textColor, style = MaterialTheme.typography.titleLarge)
                Text("Скорость: %.1f — быстрее движение, выше расход энергии".format(speed), color = textColor)
                Slider(value = speed, onValueChange = { speed = it }, valueRange = 0.2f..2.5f)
                Text("Зрение: %.1f — дальность поиска пищи".format(vision), color = textColor)
                Slider(value = vision, onValueChange = { vision = it }, valueRange = 2f..30f)
                Text("Любопытство: %.2f — вероятность случайного движения".format(curiosity), color = textColor)
                Slider(value = curiosity, onValueChange = { curiosity = it })
                Text("Агрессия: %.2f — пока не влияет на поведение".format(aggression), color = textColor)
                Slider(value = aggression, onValueChange = { aggression = it })
                Button(onClick = {
                    world = simulation.spawn(world, Genome(speed.toDouble(), vision.toDouble(), aggression.toDouble(), curiosity.toDouble()))
                }) { Text("Добавить существо") }
                OutlinedButton(onClick = { world = simulation.addFood(world) }) { Text("Добавить пищу") }
            } else {
                Text("Нажми «Старт», чтобы существа начали искать пищу и размножаться.", color = textColor)
            }
        }
    }
}
