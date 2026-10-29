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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.example.composeeffectlab.R
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

private enum class LabSection(val labelRes: Int) {
    GUIDE(R.string.tab_guide),
    LAUNCHED(R.string.tab_launched),
    UPDATED(R.string.tab_updated),
    DISPOSABLE(R.string.tab_disposable),
    COMPARISON(R.string.tab_comparison),
    REAL_CASES(R.string.tab_cases),
}

@Composable
fun ComposeEffectLabApp(onEventsChanged: (List<EffectEvent>) -> Unit = {}) {
    var selected by remember { mutableStateOf(LabSection.GUIDE) }
    var events by remember { mutableStateOf(emptyList<EffectEvent>()) }
    val currentObserver by rememberUpdatedState(onEventsChanged)
    val recorder = remember { EffectEventRecorder { events = it; currentObserver(it) } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
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
                        text = { Text(stringResource(section.labelRes)) },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            when (selected) {
                LabSection.GUIDE -> GuideScreen()
                LabSection.LAUNCHED -> LaunchedEffectScreen(recorder, events)
                LabSection.UPDATED -> UpdatedValueScreen(recorder, events)
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
        "1" to stringResource(R.string.guide_one),
        "2" to stringResource(R.string.guide_two),
        "3" to stringResource(R.string.guide_three),
        "4" to stringResource(R.string.guide_four),
        "5" to stringResource(R.string.guide_five),
        "6" to stringResource(R.string.guide_six),
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            ConceptSection(
                stringResource(R.string.guide_title),
                stringResource(R.string.guide_body),
            )
        }
        items(milestones) { (number, description) ->
            Column {
                Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.Top) {
                    Text(number, modifier = Modifier.width(32.dp), color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge)
                    Text(description, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                }
                HorizontalDivider()
            }
        }
        item {
            ConceptSection(
                stringResource(R.string.rule_title),
                stringResource(R.string.rule_body),
            )
        }
    }
}

@Composable
private fun LaunchedEffectScreen(recorder: EffectEventRecorder, events: List<EffectEvent>) {
    var effectKey by remember { mutableIntStateOf(1) }
    var visible by remember { mutableStateOf(false) }

    // The screen owns the experiment. Scrolling a lazy item does not withdraw it.
    if (visible) {
        KeyedLaunchedEffectProbe(effectKey) { raw ->
            recorder.record("LaunchedEffect", raw, if (raw.startsWith("start")) EventKind.START else EventKind.DISPOSE)
        }
    }
    LabPage(events, recorder::clear) {
        item { ConceptSection(stringResource(R.string.launched_title), stringResource(R.string.launched_body)) }
        item { CodeBlock("LaunchedEffect(key) {\n  awaitCancellation()\n}") }
        item {
            Text(stringResource(R.string.current_key, effectKey), style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(12.dp))
            Button(shape = MaterialTheme.shapes.medium, onClick = { visible = !visible }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(stringResource(if (visible) R.string.remove_effect else R.string.insert_effect))
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(shape = MaterialTheme.shapes.medium, onClick = { effectKey++ }, enabled = visible, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(stringResource(R.string.change_key))
            }
        }
    }
}

@Composable
private fun UpdatedValueScreen(recorder: EffectEventRecorder, events: List<EffectEvent>) {
    val messageA = stringResource(R.string.message_a)
    val messageB = stringResource(R.string.message_b)
    var currentMessage by remember { mutableStateOf(messageA) }
    var running by remember { mutableStateOf(false) }
    if (running) {
        UpdatedValueEffectProbe(currentMessage, 3_000) { raw ->
            recorder.record("rememberUpdatedState", raw, if (raw == "start") EventKind.START else EventKind.INFO)
            if (raw.startsWith("delivered:")) running = false
        }
    }
    LabPage(events, recorder::clear) {
        item { ConceptSection(stringResource(R.string.updated_title), stringResource(R.string.updated_body)) }
        item { CodeBlock("val latest by rememberUpdatedState(value)\nLaunchedEffect(Unit) {\n  delay(3_000)\n  deliver(latest)\n}") }
        item {
            Text(stringResource(R.string.current_message, currentMessage), style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(12.dp))
            Button(shape = MaterialTheme.shapes.medium, onClick = { running = true }, enabled = !running, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(stringResource(R.string.start_delivery))
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(shape = MaterialTheme.shapes.medium, onClick = { currentMessage = if (currentMessage == messageA) messageB else messageA },
                enabled = running, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(stringResource(R.string.update_message))
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

    if (visible) DisposableEffectProbe(selectedKey, registry)
    LabPage(events, recorder::clear) {
        item {
            ConceptSection(
                stringResource(R.string.disposable_title),
                stringResource(R.string.disposable_body),
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
                    FilterChip(shape = MaterialTheme.shapes.medium,
                        selected = key == selectedKey,
                        onClick = { selectedKey = key },
                        label = { Text(key) },
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(shape = MaterialTheme.shapes.medium, onClick = { visible = !visible }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(if (visible) stringResource(R.string.release_listener) else stringResource(R.string.register_listener))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                if (visible) stringResource(R.string.current_resource, selectedKey) else stringResource(R.string.no_resource),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun ComparisonScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            ConceptSection(
                "LaunchedEffect",
                stringResource(R.string.launched_explanation),
            )
        }
        item {
            ConceptSection(
                "DisposableEffect",
                stringResource(R.string.disposable_explanation),
            )
        }
        item {
            ConceptSection(
                "rememberUpdatedState",
                stringResource(R.string.updated_explanation),
            )
        }
        item {
            CodeBlock(
                stringResource(R.string.decision_guide),
            )
        }
        item {
            ConceptSection(
                stringResource(R.string.keys_title),
                stringResource(R.string.keys_body),
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

    if (analyticsVisible) StableScreenAnalytics(screenId, recorder)
    if (observerVisible) LifecycleObserverDemo(recorder)
    LabPage(events, recorder::clear) {
        item {
            ConceptSection(
                stringResource(R.string.analytics_title),
                stringResource(R.string.analytics_body),
            )
            Spacer(Modifier.height(8.dp))
            Text("screenId = $screenId", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Button(shape = MaterialTheme.shapes.medium, onClick = { analyticsVisible = !analyticsVisible }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(if (analyticsVisible) stringResource(R.string.hide_screen) else stringResource(R.string.show_screen))
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(shape = MaterialTheme.shapes.medium, onClick = { screenId++ }, enabled = analyticsVisible, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(stringResource(R.string.change_screen))
            }
        }
        item {
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            ConceptSection(
                stringResource(R.string.lifecycle_title),
                stringResource(R.string.lifecycle_body),
            )
            Spacer(Modifier.height(8.dp))
            Button(shape = MaterialTheme.shapes.medium, onClick = { observerVisible = !observerVisible }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(if (observerVisible) stringResource(R.string.remove_observer) else stringResource(R.string.register_observer))
            }
        }
    }
}

@Composable
private fun StableScreenAnalytics(screenId: Int, recorder: EffectEventRecorder) {
    val source = stringResource(R.string.analytics_source)
    val waiting = stringResource(R.string.analytics_wait, screenId)
    val sent = stringResource(R.string.analytics_sent, screenId)
    val cancelledMessage = stringResource(R.string.analytics_cancel, screenId)
    LaunchedEffect(screenId) {
        recorder.record(source, waiting, EventKind.START)
        try {
            delay(2_000)
            recorder.record(source, sent, EventKind.INFO)
        } catch (cancelled: CancellationException) {
            recorder.record(source, cancelledMessage, EventKind.DISPOSE)
            throw cancelled
        }
    }
}

@Composable
private fun LifecycleObserverDemo(recorder: EffectEventRecorder) {
    val owner = LocalLifecycleOwner.current
    val currentRecorder by rememberUpdatedState(recorder)

    val registered = stringResource(R.string.observer_registered)
    val released = stringResource(R.string.observer_released)
    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, event ->
            currentRecorder.record("Lifecycle", event.name, EventKind.INFO)
        }
        owner.lifecycle.addObserver(observer)
        recorder.record("Lifecycle", registered, EventKind.START)

        onDispose {
            owner.lifecycle.removeObserver(observer)
            recorder.record("Lifecycle", released, EventKind.DISPOSE)
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
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        content()
        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.timeline_title), modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
                if (events.isNotEmpty()) OutlinedButton(shape = MaterialTheme.shapes.medium, onClick = onClear) { Text(stringResource(R.string.clear)) }
            }
        }
        if (events.isEmpty()) {
            item { Text(stringResource(R.string.timeline_empty), color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(events, key = { it.id }) { event -> EventRow(event) }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun ConceptSection(title: String, body: String) {
    Column {
        Text(title, modifier = Modifier.semantics { heading() }, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(body, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun CodeBlock(code: String) {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium)
        .padding(16.dp)) {
        Text(code, color = MaterialTheme.colorScheme.onPrimaryContainer, fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun EventRow(event: EffectEvent) {
    val label = when (event.kind) {
        EventKind.START -> R.string.event_start
        EventKind.TICK -> R.string.event_tick
        EventKind.INFO -> R.string.event_info
        EventKind.DISPOSE -> R.string.event_dispose
    }
    Column(Modifier.semantics(mergeDescendants = true) {}) {
        Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.Top) {
            Text("${event.id}", modifier = Modifier.width(32.dp), style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f)) {
                Text(event.source, style = MaterialTheme.typography.titleSmall)
                Text(stringResource(label), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
                Text(event.message, style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace)
            }
        }
        HorizontalDivider()
    }
}
