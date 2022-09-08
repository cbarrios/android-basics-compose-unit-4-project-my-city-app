package com.example.flavorsofmiami

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.flavorsofmiami.data.model.Recommendation
import com.example.flavorsofmiami.ui.*
import com.example.flavorsofmiami.ui.theme.FlavorsOfMiamiTheme

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlavorsOfMiamiTheme {
                val windowSize = calculateWindowSizeClass(activity = this)
                MyCityApp(widthSizeClass = windowSize.widthSizeClass)
            }
        }
    }
}


// (1) App main layout: Scaffold with AppBar, AppNavigation and NavHost + (bottom/rail/drawer navigation and list/list-detail layouts)
@Composable
fun MyCityApp(
    modifier: Modifier = Modifier,
    viewModel: RecommendationViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    widthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact
) {

    val navigationType: NavigationType
    val contentType: ContentType
    when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            navigationType = NavigationType.BOTTOM
            contentType = ContentType.LIST
        }
        WindowWidthSizeClass.Medium -> {
            navigationType = NavigationType.RAIL
            contentType = ContentType.LIST
        }
        WindowWidthSizeClass.Expanded -> {
            navigationType = NavigationType.DRAWER
            contentType = ContentType.LIST_DETAIL
        }
        else -> {
            navigationType = NavigationType.BOTTOM
            contentType = ContentType.LIST
        }
    }

    val defaultUi = navigationType == NavigationType.BOTTOM && contentType == ContentType.LIST

    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.Church.name
    )

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = contentType.name) {
        if (contentType == ContentType.LIST && uiState.recommendation != null && viewModel.shouldNavigateToDetails) {
            if (uiState.currentScreen.name != AppScreen.Church.name) {
                navController.navigate(uiState.currentScreen.name) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // re-selecting the same item
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }
            }
            val backQueue = navController.backQueue.map { it.destination.route }
            if (AppScreen.Details.name !in backQueue) {
                navController.navigate(AppScreen.Details.name)
            }
        }
        if (contentType == ContentType.LIST_DETAIL) {
            val backQueue = navController.backQueue.map { it.destination.route }
            if (AppScreen.Details.name in backQueue) {
                navController.navigateUp()
            }
            viewModel.setupShouldNavigateToDetails()
            viewModel.setRecommendationInfoForCurrentScreen(uiState.currentScreen)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            AppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                title = if (contentType == ContentType.LIST) stringResource(id = currentScreen.title) else stringResource(
                    id = uiState.currentScreen.title
                )
            )
        },
        bottomBar = {
            if (defaultUi) {
                AppNavigation(
                    navigationType = navigationType,
                    navItems = NavMenuItems.menuItems,
                    currentDestination = backStackEntry?.destination,
                    navController = navController,
                    onMenuClicked = {
                        onNavMenuClicked(contentType, it, navController, viewModel)
                    },
                    onSelectedSaved = {
                        viewModel.setCurrentScreen(it)
                    }
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (uiState.loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            Row {
                var selectedLabel by remember { mutableStateOf("") }
                if (navigationType != NavigationType.BOTTOM) {
                    AppNavigation(
                        navigationType = navigationType,
                        navItems = NavMenuItems.menuItems,
                        currentDestination = backStackEntry?.destination,
                        navController = navController,
                        onMenuClicked = {
                            onNavMenuClicked(contentType, it, navController, viewModel)
                        },
                        onSelectedSaved = {
                            if (navigationType == NavigationType.DRAWER) {
                                if (selectedLabel.isEmpty()) {
                                    viewModel.setCurrentScreen(it)
                                    selectedLabel = it
                                } else {
                                    if (selectedLabel != it) {
                                        viewModel.setCurrentScreen(it, true)
                                        selectedLabel = it
                                    }
                                }
                            } else {
                                viewModel.setCurrentScreen(it)
                            }
                        }
                    )
                }

                NavHost(
                    modifier = Modifier.weight(1f),
                    navController = navController,
                    startDestination = AppScreen.Church.name
                ) {
                    composable(route = AppScreen.Church.name) {
                        CategoryScreen(
                            recommendations = uiState.churches,
                            onItemClicked = {
                                onRecommendationClicked(
                                    it,
                                    viewModel,
                                    navController,
                                    contentType
                                )
                            }
                        )
                    }
                    composable(route = AppScreen.Wedding.name) {
                        CategoryScreen(
                            recommendations = uiState.weddings,
                            onItemClicked = {
                                onRecommendationClicked(
                                    it,
                                    viewModel,
                                    navController,
                                    contentType
                                )
                            }
                        )
                    }
                    composable(route = AppScreen.Hotel.name) {
                        CategoryScreen(
                            recommendations = uiState.hotels,
                            onItemClicked = {
                                onRecommendationClicked(
                                    it,
                                    viewModel,
                                    navController,
                                    contentType
                                )
                            }
                        )
                    }
                    composable(route = AppScreen.Details.name) {
                        RecommendationInfoScreen(
                            contentType = contentType,
                            recommendation = uiState.recommendation,
                            onNavigateUp = {
                                // Because LIST_DETAIL will include the info in the main layout
                                // Basically clicking on a card will just set that info right in the same layout
                                // So this screen will in that case only be used to display the info, but not for navigation
                                // Therefore, we only navigate up if our content type is LIST
                                if (contentType == ContentType.LIST) {
                                    navController.navigateUp()
                                }
                            }
                        )
                    }
                }

                if (contentType == ContentType.LIST_DETAIL) {
                    RecommendationInfoScreen(
                        modifier = Modifier.weight(1f),
                        contentType = contentType,
                        recommendation = uiState.recommendation
                    )
                }
            }
        }
    }
}

private fun onRecommendationClicked(
    recommendation: Recommendation,
    viewModel: RecommendationViewModel,
    navController: NavHostController,
    contentType: ContentType
) {
    viewModel.setRecommendationInfo(recommendation)
    if (contentType == ContentType.LIST) {
        navController.navigate(AppScreen.Details.name)
    }
}

private fun onNavMenuClicked(
    contentType: ContentType,
    menuItem: MenuItem,
    navController: NavHostController,
    viewModel: RecommendationViewModel
) {
    if (contentType == ContentType.LIST) {
        viewModel.setCurrentScreen(menuItem.label)
        val backQueue = navController.backQueue.map { it.destination.route }
        if (AppScreen.Details.name in backQueue) {
            navController.navigateUp()
        }
        navController.navigate(menuItem.label) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // re-selecting the same item
            launchSingleTop = true
            // Restore state when re-selecting a previously selected item
            restoreState = true
        }
    } else {
        viewModel.setCurrentScreen(menuItem.label, updateFirstRecommendation = true)
        navController.navigate(menuItem.label) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // re-selecting the same item
            launchSingleTop = true
            // Restore state when re-selecting a previously selected item
            restoreState = true
        }
    }
}

// (2) Top app bar layout
@Composable
fun AppBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    title: String,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = title) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            }
        }
    )
}

// (3) Reusable app navigation component
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navigationType: NavigationType,
    navItems: List<MenuItem>,
    currentDestination: NavDestination?,
    navController: NavHostController,
    onMenuClicked: (MenuItem) -> Unit,
    onSelectedSaved: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (navigationType) {
        NavigationType.BOTTOM -> {
            BottomNavigation(modifier = modifier) {
                // Boolean to see if we are in our menu screens and NOT in details
                val found =
                    navItems.firstOrNull { item -> currentDestination?.hierarchy?.any { it.route == item.label } == true } != null
                val backQueue = navController.backQueue.map { it.destination.route }
                var label: String? = null
                if (AppScreen.Details.name in backQueue) {
                    label = backQueue.getOrNull(2)
                    if (label == null || label == AppScreen.Details.name) label =
                        backQueue.getOrNull(1)
                }
                navItems.forEach { item ->
                    val extraCheck = item.label == label
                    var isSelected =
                        currentDestination?.hierarchy?.any { it.route == item.label } == true
                    if (!isSelected) {
                        isSelected = if (found) false else extraCheck
                    }
                    if (isSelected) onSelectedSaved(item.label)
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                if (isSelected) item.iconSelected else item.icon,
                                contentDescription = null
                            )
                        },
                        label = { Text(item.label) },
                        selected = isSelected,
                        onClick = { onMenuClicked(item) }
                    )
                }
            }
        }
        NavigationType.RAIL -> {
            NavigationRail(modifier = modifier) {
                // Boolean to see if we are in our menu screens and NOT in details
                val found =
                    navItems.firstOrNull { item -> currentDestination?.hierarchy?.any { it.route == item.label } == true } != null
                val backQueue = navController.backQueue.map { it.destination.route }
                var label: String? = null
                if (AppScreen.Details.name in backQueue) {
                    label = backQueue.getOrNull(2)
                    if (label == null || label == AppScreen.Details.name) label =
                        backQueue.getOrNull(1)
                }
                navItems.forEach { item ->
                    val extraCheck = item.label == label
                    var isSelected =
                        currentDestination?.hierarchy?.any { it.route == item.label } == true
                    if (!isSelected) {
                        isSelected = if (found) false else extraCheck
                    }
                    if (isSelected) onSelectedSaved(item.label)
                    NavigationRailItem(
                        icon = {
                            Icon(
                                if (isSelected) item.iconSelected else item.icon,
                                contentDescription = null
                            )
                        },
                        label = { Text(item.label) },
                        selected = isSelected,
                        onClick = { onMenuClicked(item) }
                    )
                }
            }
        }
        NavigationType.DRAWER -> {
            PermanentDrawerSheet(
                modifier = Modifier.width(240.dp),
                drawerContainerColor = MaterialTheme.colors.surface
            ) {
                Spacer(Modifier.height(12.dp))
                navItems.forEach { item ->
                    val isSelected =
                        currentDestination?.hierarchy?.any { it.route == item.label } == true
                    if (isSelected) onSelectedSaved(item.label)
                    val defaultColor =
                        if (isSelected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                if (isSelected) item.iconSelected else item.icon,
                                contentDescription = null,
                                tint = defaultColor
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                color = defaultColor
                            )
                        },
                        selected = isSelected,
                        onClick = { onMenuClicked(item) },
                        modifier = Modifier.padding(horizontal = 12.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colors.primary,
                            unselectedContainerColor = MaterialTheme.colors.surface
                        )
                    )
                }
            }
        }
    }
}

@Preview(widthDp = 400)
@Preview(widthDp = 400, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MyCityCompactPreview() {
    FlavorsOfMiamiTheme {
        MyCityApp(
            widthSizeClass = WindowWidthSizeClass.Compact
        )
    }
}

@Preview(widthDp = 700)
@Preview(widthDp = 700, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MyCityMediumPreview() {
    FlavorsOfMiamiTheme {
        MyCityApp(
            widthSizeClass = WindowWidthSizeClass.Medium
        )
    }
}

@Preview(widthDp = 1000)
@Preview(widthDp = 1000, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MyCityExpandedPreview() {
    FlavorsOfMiamiTheme {
        MyCityApp(
            widthSizeClass = WindowWidthSizeClass.Expanded
        )
    }
}