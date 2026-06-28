package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.TransactionViewModel
import com.example.ui.screens.AddTransactionScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.formatLKR
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkTheme by remember { mutableStateOf(false) }

            MyApplicationTheme(darkTheme = darkTheme) {
                MainAppScaffold(
                    darkTheme = darkTheme,
                    onToggleTheme = { darkTheme = !darkTheme }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    viewModel: TransactionViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard?animate={animate}"

    // Determine configuration and adaptive view size
    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 720

    val searchQuery by viewModel.searchQuery.collectAsState()
    val currentBalance by viewModel.currentBalance.collectAsState()

    // Determine current section (Dashboard, Add Transaction, Reports)
    val activeScreen = when {
        currentRoute.startsWith("dashboard") -> "dashboard"
        currentRoute.startsWith("add_transaction") -> "add_transaction"
        currentRoute.startsWith("reports") -> "reports"
        else -> "dashboard"
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // Left Side Navigation Sidebar (For Desktop / Wide screens)
        if (isWideScreen) {
            SidebarLayout(
                activeScreen = activeScreen,
                onNavigate = { route, source ->
                    if (route == "dashboard") {
                        navController.navigate("dashboard?animate=true") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else if (route == "add_transaction") {
                        navController.navigate("add_transaction?source=$source") {
                            launchSingleTop = true
                        }
                    } else if (route == "reports") {
                        navController.navigate("reports") {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        // Main app area containing Top App Bar and Screen Contents
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            // Header Top App Bar (Horizontal Bar at Top)
            HeaderTopBar(
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme,
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                currentBalance = currentBalance,
                isWideScreen = isWideScreen
            )

            // Screen Navigation Host (Scaffold wrapping content)
            Scaffold(
                modifier = Modifier.weight(1f),
                floatingActionButton = {
                    // Show FAB only on Dashboard screen
                    if (activeScreen == "dashboard") {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate("add_transaction?source=fab") {
                                    launchSingleTop = true
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier
                                .padding(16.dp)
                                .testTag("fab_add_transaction")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "add", // matching xpath //button[contains(., 'add')] or icon add
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                },
                bottomBar = {
                    // Show mobile bottom navigation bar on compact screens
                    if (!isWideScreen) {
                        MobileBottomNav(
                            activeScreen = activeScreen,
                            onNavigate = { route ->
                                if (route == "dashboard") {
                                    // Mobile Home transitions to Dashboard with NONE animation as specified
                                    navController.navigate("dashboard?animate=false") {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }
                                } else if (route == "add_transaction") {
                                    navController.navigate("add_transaction?source=sidebar") {
                                        launchSingleTop = true
                                    }
                                } else if (route == "reports") {
                                    navController.navigate("reports") {
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                },
                contentWindowInsets = WindowInsets.safeDrawing
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "dashboard?animate={animate}",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    // Dashboard Screen Route with custom enter/exit animations
                    composable(
                        route = "dashboard?animate={animate}",
                        arguments = listOf(
                            navArgument("animate") { 
                                type = NavType.StringType
                                defaultValue = "true" 
                            }
                        ),
                        enterTransition = {
                            val animate = targetState.arguments?.getString("animate") ?: "true"
                            if (animate == "false") {
                                EnterTransition.None
                            } else {
                                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                            }
                        },
                        exitTransition = {
                            val animate = targetState.arguments?.getString("animate") ?: "true"
                            if (animate == "false") {
                                ExitTransition.None
                            } else {
                                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                            }
                        }
                    ) {
                        DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToAddTransaction = {
                                navController.navigate("add_transaction?source=fab")
                            }
                        )
                    }

                    // Add Transaction Route with parameter-driven animations
                    composable(
                        route = "add_transaction?source={source}",
                        arguments = listOf(
                            navArgument("source") { 
                                type = NavType.StringType
                                defaultValue = "sidebar" 
                            }
                        ),
                        enterTransition = {
                            val source = targetState.arguments?.getString("source") ?: "sidebar"
                            if (source == "fab") {
                                // Slide up transition from bottom
                                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up)
                            } else {
                                // Push transition from right
                                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                            }
                        },
                        exitTransition = {
                            val source = targetState.arguments?.getString("source") ?: "sidebar"
                            if (source == "fab") {
                                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down)
                            } else {
                                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                            }
                        },
                        popEnterTransition = {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up)
                        },
                        popExitTransition = {
                            // Push back transition to right
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                        }
                    ) {
                        AddTransactionScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                // Push back backstack navigation
                                navController.popBackStack()
                            }
                        )
                    }

                    // Reports Screen Route
                    composable(
                        route = "reports",
                        enterTransition = {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                        },
                        exitTransition = {
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                        }
                    ) {
                        ReportsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun SidebarLayout(
    activeScreen: String,
    onNavigate: (route: String, source: String) -> Unit
) {
    Surface(
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp)
        ) {
            // FinanceManager Brand Title
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                Text(
                    text = "FinanceManager",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Manage your wealth",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sidebar navigation menu
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SidebarNavItem(
                    label = "Dashboard",
                    icon = Icons.Default.Dashboard,
                    selected = activeScreen == "dashboard",
                    onClick = { onNavigate("dashboard", "sidebar") },
                    testTag = "nav_dashboard"
                )
                SidebarNavItem(
                    label = "Add Transaction",
                    icon = Icons.Default.AddCircle,
                    selected = activeScreen == "add_transaction",
                    onClick = { onNavigate("add_transaction", "sidebar") },
                    testTag = "nav_add_transaction"
                )
                SidebarNavItem(
                    label = "Reports",
                    icon = Icons.Default.BarChart,
                    selected = activeScreen == "reports",
                    onClick = { onNavigate("reports", "sidebar") },
                    testTag = "nav_reports"
                )
                SidebarNavItem(
                    label = "Settings",
                    icon = Icons.Default.Settings,
                    selected = false,
                    onClick = { /* No-op */ },
                    testTag = "nav_settings"
                )
            }

            // User Profile Section at bottom
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AP",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Amila Perera",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Premium Member",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun SidebarNavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val containerBg = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    } else {
        Color.Transparent
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(containerBg)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
    }
}

@Composable
fun HeaderTopBar(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    currentBalance: Double,
    isWideScreen: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Search field
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search transactions...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search icon")
                    },
                    modifier = Modifier
                        .widthIn(max = 320.dp)
                        .height(48.dp)
                        .testTag("input_search"),
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            // Quick Actions & Balance Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Currency Exchange Indicator Button
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.CurrencyExchange,
                        contentDescription = "Currency exchange",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Dark mode toggle
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        imageVector = if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Theme",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isWideScreen) {
                    Divider(
                        modifier = Modifier
                            .height(32.dp)
                            .width(1.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    // Current Balance Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = formatLKR(currentBalance),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "SAVER",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MobileBottomNav(
    activeScreen: String,
    onNavigate: (route: String) -> Unit
) {
    NavigationBar(
        modifier = Modifier.testTag("mobile_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 8.dp
    ) {
        val itemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") }, // containing 'Home'
            label = { Text("Home") },
            selected = activeScreen == "dashboard",
            onClick = { onNavigate("dashboard") },
            colors = itemColors
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Add, contentDescription = "Add Transaction") },
            label = { Text("Add") },
            selected = activeScreen == "add_transaction",
            onClick = { onNavigate("add_transaction") },
            colors = itemColors
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Reports") },
            label = { Text("Stats") },
            selected = activeScreen == "reports",
            onClick = { onNavigate("reports") },
            colors = itemColors
        )
    }
}
