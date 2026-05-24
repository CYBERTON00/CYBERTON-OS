package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.DirectoryExplorer
import com.example.ui.components.RoadmapPlanner
import com.example.ui.components.CopilotPanel
import com.example.ui.components.SystemMonitor
import com.example.ui.theme.*
import com.example.ui.viewmodel.CybertonViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.text.SimpleDateFormat
import java.util.*

enum class WorkspaceTab(val text: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    FILES("EXPLORER", Icons.Default.List),
    ROADMAP("ROADMAP", Icons.Default.Info),
    COPILOT("COPILOT API", Icons.Default.Create),
    DIAGNOSTICS("SYS_MONITOR", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                    containerColor = CyberDeepObsidian
                ) { innerPadding ->
                    CybertonAppLayout(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CybertonAppLayout(
    viewModel: CybertonViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(WorkspaceTab.FILES) }
    var utcTimeText by remember { mutableStateOf("2026-05-24 15:11:10 UTC") }

    // Live Clock Routine
    LaunchedEffect(Unit) {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        while (isActive) {
            utcTimeText = format.format(Date())
            delay(1000)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDeepObsidian)
    ) {
        // Futuristic Cyberdeck Header Panel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberNavySlate)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Brand info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Pulsing state node
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(CyberNeonCyan)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "CYBERTON-OS // v1.0.4",
                            color = CyberNeonGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Right Matrix state time tracker
                    Text(
                        text = utcTimeText,
                        color = CyberNeonCyan,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            HorizontalDivider(color = CyberBorderNavy)
        }

        // Custom Elegant M3 Rounded Tabs Selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberNavySlate)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WorkspaceTab.values().forEach { tab ->
                    val isSelected = activeTab == tab
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (isSelected) CyberMint else Color.Transparent)
                            .clickable { activeTab = tab }
                            .padding(vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.text,
                                tint = if (isSelected) CyberNeonGreen else CyberLightSlate.copy(alpha = 0.6f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.text,
                                color = if (isSelected) CyberWhite else CyberLightSlate.copy(alpha = 0.6f),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = CyberBorderNavy.copy(alpha = 0.4f))
        }

        // Active Workspace Screen Rendering
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    (fadeIn() + scaleIn(initialScale = 0.98f)) togetherWith (fadeOut() + scaleOut(targetScale = 0.98f))
                },
                label = "WorkspaceTransition"
            ) { tabSpec ->
                when (tabSpec) {
                    WorkspaceTab.FILES -> {
                        DirectoryExplorer(viewModel = viewModel)
                    }
                    WorkspaceTab.ROADMAP -> {
                        RoadmapPlanner(viewModel = viewModel)
                    }
                    WorkspaceTab.COPILOT -> {
                        CopilotPanel(viewModel = viewModel)
                    }
                    WorkspaceTab.DIAGNOSTICS -> {
                        SystemMonitor()
                    }
                }
            }
        }
    }
}
