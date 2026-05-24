package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import com.example.data.database.AppDatabase
import com.example.data.entity.TechPhase
import com.example.data.repository.TechPhaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class FileNode(
    val path: String,
    val name: String,
    val isDirectory: Boolean,
    val level: Int,
    val description: String,
    val techStack: String = "",
    val isExpanded: Boolean = true,
    val defaultSnippet: String = ""
)

data class ChatMessage(
    val author: String, // "user" or "ai"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

class CybertonViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = TechPhaseRepository(db.techPhaseDao())

    // UI state flows
    val phases: StateFlow<List<TechPhase>> = repository.allPhases
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedPhaseId = MutableStateFlow<Int?>(null)
    val selectedPhaseId: StateFlow<Int?> = _selectedPhaseId.asStateFlow()

    // File node state flow (allows expanding/collapsing nodes)
    private val _fileNodes = MutableStateFlow<List<FileNode>>(emptyList())
    val fileNodes: StateFlow<List<FileNode>> = _fileNodes.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFileNode = MutableStateFlow<FileNode?>(null)
    val selectedFileNode: StateFlow<FileNode?> = _selectedFileNode.asStateFlow()

    // Copilot State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("ai", "Systems initialized. Welcome to the **CYBERTON-OS Architect Terminal**. Ask me anything about configuring your multi-agent orchestrator, FastAPI websockets, ChromaDB embeddings, or generating custom files!")
    ))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _aiErrorLog = MutableStateFlow<String?>(null)
    val aiErrorLog: StateFlow<String?> = _aiErrorLog.asStateFlow()

    private val _generatedBoilerplate = MutableStateFlow<String?>(null)
    val generatedBoilerplate: StateFlow<String?> = _generatedBoilerplate.asStateFlow()

    init {
        initializePhases()
        initializeFileTree()
    }

    private fun initializePhases() {
        viewModelScope.launch {
            val currentPhases = repository.allPhases.first()
            if (currentPhases.isEmpty()) {
                val initialList = listOf(
                        TechPhase(
                            id = 1,
                            name = "Extension Workspace",
                            subtitle = "Phase 1: Chrome Assistant",
                            description = "Initial entry point: Injecting dom-reader, managing service-workers, tab manager events, and creating the visual panel.",
                            technologies = "TypeScript, React, Manifest V3",
                            notes = "Drafted background manifest structure and content injection scripts.",
                            progress = 100,
                            isCompleted = true
                        ),
                        TechPhase(
                            id = 2,
                            name = "Main AI Dashboard",
                            subtitle = "Phase 2: Management Engine",
                            description = "Unified management UI compiling analytics, historical system logs, node configuration states, and memory maps.",
                            technologies = "Next.js, Tailwind, Turborepo",
                            notes = "Main workspace configured using Turborepo shared packages.",
                            progress = 85,
                            isCompleted = false
                        ),
                        TechPhase(
                            id = 3,
                            name = "Backend microservice core",
                            subtitle = "Phase 3: Realtime Routing",
                            description = "FastAPI endpoints coordinating authenticated agent connections, real-time message exchange and session streaming.",
                            technologies = "FastAPI, WebSockets, Python",
                            notes = "Routing layers set up. Currently integrating agent scheduler.",
                            progress = 60,
                            isCompleted = false
                        ),
                        TechPhase(
                            id = 4,
                            name = "Vector memory layout",
                            subtitle = "Phase 4: Semantic Graph",
                            description = "Indexing and retrieval pipeline using ChromaDB embeddings for short-term session state and infinite long-term context recall.",
                            technologies = "ChromaDB, SQLite, SentenceTransformers",
                            notes = "",
                            progress = 15,
                            isCompleted = false
                        ),
                        TechPhase(
                            id = 5,
                            name = "Realtime Sound & Voice OS",
                            subtitle = "Phase 5: Cognitive Vocalizer",
                            description = "Integrating whisper transcripts, custom TTS voice feeds, stream sync-loops, and wake-word listeners.",
                            technologies = "OpenAI Whisper, ElevenLabs SDK",
                            notes = "",
                            progress = 0,
                            isCompleted = false
                        ),
                        TechPhase(
                            id = 6,
                            name = "Automation sandbox",
                            subtitle = "Phase 6: Browser Run-Loop",
                            description = "Playwright/Puppeteer script triggers authorizing browser clicks, interactive reads, and script automation tasks.",
                            technologies = "Playwright, Puppeteer, Python-Shell",
                            notes = "",
                            progress = 0,
                            isCompleted = false
                        ),
                        TechPhase(
                            id = 7,
                            name = "Multi-Agent Coordinator",
                            subtitle = "Phase 7: Reason & Orchestrate",
                            description = "High-level planning engine utilizing reflections, orchestrator queues, tool builders, and self-validation runs.",
                            technologies = "DeepMind Models, LangChain Core",
                            notes = "",
                            progress = 0,
                            isCompleted = false
                        ),
                        TechPhase(
                            id = 8,
                            name = "Electron Desktop Wrapper",
                            subtitle = "Phase 8: Hardware bridge",
                            description = "Packaging CYBERTON OS inside Electron workspace to support system notifications and hardware telemetry listeners.",
                            technologies = "Electron, Node-SPI, Rust-Ffi",
                            notes = "",
                            progress = 0,
                            isCompleted = false
                        )
                    )
                    repository.insertPhases(initialList)
                }
        }
    }

    private fun initializeFileTree() {
        val rootNodes = listOf(
            FileNode("apps", "apps", true, 0, "Host repository for the user-facing workspaces", "Pnpm Workspaces"),
            FileNode("apps/extension", "extension", true, 1, "Chrome extension injecting DOM-analyzers & chat assistant views", "Manifest V3, React"),
            FileNode("apps/extension/manifest.json", "manifest.json", false, 2, "Declares runtime permissions, background scripts, and script injection rules", "JSON", false, """{
  "manifest_version": 3,
  "name": "Cyberton-OS Core Companion",
  "version": "1.0.0",
  "permissions": ["activeTab", "sidePanel", "scripting", "storage"],
  "background": {
    "service_worker": "src/background/service-worker.ts"
  }
}"""),
            FileNode("apps/extension/src", "src", true, 2, "Core TS sources of the extension companion", "TypeScript"),
            FileNode("apps/extension/src/background", "background", true, 3, "Service worker orchestrating cross-tab router", "NodeJS"),
            FileNode("apps/extension/src/background/service-worker.ts", "service-worker.ts", false, 4, "Initializes runtime handshake, processes DOM nodes, and triggers sidepanels", "TypeScript", false, """// Chrome Extension Background Service Worker
chrome.runtime.onInstalled.addListener(() => {
  console.log("CYBERTON OS background service worker successfully initialized!");
});"""),
            FileNode("apps/extension/src/background/tab-manager.ts", "tab-manager.ts", false, 4, "Tracks active browser tab states", "TypeScript", false, "// tab tracking state"),
            FileNode("apps/extension/src/content", "content", true, 3, "DOM readers and dynamic screen trackers", "TypeScript"),
            FileNode("apps/extension/src/content/content-script.ts", "content-script.ts", false, 4, "Parses structural DOM, extracts metadata, and posts state to background worker", "TypeScript", false, "console.log('content script loaded');"),
            FileNode("apps/extension/src/sidepanel", "sidepanel", true, 3, "Aesthetic side pane workspace panels", "React, Tailwind"),
            FileNode("apps/extension/src/sidepanel/ChatInterface.tsx", "ChatInterface.tsx", false, 4, "Main sidebar chat window supporting dynamic styling and prompt entries", "React (TSX)", false, "export default function ChatInterface() { return <div>Chat</div>; }"),
            FileNode("apps/extension/src/ai", "ai", true, 3, "Local reasoning managers and context collectors", "TypeScript"),
            FileNode("apps/extension/src/ai/reasoning-engine.ts", "reasoning-engine.ts", false, 4, "Decides if local tools, memory indexes, or global API router is dispatched", "TypeScript", false, "// logic"),
            
            FileNode("apps/dashboard", "dashboard", true, 1, "Unified React/Next.js monitoring board", "Next.js, Tailwind"),
            FileNode("apps/dashboard/src", "src", true, 2, "React core app configurations", "TypeScript, React"),
            FileNode("apps/dashboard/src/app", "app", true, 3, "Next.js routing folders and main views", "React Server Components"),
            FileNode("apps/dashboard/src/app/page.tsx", "page.tsx", false, 4, "Main home control displaying nodes connectivity, active agent tasks, and memory state", "React (TSX)", false, "export default function Page() { return <h1>Dashboard</h1>; }"),
            FileNode("apps/dashboard/src/components", "components", true, 3, "Reusable reactive page parts", "React, Tailwind, Framer Motion"),
            FileNode("apps/dashboard/src/components/analytics", "analytics", true, 4, "Visual stats components", "React"),
            FileNode("apps/dashboard/src/components/analytics/MemoryView.tsx", "MemoryView.tsx", false, 5, "Framer-motion powered coordinates graphing system tracking index nodes", "React (TSX)"),

            FileNode("backend", "backend", true, 0, "Container core written in FastAPI and Python", "FastAPI, Python"),
            FileNode("backend/api", "api", true, 1, "Microservice network protocols", "FastAPI"),
            FileNode("backend/api/server.py", "server.py", false, 2, "Initializes CORS middleware, loads configurations, runs websocket servers, and routes database", "Python (FastAPI)", false, """from fastapi import FastAPI
app = FastAPI(title="CYBERTON-OS Core API")
"""),
            FileNode("backend/api/routes", "routes", true, 2, "Individual service endpoints", "Python"),
            FileNode("backend/api/routes/chat.py", "chat.py", false, 3, "Streams tokens synchronously with sliding context support to prevent overflows", "Python", false, "# chat websocket"),
            FileNode("backend/api/routes/memory.py", "memory.py", false, 3, "Triggers vector DB insertion/query loops to associate state semantically", "Python"),
            
            FileNode("backend/ai", "ai", true, 1, "Local and cloud-based deep learning orchestration", "Ollama, LangChain"),
            FileNode("backend/ai/models", "models", true, 2, "Custom configurations of individual llm servers", "Ollama, DeepSeek"),
            FileNode("backend/ai/models/deepseek", "deepseek", true, 3, "DeepSeek endpoint config files", "Python"),
            FileNode("backend/ai/models/deepseek/client.py", "client.py", false, 4, "Sends inputs to deepseek-reasoner using custom client temperature models", "Python"),
            FileNode("backend/ai/orchestration", "orchestration", true, 2, "Multi-agent dispatch structures", "Python"),
            FileNode("backend/ai/orchestration/dispatcher.py", "dispatcher.py", false, 3, "Schedules workflow tasks and verifies intermediate solver nodes", "Python"),

            FileNode("backend/memory", "memory", true, 1, "Persistent memory layouts", "ChromaDB, SQLite"),
            FileNode("backend/memory/vector", "vector", true, 2, "Vector search and index utilities", "Python"),
            FileNode("backend/memory/vector/chroma_client.py", "chroma_client.py", false, 3, "Establishes connection to Chroma vector database indexer", "Python"),

            FileNode("backend/automation", "automation", true, 1, "Automated browser sandbox tools", "Playwright, Python"),
            FileNode("backend/automation/browser", "browser", true, 2, "Automated Chrome driver executions", "Playwright"),
            FileNode("backend/automation/browser/playwright_runner.py", "playwright_runner.py", false, 3, "Launches headless Chrome instances to capture raw markdown parameters", "Python"),

            FileNode("backend/voice", "voice", true, 1, "Vocal interfaces and synthesizer engines", "Whisper, Google TTS"),
            FileNode("backend/voice/whisper", "whisper", true, 2, "Whisper local transcription settings", "Python"),
            FileNode("backend/voice/whisper/transcribe.py", "transcribe.py", false, 3, "Takes buffer inputs and processes real-time acoustic transcription", "Python"),

            FileNode("packages", "packages", true, 0, "Shared workspaces accessed across Turborepo nodes", "Turborepo"),
            FileNode("packages/ui", "ui", true, 1, "Shared style modules", "React, PostCSS"),
            FileNode("packages/ui/components", "components", true, 2, "Shared design panels", "Tailwind, React"),
            FileNode("packages/ui/components/Card.tsx", "Card.tsx", false, 3, "Glassmorphism-styled card with glowing border aesthetics", "React (TSX)"),
            FileNode("packages/shared-types", "shared-types", true, 1, "Shared structural models", "TypeScript"),
            FileNode("packages/shared-types/ai.ts", "ai.ts", false, 2, "TypeScript schemas defining API outputs and message payload models", "TypeScript"),

            FileNode("docker-compose.yml", "docker-compose.yml", false, 0, "Spins up PostgreSQL, ChromaDB services, and local FastAPI containers", "Docker Compose"),
            FileNode("package.json", "package.json", false, 0, "Turborepo configuration and main package execution definitions", "PNPM, Node")
        )
        _fileNodes.value = rootNodes
    }

    fun toggleFolder(path: String) {
        _fileNodes.value = _fileNodes.value.map { node ->
            if (node.path == path) {
                node.copy(isExpanded = !node.isExpanded)
            } else {
                node
            }
        }
    }

    fun selectFileNode(node: FileNode) {
        _selectedFileNode.value = node
        _generatedBoilerplate.value = null // reset
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectPhase(id: Int) {
        _selectedPhaseId.value = if (_selectedPhaseId.value == id) null else id
    }

    fun updatePhaseProgress(id: Int, progress: Int, isCompleted: Boolean, notes: String) {
        viewModelScope.launch {
            val currentList = phases.value
            val found = currentList.find { it.id == id }
            if (found != null) {
                val updated = found.copy(
                    progress = progress,
                    isCompleted = isCompleted,
                    notes = notes,
                    timestamp = System.currentTimeMillis()
                )
                repository.updatePhase(updated)
            }
        }
    }

    // --- Gemini Call Methods ---

    fun generateFileBoilerplate(node: FileNode) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            _aiErrorLog.value = "Gemini API Key is missing! Please configure GEMINI_API_KEY in your Secrets panel in the AI Studio UI."
            return
        }

        _isGenerating.value = true
        _aiErrorLog.value = null
        _generatedBoilerplate.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val systemPrompt = "You are the Chief Systems Architect of CYBERTON OS, an advanced, startup-grade AI Operating System monorepo. Your goal is to provide beautiful, high-fidelity, complete, and production-ready code that matches the requested folder and file path perfectly."

            val prompt = """
                The user wants to generate code for the file in the workspace:
                - File Name: ${node.name}
                - Path in monorepo: ${node.path}
                - Module/Service Details: ${node.description}
                - Core technologies to use: ${node.techStack}

                Provide a highly professional, ready-to-use, startup-grade code implementation for this file. 
                Keep it completely comprehensive, do not use dummy placeholders. Write actual systems logic (e.g., proper error handling, exports, configuration integrations, and typings).
                Introduce your answer with a very brief structural review in 2 lines, followed directly by the complete code block wrapped in classical triple-backticks with the correct syntax highlighter identifier (e.g., typescript, python, dockerfile, json).
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(
                    Content(parts = listOf(Part(text = prompt)))
                ),
                systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
            )

            try {
                val response = RetrofitClient.service.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                withContext(Dispatchers.Main) {
                    if (text != null) {
                        _generatedBoilerplate.value = text
                    } else {
                        _aiErrorLog.value = "Returned an empty response. Try again."
                    }
                }
            } catch (e: Exception) {
                Log.e("CybertonOS", "Gemini API error", e)
                withContext(Dispatchers.Main) {
                    _aiErrorLog.value = "API handshake failed: ${e.localizedMessage ?: e.message}"
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _isGenerating.value = false
                }
            }
        }
    }

    fun sendCopilotMessage(bodyText: String) {
        if (bodyText.isBlank()) return

        val userMsg = ChatMessage("user", bodyText)
        _chatMessages.value = _chatMessages.value + userMsg

        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Echo back warning
            viewModelScope.launch {
                _chatMessages.value = _chatMessages.value + ChatMessage("ai", "⚠️ **System Notification**: Gemini API Key is missing. I cannot query the cloud brain. Please go to the **Secrets panel** in the AI Studio sidebar and enter your custom `GEMINI_API_KEY` to unlock full architectural responses.")
            }
            return
        }

        _isGenerating.value = true
        _aiErrorLog.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val systemPrompt = """
                You are the Chief Cognitive Architect of CYBERTON OS, an elite AI operating system. 
                You are talking to a Senior systems engineer. Keep your tone professional, crisp, insightful, and styled with tactical hacker OS terminal vibes.
                Refer directly to Turborepo monorepos, FastAPI web socket routes with async protocols, ChromaDB semantic graph embedding indexes, or Playwright browser automation containers where appropriate. 
                Help them draft, scale, configure, or secure any part of the CYBERTON OS ecosystem. 
                Format your code responses nicely using Markdown code blocks.
            """.trimIndent()

            // Compile conversational context (last 6 messages)
            val historyParts = _chatMessages.value.takeLast(6).map { msg ->
                val prefix = if (msg.author == "user") "User: " else "System Architect: "
                Part(text = prefix + msg.content)
            }

            val request = GenerateContentRequest(
                contents = listOf(
                    Content(parts = historyParts)
                ),
                systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
            )

            try {
                val response = RetrofitClient.service.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                withContext(Dispatchers.Main) {
                    if (text != null) {
                        _chatMessages.value = _chatMessages.value + ChatMessage("ai", text)
                    } else {
                        _chatMessages.value = _chatMessages.value + ChatMessage("ai", "Received empty response from the matrix.")
                    }
                }
            } catch (e: Exception) {
                Log.e("CybertonOS", "Copilot API error", e)
                withContext(Dispatchers.Main) {
                    _chatMessages.value = _chatMessages.value + ChatMessage("ai", "🚨 **Connection Fault**: API channel output error: ${e.localizedMessage ?: e.message}")
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _isGenerating.value = false
                }
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage("ai", "Systems initialized. Welcome to the **CYBERTON-OS Architect Terminal**. Ask me anything about configuring your multi-agent orchestrator, FastAPI websockets, ChromaDB embeddings, or generating custom files!")
        )
    }
}
