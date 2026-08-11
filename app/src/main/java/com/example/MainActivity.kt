package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.breathing.BoxBreathingScreen
import com.example.ui.components.BottomNavBar
import com.example.ui.contact.ContactCrisisScreen
import com.example.ui.grounding.GroundingGameScreen
import com.example.ui.home.HomeScreen
import com.example.ui.journal.JournalEntryScreen
import com.example.ui.journal.JournalHistoryScreen
import com.example.ui.journal.JournalProcessingScreen
import com.example.ui.journal.VoiceJournalScreen
import com.example.ui.progress.ProgressScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.CalmLoopTheme
import com.example.ui.viewModel.CalmLoopViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    private val viewModel: CalmLoopViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CalmLoopTheme {
                CalmLoopApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CalmLoopApp(viewModel: CalmLoopViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    // Only show bottom navigation bar on primary tabs
    val showBottomBar = currentRoute in listOf("home", "journal_history", "progress")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("home") {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigate = { route -> navController.navigate(route) }
                    )
                }

                composable("journal") {
                    VoiceJournalScreen(
                        onNavigateToProcessing = { encodedText ->
                            navController.navigate("journal_processing/$encodedText")
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = "journal_processing/{transcription}",
                    arguments = listOf(navArgument("transcription") { type = NavType.StringType })
                ) { backStackEntry ->
                    val rawTranscription = backStackEntry.arguments?.getString("transcription") ?: ""
                    val decoded = try {
                        URLDecoder.decode(rawTranscription, StandardCharsets.UTF_8.toString())
                    } catch (e: Exception) {
                        rawTranscription
                    }
                    JournalProcessingScreen(
                        transcription = decoded,
                        viewModel = viewModel,
                        onNavigateToEntry = { entryId ->
                            navController.navigate("journal_entry/$entryId") {
                                popUpTo("home")
                            }
                        }
                    )
                }

                composable(
                    route = "journal_entry/{entryId}",
                    arguments = listOf(navArgument("entryId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val entryId = backStackEntry.arguments?.getLong("entryId") ?: 0L
                    JournalEntryScreen(
                        entryId = entryId,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("journal_history") {
                    JournalHistoryScreen(
                        viewModel = viewModel,
                        onNavigateToNew = { navController.navigate("journal") },
                        onNavigateToEntry = { id -> navController.navigate("journal_entry/$id") },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("breathing") {
                    BoxBreathingScreen(
                        viewModel = viewModel,
                        onNavigateToGrounding = { navController.navigate("grounding") },
                        onNavigateToCrisis = { navController.navigate("contact") },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("grounding") {
                    GroundingGameScreen(
                        viewModel = viewModel,
                        onNavigateToBreathing = { navController.navigate("breathing") },
                        onNavigateToCrisis = { navController.navigate("contact") },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("contact") {
                    ContactCrisisScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("progress") {
                    ProgressScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("settings") {
                    SettingsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
