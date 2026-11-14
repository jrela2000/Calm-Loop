package com.jarrodqsimpson.calmloop

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import com.jarrodqsimpson.calmloop.ui.about.AboutScreen
import com.jarrodqsimpson.calmloop.ui.components.ImmersiveBackground
import com.jarrodqsimpson.calmloop.ui.onboarding.WelcomeScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jarrodqsimpson.calmloop.ui.breathing.BoxBreathingScreen
import com.jarrodqsimpson.calmloop.ui.components.BottomNavBar
import com.jarrodqsimpson.calmloop.ui.contact.ContactCrisisScreen
import com.jarrodqsimpson.calmloop.ui.grounding.GroundingGameScreen
import com.jarrodqsimpson.calmloop.ui.home.HomeScreen
import com.jarrodqsimpson.calmloop.ui.journal.JournalEntryScreen
import com.jarrodqsimpson.calmloop.ui.journal.JournalHistoryScreen
import com.jarrodqsimpson.calmloop.ui.journal.JournalProcessingScreen
import com.jarrodqsimpson.calmloop.ui.journal.VoiceJournalScreen
import com.jarrodqsimpson.calmloop.ui.progress.ProgressScreen
import com.jarrodqsimpson.calmloop.ui.settings.SettingsScreen
import com.jarrodqsimpson.calmloop.ui.theme.CalmLoopTheme
import com.jarrodqsimpson.calmloop.ui.viewModel.CalmLoopViewModel

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
    val settings by viewModel.settings.collectAsState()
    val loadedSettings = settings
    if (loadedSettings == null) {
        // Preferences are still loading (a few ms); show the background rather than a wrong screen.
        ImmersiveBackground {}
        return
    }
    // Rebuild navigation when consent changes (first acceptance, or after "Delete All My Data").
    key(loadedSettings.hasAcceptedCurrentDisclaimer) {
        CalmLoopNavigation(
            viewModel = viewModel,
            startDestination = if (loadedSettings.hasAcceptedCurrentDisclaimer) "home" else "welcome"
        )
    }
}

@Composable
private fun CalmLoopNavigation(viewModel: CalmLoopViewModel, startDestination: String) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: startDestination

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
                startDestination = startDestination
            ) {
                composable("welcome") {
                    WelcomeScreen(
                        aiAvailable = viewModel.isAiConfigured,
                        onAccept = { aiEnabled -> viewModel.acceptDisclaimer(aiEnabled) },
                        onOpenPrivacy = { navController.navigate("about") }
                    )
                }

                composable("about") {
                    AboutScreen(onBack = { navController.popBackStack() })
                }

                composable("home") {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigate = { route -> navController.navigate(route) }
                    )
                }

                composable("journal") {
                    VoiceJournalScreen(
                        onNavigateToProcessing = { text ->
                            if (viewModel.submitJournal(text)) navController.navigate("journal_processing")
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("journal_processing") {
                    JournalProcessingScreen(
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
                        onOpenPrivacy = { navController.navigate("about") },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
