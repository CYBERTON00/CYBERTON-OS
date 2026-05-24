package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

data class ServiceState(
    val name: String,
    val port: String,
    val status: String, // "ONLINE", "ACTIVE", "READY", "OFFLINE"
    val color: Color
)

@Composable
fun SystemMonitor(
    modifier: Modifier = Modifier
) {
    // Dynamic values for dials
    var cpuLoad by remember { mutableFloatStateOf(44.2f) }
    var memoryUsage by remember { mutableFloatStateOf(68.5f) }
    var vectorIndexes by remember { mutableIntStateOf(1024) }
    
    // Log stream state
    val logMessages = remember { mutableStateListOf<String>() }
    val lazyListState = rememberLazyListState()

    val services = listOf(
        ServiceState("Turborepo Monorepo", "WS_ROOT", "ONLINE", CyberNeonGreen),
        ServiceState("Chrome Assistant Ext", "PORT 8222", "ONLINE", CyberNeonGreen),
        ServiceState("NextJS Web Dashboard", "PORT 3000", "READY", CyberNeonCyan),
        ServiceState("FastAPI Backend Core", "PORT 8000", "ACTIVE", CyberNeonGreen),
        ServiceState("ChromaDB Vector Store", "PORT 8001", "ONLINE", CyberNeonGreen),
        ServiceState("Playwright Web Sandbox", "PORT 4444", "ONLINE", CyberNeonGreen),
        ServiceState("Whisper Vocal Synthesizer", "A_SYNC", "READY", CyberNeonGreen),
        ServiceState("Local Ollama Server", "PORT 11434", "OFFLINE / LOCAL", CyberWarmAmber)
    )

    val diagnosticTemplates = listOf(
        "GET /api/v1/memory/search - ChromaDB Query took 14ms (found 3 matches)",
        "WebSocket connecting... Handshake completed with Chrome Extension Client",
        "Reasoning Engine outputted planning instructions, score: 0.94",
        "Playwright Sandbox launcher starting automated headless browser execution",
        "Transcribing audio buffer input stream... Acoustic analysis success",
        "POST /api/v1/vector/embed - Added document chunk: reasoning-engine.ts",
        "Garbage collection executed. Memory reclaimed: 142MB",
        "Turborepo cache hit in apps/dashboard. Compilation skipped [elapsed: 111ms]",
        "Incoming vocal wave trigger detected matching wakeword: 'Hey Cyberton'",
        "Syncing short-term chat lists to long-term SQL vector graph database"
    )

    // Animated wobbles for dials and logging stream execution
    LaunchedEffect(Unit) {
        // Hydrate logs initially
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        repeat(6) { index ->
            val time = dateFormat.format(Date(System.currentTimeMillis() - (6 - index) * 5000))
            val log = diagnosticTemplates[index % diagnosticTemplates.size]
            logMessages.add("[$time] SYS_KERNEL_LOG: $log")
        }

        while (isActive) {
            delay(2200)
            // Wobble CPU Loading
            cpuLoad = (35f + Random.nextFloat() * 40f).coerceIn(10f, 95f)
            // Drift Memory
            memoryUsage = (62f + Random.nextFloat() * 10f).coerceIn(40f, 98f)
            // Increment indexes occasionally
            if (Random.nextBoolean()) {
                vectorIndexes += Random.nextInt(1, 4)
            }

            // Append new system log
            val time = dateFormat.format(Date())
            val randomLog = diagnosticTemplates.random()
            logMessages.add("[$time] SYS_KERNEL_LOG: $randomLog")
            if (logMessages.size > 24) {
                logMessages.removeAt(0)
            }
            
            // Auto scroll is handled safely inside composable
            delay(10)
            if (logMessages.isNotEmpty()) {
                lazyListState.animateScrollToItem(logMessages.size - 1)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDeepObsidian)
            .padding(16.dp)
    ) {
        // System Gauges (Top Row containing native canvas arc drawings)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GaugeCard(
                title = "CORE API LOAD",
                value = "${cpuLoad.toInt()}%",
                progress = cpuLoad / 100f,
                accentColor = CyberNeonGreen,
                modifier = Modifier.weight(1f)
            )
            GaugeCard(
                title = "MEMORY ALLOC",
                value = "${memoryUsage.toInt()}%",
                progress = memoryUsage / 100f,
                accentColor = CyberNeonCyan,
                modifier = Modifier.weight(1f)
            )
            GaugeCard(
                title = "VECTORS INDEXED",
                value = "$vectorIndexes keys",
                progress = (vectorIndexes % 100) / 100f,
                accentColor = CyberWarmAmber,
                modifier = Modifier.weight(1f)
            )
        }

        // Services endpoints mapping (Middle Box)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f)
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, CyberBorderNavy),
            colors = CardDefaults.cardColors(containerColor = CyberNavySlate)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "NETWORK GATEWAYS & STATUS SERVICE MONITOR",
                    color = CyberNeonGreen,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(services) { svc ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberTerminalGray.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .border(1.dp, CyberBorderNavy.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(svc.color)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = svc.name,
                                color = CyberWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = svc.port,
                                color = CyberLightSlate,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            Text(
                                text = svc.status,
                                color = svc.color,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Live Diagnostic Terminal Logs (Bottom Box)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, CyberBorderNavy),
            colors = CardDefaults.cardColors(containerColor = CyberDeepObsidian)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.Red)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "REALTIME COGNITIVE SYSTEM LOG TRACE",
                        color = Color.Red,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(color = CyberBorderNavy)

                // Log Window Scroll
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 6.dp)
                ) {
                    items(logMessages) { log ->
                        Text(
                            text = log,
                            color = CyberLightSlate,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GaugeCard(
    title: String,
    value: String,
    progress: Float,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, CyberBorderNavy, RoundedCornerShape(6.dp)),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySlate)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(10.dp)
        ) {
            Text(
                text = title,
                color = CyberLightSlate,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            
            Spacer(modifier = Modifier.height(10.dp))

            // Canvas Native Dial Arc Drawing
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(54.dp)
            ) {
                Canvas(modifier = Modifier.size(48.dp)) {
                    // Back circle arc track
                    drawCircle(
                        color = CyberBorderNavy,
                        radius = size.width / 2f,
                        style = Stroke(width = 4.dp.toPx())
                    )
                    // Front progress arc stroke
                    drawArc(
                        color = accentColor,
                        startAngle = -90f,
                        sweepAngle = progress * 360f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                
                Text(
                    text = value.substringBefore(" "), // short label
                    color = CyberWhite,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (value.contains("keys")) value else "STATUS NOMINAL",
                color = if (value.contains("keys")) CyberNeonGreen else accentColor,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
