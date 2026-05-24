package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.CybertonViewModel
import com.example.ui.viewmodel.FileNode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryExplorer(
    viewModel: CybertonViewModel,
    modifier: Modifier = Modifier
) {
    val fileNodes by viewModel.fileNodes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedNode by viewModel.selectedFileNode.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val aiError by viewModel.aiErrorLog.collectAsState()
    val generatedCode by viewModel.generatedBoilerplate.collectAsState()
    
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // Derived list based on search and hierarchy visibility
    val visibleNodes = remember(fileNodes, searchQuery) {
        if (searchQuery.isNotBlank()) {
            fileNodes.filter { 
                it.name.contains(searchQuery, ignoreCase = true) || 
                it.path.contains(searchQuery, ignoreCase = true) 
            }
        } else {
            // Filter nodes whose parents are collapsed
            val list = mutableListOf<FileNode>()
            var skipUnderPath: String? = null
            
            for (node in fileNodes) {
                if (skipUnderPath != null && node.path.startsWith(skipUnderPath)) {
                    continue
                } else {
                    skipUnderPath = null
                }
                
                list.add(node)
                
                if (node.isDirectory && !node.isExpanded) {
                    skipUnderPath = node.path + "/"
                }
            }
            list
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDeepObsidian)
    ) {
        // Search header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(CyberNavySlate, RoundedCornerShape(8.dp))
                .border(1.dp, CyberBorderNavy, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search icon",
                    tint = CyberNeonGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { 
                        Text(
                            "root@cyberton-os:~/grep -rI filename ...", 
                            color = CyberLightSlate.copy(alpha = 0.5f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        ) 
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = CyberWhite,
                        unfocusedTextColor = CyberWhite,
                        cursorColor = CyberNeonGreen
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                )
            }
        }

        // Two pane adaptive layout
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Folder Explorer Pane (Top/Left)
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(CyberNavySlate, RoundedCornerShape(8.dp))
                    .border(1.dp, CyberBorderNavy, RoundedCornerShape(8.dp))
            ) {
                // Console bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberTerminalGray, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "WORKSPACE_TREE: pnpm / monorepo (${fileNodes.size} files)",
                            color = CyberNeonGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(Color.Red))
                            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(Color.Yellow))
                            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(Color.Green))
                        }
                    }
                }

                HorizontalDivider(color = CyberBorderNavy)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    if (visibleNodes.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No nodes match standard filesystem grep",
                                    color = CyberLightSlate,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    items(visibleNodes, key = { it.path }) { node ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (selectedNode?.path == node.path) CyberBorderNavy.copy(alpha = 0.5f) 
                                    else Color.Transparent
                                )
                                .clickable {
                                    if (node.isDirectory) {
                                        viewModel.toggleFolder(node.path)
                                    } else {
                                        viewModel.selectFileNode(node)
                                    }
                                }
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                                .padding(start = (node.level * 14).dp)
                        ) {
                            Icon(
                                imageVector = if (node.isDirectory) {
                                    if (node.isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp
                                } else {
                                    Icons.Default.PlayArrow
                                },
                                contentDescription = if (node.isDirectory) "Collapse/Expand" else "File token",
                                tint = if (node.isDirectory) CyberNeonCyan else CyberLightSlate.copy(alpha = 0.4f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (node.isDirectory) "${node.name}/" else node.name,
                                color = if (node.isDirectory) CyberNeonCyan else CyberWhite,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = if (node.isDirectory) FontWeight.SemiBold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            if (node.techStack.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .border(1.dp, CyberBorderNavy, RoundedCornerShape(4.dp))
                                        .background(CyberTerminalGray)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = node.techStack.split(",").first(),
                                        color = CyberNeonGreen,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // File Details Panel / Code Generator (Bottom/Right)
            Card(
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, CyberBorderNavy),
                colors = CardDefaults.cardColors(containerColor = CyberNavySlate)
            ) {
                selectedNode?.let { node ->
                    Column(modifier = Modifier.fillMaxSize()) {
                        // File Header details
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberTerminalGray)
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "FILE: /${node.path}",
                                        color = CyberNeonCyan,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = node.techStack,
                                        color = CyberWarmAmber,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(
                                    text = node.description,
                                    color = CyberLightSlate,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        // Code synthesis section
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(CyberDeepObsidian)
                                .padding(8.dp)
                        ) {
                            val codeToShow = generatedCode ?: node.defaultSnippet.ifEmpty { 
                                "// No synthesized content exists in vector cache.\n// Tap 'SYNTHESIZE CODE' below to load structural boilerplate with global system instructions." 
                            }
                            
                            Box(modifier = Modifier.fillMaxSize()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .horizontalScroll(rememberScrollState())
                                        .padding(bottom = 44.dp)
                                ) {
                                    Text(
                                        text = codeToShow,
                                        color = if (generatedCode != null || node.defaultSnippet.isNotEmpty()) CyberWhite else CyberLightSlate.copy(alpha = 0.5f),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp
                                    )
                                }

                                // Interactive float tools inside code block
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (codeToShow.isNotBlank() && !codeToShow.startsWith("// No synthesized")) {
                                        IconButton(
                                            onClick = {
                                                val textToCopy = if (codeToShow.contains("```")) {
                                                    // strip markdown code blocks
                                                    codeToShow.substringAfter("```").substringBeforeLast("```")
                                                } else codeToShow
                                                clipboardManager.setText(AnnotatedString(textToCopy))
                                                Toast.makeText(context, "Copied boilerplate!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(CyberTerminalGray, RoundedCornerShape(18.dp))
                                                .border(1.dp, CyberBorderNavy, RoundedCornerShape(18.dp))
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share, 
                                                contentDescription = "Copy code", 
                                                tint = CyberNeonGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { viewModel.generateFileBoilerplate(node) },
                                        enabled = !isGenerating,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = CyberTerminalGray,
                                            contentColor = CyberNeonGreen
                                        ),
                                        border = BorderStroke(1.dp, if (isGenerating) CyberBorderNavy else CyberNeonGreen),
                                        modifier = Modifier.height(36.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                    ) {
                                        if (isGenerating) {
                                            CircularProgressIndicator(
                                                color = CyberNeonGreen,
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("CONNECTING MATRIX...", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "AI Generate",
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("SYNTHESIZE BOILERPLATE", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                }
                            }
                        }

                        // Display error row if failed
                        aiError?.let { err ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CyberErrorRed.copy(alpha = 0.15f))
                                    .border(1.dp, CyberErrorRed, RoundedCornerShape(2.dp))
                                    .padding(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Info, 
                                        contentDescription = "Api Error", 
                                        tint = CyberErrorRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = err,
                                        color = CyberErrorRed,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                } ?: run {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Select prompt",
                                tint = CyberLightSlate.copy(alpha = 0.3f),
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "SELECT A WORKSPACE FILE TO INSPECT DETAILS",
                                color = CyberLightSlate.copy(alpha = 0.5f),
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
