package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.TechPhase
import com.example.ui.theme.*
import com.example.ui.viewmodel.CybertonViewModel

@Composable
fun RoadmapPlanner(
    viewModel: CybertonViewModel,
    modifier: Modifier = Modifier
) {
    val phases by viewModel.phases.collectAsState()
    val selectedPhaseId by viewModel.selectedPhaseId.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDeepObsidian)
            .padding(16.dp)
    ) {
        // Tracker stats banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, CyberBorderNavy),
            colors = CardDefaults.cardColors(containerColor = CyberNavySlate)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "STARTUP WORKSPACE TIMELINE",
                    color = CyberNeonGreen,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                val completedCount = remember(phases) { phases.count { it.isCompleted } }
                val totalPercent = remember(phases) {
                    if (phases.isEmpty()) 0 else (phases.sumOf { it.progress } / phases.size)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Aggregated Milestones: $completedCount / ${phases.size} Completed",
                        color = CyberWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$totalPercent% TOTAL SYNCHRONIZED",
                        color = CyberNeonCyan,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { totalPercent / 100f },
                    color = CyberNeonCyan,
                    trackColor = CyberBorderNavy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }
        }

        // Live Roadmap Phases Tree
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(phases, key = { it.id }) { phase ->
                val isExpanded = selectedPhaseId == phase.id
                var draftProgress by remember(phase.id, phase.progress) { mutableFloatStateOf(phase.progress.toFloat()) }
                var draftCompleted by remember(phase.id, phase.isCompleted) { mutableStateOf(phase.isCompleted) }
                var draftNotes by remember(phase.id, phase.notes) { mutableStateOf(phase.notes) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (isExpanded) CyberNeonGreen else CyberBorderNavy,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isExpanded) CyberNavySlate else CyberNavySlate.copy(alpha = 0.6f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectPhase(phase.id) }
                            .padding(14.dp)
                    ) {
                        // Title header layout
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Stage Badge Number
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (phase.isCompleted) CyberNeonGreen else CyberBorderNavy)
                            ) {
                                if (phase.isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Done",
                                        tint = CyberDeepObsidian,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Text(
                                        text = phase.id.toString(),
                                        color = CyberWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            // Names & Descriptions
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = phase.name,
                                    color = if (phase.isCompleted) CyberWhite else CyberWhite.copy(alpha = 0.8f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = phase.subtitle,
                                    color = if (phase.isCompleted) CyberNeonGreen else CyberLightSlate,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            }

                            // Phase Progress Text Status
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${phase.progress}%",
                                    color = if (phase.isCompleted) CyberNeonGreen else CyberNeonCyan,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = if (phase.isCompleted) "VERIFIED" else "ACTIVE",
                                    color = if (phase.isCompleted) CyberNeonGreen.copy(alpha = 0.7f) else CyberWarmAmber,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Short description
                        Text(
                            text = phase.description,
                            color = CyberLightSlate,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )

                        // Technologies row
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            phase.technologies.split(",").forEach { tech ->
                                Box(
                                    modifier = Modifier
                                        .background(CyberTerminalGray, RoundedCornerShape(4.dp))
                                        .border(1.dp, CyberBorderNavy.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = tech.trim(),
                                        color = CyberNeonCyan,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        // Interactive details configuration
                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .fillMaxWidth()
                                    .border(1.dp, CyberBorderNavy, RoundedCornerShape(6.dp))
                                    .background(CyberDeepObsidian)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    "CONFIGURE DEPLOYMENT PHASES",
                                    color = CyberNeonGreen,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Completeness Toggle + Slider Slider
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "Phase Completion State:",
                                        color = CyberWhite,
                                        fontSize = 12.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Switch(
                                        checked = draftCompleted,
                                        onCheckedChange = { checked ->
                                            draftCompleted = checked
                                            if (checked) draftProgress = 100f
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = CyberDeepObsidian,
                                            checkedTrackColor = CyberNeonGreen,
                                            uncheckedThumbColor = CyberLightSlate,
                                            uncheckedTrackColor = CyberBorderNavy
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Slider custom positioning
                                Text(
                                    "Milestone Completion Metrics: ${draftProgress.toInt()}%",
                                    color = CyberWhite,
                                    fontSize = 12.sp
                                )
                                Slider(
                                    value = draftProgress,
                                    onValueChange = {
                                        draftProgress = it
                                        draftCompleted = it >= 100f
                                    },
                                    valueRange = 0f..100f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = CyberNeonCyan,
                                        activeTrackColor = CyberNeonCyan,
                                        inactiveTrackColor = CyberBorderNavy
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Developer notes logging input
                                OutlinedTextField(
                                    value = draftNotes,
                                    onValueChange = { draftNotes = it },
                                    label = { Text("Developer System Log", color = CyberLightSlate, fontSize = 11.sp) },
                                    placeholder = { 
                                        Text(
                                            "Describe blockers, custom microservice route tests or environment states...", 
                                            color = CyberLightSlate.copy(alpha = 0.4f),
                                            fontSize = 11.sp
                                        ) 
                                    },
                                    modifier = Modifier.fillMaxWidth().height(80.dp),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, color = CyberWhite, fontFamily = FontFamily.Monospace),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CyberNeonGreen,
                                        unfocusedBorderColor = CyberBorderNavy,
                                        cursorColor = CyberNeonGreen
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Database insertion triggers
                                Button(
                                    onClick = {
                                        viewModel.updatePhaseProgress(
                                            id = phase.id,
                                            progress = draftProgress.toInt(),
                                            isCompleted = draftCompleted,
                                            notes = draftNotes
                                        )
                                        Toast.makeText(context, "System configuration committed to Room database!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CyberNeonGreen,
                                        contentColor = CyberDeepObsidian
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Save Configuration",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "COMMIT CONFIGURATION TO SYSTEM INDEX",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
