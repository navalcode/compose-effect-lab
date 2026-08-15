@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.composeeffectlab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.composeeffectlab.domain.FakeListenerRegistry
import com.example.composeeffectlab.model.EffectEvent
import com.example.composeeffectlab.model.EffectEventRecorder
import com.example.composeeffectlab.model.EventKind
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

private enum class LabSection(val label: String) {
    GUIDE("Guía"),
    LAUNCHED("LaunchedEffect"),
    DISPOSABLE("DisposableEffect"),
    COMPARISON("Comparativa"),
    REAL_CASES("Casos reales"),
}

@Composable
fun ComposeEffectLabApp() {
    var selected by remember { mutableStateOf(LabSection.GUIDE) }
    var events by remember { mutableStateOf(emptyList<EffectEvent>()) }
    val recorder = remember { EffectEventRecorder { events = it } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Compose Effect Lab", fontWeight = FontWeight.Bold)
                        Text(
                            "Predecir · ejecutar · observar · explicar",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            ScrollableTabRow(
                selectedTabIndex = selected.ordinal,
                edgePadding = 12.dp,
            ) {
                LabSection.entries.forEach { section ->
                    Tab(
                        selected = section == selected,
                        onClick = { selected = section },
                        text = { Text(section.label) },
                    )
                }
            }

            when (selected) {
                LabSection.GUIDE -> GuideScreen()
                LabSection.LAUNCHED -> LaunchedEffectScreen(recorder, events)
                LabSection.DISPOSABLE -> DisposableEffectScreen(recorder, events)
                LabSection.COMPARISON -> ComparisonScreen()
                LabSection.REAL_CASES -> RealCasesScreen(recorder, events)
            }
        }
    }
}

@Composable
private fun GuideScreen() {
    val milestones = listOf(
        "1" to "Comprender cuándo entra y sale un efecto de la composición",
        "2" to "Predecir cómo las claves reinician LaunchedEffect",
        "3" to "Cancelar trabajo suspendible al cambiar clave o retirar la UI",
        "4" to "Registrar y liberar recursos con DisposableEffect",
        "5" to "Actualizar valores sin reiniciar mediante rememberUpdatedState",
        "6" to "Trasladar las reglas a dos casos cercanos a producción",
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ConceptCard(
                "Objetivo",
                "Los efectos conectan el mundo declarativo de Compose con trabajo " +
                    "externo. Antes de ejecutar, predice cuándo empezará, cuándo se " +
                    "reiniciará y qué debe liberarse.",
            )
        }
        items(milestones) { (number, description) ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Text(number, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(description)
                }
            }
        }
        item {
            ConceptCard(
                "Regla previa",
                "Los eventos de usuario normales van en onClick. Un efecto se usa " +
                    "cuando la presencia o las claves de un composable deben gobernar " +
                    "trabajo asíncrono o un recurso externo.",
            )
        }
    }
}

@Composable
private fun LaunchedEffectScreen(
    recorder: EffectEventRecorder,
    events: List<EffectEvent>,
) {
    var effectKey by remember { mutableIntStateOf(1) }
    var visible by remember { mutableStateOf(false) }
    var currentMessage by remember { mutableStateOf("mensaje A") }
    var updatedDemoVisible by remember { mutableStateOf(false) }

    LabPage(events, recorder::clear) {
        item {
            ConceptCard(
                "Predicción",
                "Activa el efecto y cambia su clave. La coroutine anterior debe " +
                    "cancelarse antes de comenzar la nueva. Ocultarlo lo retira de la " +
                    "composición y también lo cancela.",
            )
        }
        item {
            CodeBlock(
                "LaunchedEffect(effectKey) {\n" +
                    "  try { awaitCancellation() }\n" +
                    "  finally { releaseTemporaryWork() }\n" +
                    "}",
            )
        }
        item {
            Text("Clave actual: $effectKey", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Button(onClick = { visible = !visible }) {
                Text(if (visible) "Retirar de composición" else "Insertar efecto")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { effectKey++ }, enabled = visible) {
                Text("Cambiar clave")
            }
        }
        if (visible) {
            item {
                KeyedLaunchedEffectProbe(effectKey) { raw ->
                    val isStart = raw.startsWith("start")
                    recorder.record(
                        source = "LaunchedEffect",
                        message = raw,
                        kind = if (isStart) EventKind.START else EventKind.DISPOSE,
                    )
                }
            }
        }
        item {
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            ConceptCard(
                "Valor nuevo sin reiniciar",
                "Inicia la entrega y cambia el mensaje durante la espera. " +
                    "rememberUpdatedState permite entregar el valor más reciente sin " +
                    "convertirlo en una clave y sin reiniciar el efecto.",
            )
            Spacer(Modifier.height(8.dp))
            Text("Valor actual: $currentMessage")
            Spacer(Modifier.height(8.dp))
            Button(onClick = { updatedDemoVisible = true }, enabled = !updatedDemoVisible) {
                Text("Iniciar entrega en 3 s")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    currentMessage = if (currentMessage == "mensaje A") "mensaje B" else "mensaje A"
                },
                enabled = updatedDemoVisible,
            ) {
                Text("Actualizar mensaje sin reiniciar")
            }
        }
        if (updatedDemoVisible) {
            item {
                UpdatedValueEffectProbe(
                    value = currentMessage,
                    delayMillis = 3_000,
                ) { raw ->
                    recorder.record(
                        source = "rememberUpdatedState",
                        message = raw,
                        kind = if (raw == "start") EventKind.START else EventKind.INFO,
                    )
                }
            }
        }
    }
}

@Composable
private fun DisposableEffectScreen(
    recorder: EffectEventRecorder,
    events: List<EffectEvent>,
) {
    val availableKeys = listOf("GPS", "Lifecycle", "Sensor")
    var selectedKey by remember { mutableStateOf(availableKeys.first()) }
    var visible by remember { mutableStateOf(false) }
    val registry = remember(recorder) {
        FakeListenerRegistry { raw ->
            recorder.record(
                source = "DisposableEffect",
                message = raw,
                kind = if (raw.startsWith("register")) EventKind.START else EventKind.DISPOSE,
            )
        }
    }

    LabPage(events, recorder::clear) {
        item {
            ConceptCard(
                "Predicción",
                "Activa el listener. Al cambiar la clave, Compose ejecutará onDispose " +
                    "para el recurso anterior y después registrará el nuevo. Al " +
                    "ocultarlo, no debe quedar ninguna suscripción activa.",
            )
        }
        item {
            CodeBlock(
                "DisposableEffect(sourceId) {\n" +
                    "  val handle = source.register(listener)\n" +
                    "  onDispose { handle.unregister() }\n" +
                    "}",
            )
        }
        item {
            Row(Modifier.horizontalScroll(rememberScrollState())) {
                availableKeys.forEach { key ->
                    FilterChip(
                        selected = key == selectedKey,
                        onClick = { selectedKey = key },
                        label = { Text(key) },
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { visible = !visible }) {
                Text(if (visible) "Liberar listener" else "Registrar listener")
            }
            Spacer(Modifier.height(8.dp))
            Text(
                if (visible) "Recurso esperado: $selectedKey" else "Sin recurso activo",
                fontWeight = FontWeight.Bold,
            )
        }
        if (visible) {
            item { DisposableEffectProbe(selectedKey, registry) }
        }
    }
}

@Composable
private fun ComparisonScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ConceptCard(
                "LaunchedEffect",
                "Ejecuta una coroutine ligada a la composición. Se cancela al salir " +
                    "o al cambiar una clave y es apropiado para trabajo suspendible.",
            )
        }
        item {
            ConceptCard(
                "DisposableEffect",
                "Adquiere un recurso externo y exige liberarlo en onDispose. Es " +
                    "apropiado para listeners, observers y callbacks registrables.",
            )
        }
        item {
            ConceptCard(
                "rememberUpdatedState",
                "Actualiza un valor o callback capturado por un efecto de larga vida " +
                    "sin convertirlo en clave y sin reiniciar el trabajo.",
            )
        }
        item {
            CodeBlock(
                "¿Es una acción directa del usuario? → onClick\n" +
                    "¿Es trabajo suspendible ligado a la UI? → LaunchedEffect\n" +
                    "¿Hay que registrar y liberar un recurso? → DisposableEffect\n" +
                    "¿Debe cambiar un valor sin reiniciar? → rememberUpdatedState",
            )
        }
        item {
            ConceptCard(
                "Cuidado con las claves",
                "Una clave omitida conserva datos obsoletos. Una clave inestable " +
                    "reinicia demasiado trabajo. La clave debe representar exactamente " +
                    "la identidad que invalida el efecto.",
            )
        }
    }
}

@Composable
private fun RealCasesScreen(
    recorder: EffectEventRecorder,
    events: List<EffectEvent>,
) {
    var screenId by remember { mutableIntStateOf(1) }
    var analyticsVisible by remember { mutableStateOf(false) }
    var observerVisible by remember { mutableStateOf(false) }

    LabPage(events, recorder::clear) {
        item {
            ConceptCard(
                "Caso 1 · Vista estable",
                "Se envía una analítica solo si la misma pantalla permanece visible " +
                    "durante 2 segundos. Cambiar screenId cancela el envío anterior.",
            )
            Spacer(Modifier.height(8.dp))
            Text("screenId = $screenId", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Button(onClick = { analyticsVisible = !analyticsVisible }) {
                Text(if (analyticsVisible) "Ocultar pantalla" else "Mostrar pantalla")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { screenId++ }, enabled = analyticsVisible) {
                Text("Navegar a otra identidad")
            }
        }
        if (analyticsVisible) {
            item { StableScreenAnalytics(screenId, recorder) }
        }
        item {
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            ConceptCard(
                "Caso 2 · LifecycleObserver",
                "DisposableEffect registra un observer real en el LifecycleOwner " +
                    "actual y lo retira cuando el ejemplo sale de composición.",
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = { observerVisible = !observerVisible }) {
                Text(if (observerVisible) "Retirar observer" else "Registrar observer")
            }
        }
        if (observerVisible) {
            item { LifecycleObserverDemo(recorder) }
        }
    }
}

@Composable
private fun StableScreenAnalytics(screenId: Int, recorder: EffectEventRecorder) {
    LaunchedEffect(screenId) {
        recorder.record("Analítica", "espera screen=$screenId", EventKind.START)
        try {
            delay(2_000)
            recorder.record("Analítica", "enviado screen=$screenId", EventKind.INFO)
        } catch (cancelled: CancellationException) {
            recorder.record("Analítica", "cancelado screen=$screenId", EventKind.DISPOSE)
            throw cancelled
        }
    }
}

@Composable
private fun LifecycleObserverDemo(recorder: EffectEventRecorder) {
    val owner = LocalLifecycleOwner.current
    val currentRecorder by rememberUpdatedState(recorder)

    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, event ->
            currentRecorder.record("Lifecycle", event.name, EventKind.INFO)
        }
        owner.lifecycle.addObserver(observer)
        recorder.record("Lifecycle", "observer registrado", EventKind.START)

        onDispose {
            owner.lifecycle.removeObserver(observer)
            recorder.record("Lifecycle", "observer liberado", EventKind.DISPOSE)
        }
    }
}

@Composable
private fun LabPage(
    events: List<EffectEvent>,
    onClear: () -> Unit,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        content()
        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Línea temporal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (events.isNotEmpty()) OutlinedButton(onClick = onClear) { Text("Limpiar") }
            }
        }
        if (events.isEmpty()) {
            item { Text("Los eventos aparecerán aquí.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(events, key = { it.id }) { event -> EventRow(event) }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun ConceptCard(title: String, body: String) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(body, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun CodeBlock(code: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF202124), MaterialTheme.shapes.medium)
            .padding(16.dp),
    ) {
        Text(code, color = Color(0xFFE8EAED), fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun EventRow(event: EffectEvent) {
    val accent = when (event.kind) {
        EventKind.START -> Color(0xFF2457C5)
        EventKind.TICK -> Color(0xFF6A4C93)
        EventKind.INFO -> Color(0xFF22733B)
        EventKind.DISPOSE -> Color(0xFFB14B00)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .background(accent, MaterialTheme.shapes.extraSmall)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text("#${event.id}", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(event.source, fontWeight = FontWeight.Bold)
            Text(event.message)
        }
    }
}

