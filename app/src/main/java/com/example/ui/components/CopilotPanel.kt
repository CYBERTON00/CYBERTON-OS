package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.CybertonViewModel
import kotlinx.coroutines.launch

@Composable
fun CopilotPanel(
    viewModel: CybertonViewModel,
    modifier: Modifier = Modifier
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    var promptInput by remember { mutableStateOf("") }
    
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val presets = listOf(
        "Draft Next.js memory graph page" to "Can you draft a Next.js React component for the memory-view page using Framer Motion?",
        "FastAPI websocket chat router" to "Write a complete FastAPI websocket router script to handle real-time agent context exchanges.",
        "ChromaDB embedding index setup" to "Show me how to initialize and index document embeddings into ChromaDB with Python.",
        "Docker Compose for databases" to "Create a docker-compose.yml setting up FastAPI, PostgreSQL, and a vector service."
    )

    // Scroll automatically on new messages
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDeepObsidian)
    ) {
        // Quick Actions Scroll Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberTerminalGray)
                .padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            Column {
                Text(
                    text = "SELECT AN ARCHITECTURAL TEMPLATE PRESET",
                    color = CyberNeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.take(2).forEach { (label, prompt) ->
                        PresetChip(label = label, isEnabled = !isGenerating) {
                            promptInput = prompt
                            viewModel.sendCopilotMessage(prompt)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.drop(2).forEach { (label, prompt) ->
                        PresetChip(label = label, isEnabled = !isGenerating) {
                            promptInput = prompt
                            viewModel.sendCopilotMessage(prompt)
                        }
                    }
                }
            }
        }

        // Chat conversation viewport (Terminal Style)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chatMessages) { message ->
                TerminalMessageBubble(message = message) { codeText ->
                    clipboardManager.setText(AnnotatedString(codeText))
                    Toast.makeText(context, "Copied code from terminal context!", Toast.LENGTH_SHORT).show()
                }
            }
            
            if (isGenerating) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = CyberNeonGreen,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Orchestrating AI reasoning nodes... blinking signal",
                            color = CyberNeonGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Input Console Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberNavySlate)
        ) {
            HorizontalDivider(color = CyberBorderNavy)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Clear button
                IconButton(
                    onClick = { viewModel.clearChat() },
                    modifier = Modifier
                        .size(38.dp)
                        .background(CyberTerminalGray, RoundedCornerShape(19.dp))
                        .border(1.dp, CyberBorderNavy, RoundedCornerShape(19.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear matrix",
                        tint = CyberErrorRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))

                // Input Box
                TextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    placeholder = { 
                        Text(
                            "Task prompt (e.g., Explain memory vectors)...", 
                            color = CyberLightSlate.copy(alpha = 0.4f),
                            fontSize = 12.sp
                        ) 
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .border(1.dp, CyberBorderNavy, RoundedCornerShape(4.dp))
                        .clip(RoundedCornerShape(4.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CyberDeepObsidian,
                        unfocusedContainerColor = CyberDeepObsidian,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = CyberWhite,
                        unfocusedTextColor = CyberWhite,
                        cursorColor = CyberNeonGreen
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send Button
                Button(
                    onClick = {
                        if (promptInput.isNotBlank()) {
                            val temp = promptInput
                            promptInput = ""
                            viewModel.sendCopilotMessage(temp)
                        }
                    },
                    enabled = promptInput.isNotBlank() && !isGenerating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberNeonGreen,
                        disabledContainerColor = CyberBorderNavy,
                        contentColor = CyberDeepObsidian,
                        disabledContentColor = CyberLightSlate.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(44.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
          }
        }
    }
}

@Composable
fun PresetChip(
    label: String,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(CyberNavySlate, RoundedCornerShape(4.dp))
            .border(1.dp, CyberBorderNavy, RoundedCornerShape(4.dp))
            .clickable(enabled = isEnabled) { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = if (isEnabled) CyberNeonGreen else CyberLightSlate.copy(alpha = 0.4f),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun TerminalMessageBubble(
    message: ChatMessage,
    onCopyCode: (String) -> Unit
) {
    val isUser = message.author == "user"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Author signifier
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (isUser) CyberNeonCyan else CyberNeonGreen)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isUser) "root@user:~$" else "CYBERTON-ARCHITECT:~$",
                color = if (isUser) CyberNeonCyan else CyberNeonGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Message text box or parsed code block
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isUser) CyberNavySlate.copy(alpha = 0.4f) else CyberNavySlate)
                .border(
                    width = 1.dp,
                    color = if (isUser) CyberBorderNavy else CyberBorderNavy.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(12.dp)
        ) {
            Column {
                val parsedContent = remember(message.content) { parseMarkdown(message.content) }
                parsedContent.forEach { part ->
                    when (part) {
                        is ParsedElement.TextPart -> {
                            Text(
                                text = part.annotatedString,
                                color = CyberWhite,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                        is ParsedElement.CodeBlockPart -> {
                            TerminalCodeDisplay(
                                code = part.codeText,
                                language = part.language,
                                onCopyClick = { onCopyCode(part.codeText) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TerminalCodeDisplay(
    code: String,
    language: String,
    onCopyClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(1.dp, CyberBorderNavy, RoundedCornerShape(4.dp)),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = CyberDeepObsidian)
    ) {
        Column {
            // Header bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberTerminalGray)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "SYNTHESIZED RESOURCE [${language.uppercase()}]",
                        color = CyberNeonGreen,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onCopyClick,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share, 
                            contentDescription = "Copy code block", 
                            tint = CyberNeonGreen,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
            HorizontalDivider(color = CyberBorderNavy)
            // Code Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = code,
                    color = CyberWhite,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// --- Dynamic Simple Markdown and Code Block Parser ---

sealed class ParsedElement {
    data class TextPart(val annotatedString: AnnotatedString) : ParsedElement()
    data class CodeBlockPart(val codeText: String, val language: String) : ParsedElement()
}

fun parseMarkdown(text: String): List<ParsedElement> {
    val elements = mutableListOf<ParsedElement>()
    val codePattern = "```"
    
    if (!text.contains(codePattern)) {
        elements.add(ParsedElement.TextPart(formatStyledText(text)))
        return elements
    }

    val parts = text.split(codePattern)
    for (i in parts.indices) {
        val part = parts[i]
        if (i % 2 == 1) { // It's inside a code block
            val lines = part.split("\n")
            val lang = lines.firstOrNull()?.trim() ?: "code"
            val actualLines = lines.drop(1).joinToString("\n")
            elements.add(ParsedElement.CodeBlockPart(actualLines, if (lang.isEmpty()) "code" else lang))
        } else {
            if (part.isNotBlank()) {
                elements.add(ParsedElement.TextPart(formatStyledText(part)))
            }
        }
    }
    return elements
}

fun formatStyledText(raw: String): AnnotatedString {
    return buildAnnotatedString {
        val boldPattern = "\\*\\*(.*?)\\*\\*".toRegex()
        var lastIdx = 0
        val matches = boldPattern.findAll(raw)

        for (match in matches) {
            val start = match.range.first
            val end = match.range.last + 1
            // Append standard text preceding the find
            append(raw.substring(lastIdx, start))
            
            // Append styled bold
            withStyle(style = SpanStyle(color = CyberNeonGreen, fontWeight = FontWeight.Bold)) {
                append(match.groupValues[1])
            }
            lastIdx = end
        }
        if (lastIdx < raw.length) {
            append(raw.substring(lastIdx))
        }
    }
}
