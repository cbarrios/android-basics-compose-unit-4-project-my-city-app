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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.flavorsofmiami.data.model.Category
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

    Scaffold(
        modifier = modifier,
        topBar = {
            AppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                title = stringResource(id = currentScreen.title)
            )
        },
        bottomBar = {
            if (defaultUi) {
                AppNavigation(
                    navigationType = navigationType,
                    navItems = NavMenuItems.menuItems,
                    currentDestination = backStackEntry?.destination,
                    onMenuClicked = {
                        navController.navigate(it.label) {
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
                )
            }
        }
    ) { innerPadding ->

        val uiState by viewModel.uiState.collectAsState()

        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (uiState.loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            Row {
                if (navigationType != NavigationType.BOTTOM) {
                    AppNavigation(
                        navigationType = navigationType,
                        navItems = NavMenuItems.menuItems,
                        currentDestination = backStackEntry?.destination,
                        onMenuClicked = {
                            navController.navigate(it.label) {
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
                        })
                }
                NavHost(
                    navController = navController,
                    startDestination = AppScreen.Church.name
                ) {
                    composable(route = AppScreen.Church.name) {
                        val churches = remember(uiState.recommendations) {
                            uiState.recommendations.filter { it.category == Category.CHURCH }
                        }
                        CategoryScreen(
                            recommendations = churches,
                            onItemClicked = {
                                viewModel.setRecommendationInfo(it)
                                navController.navigate(AppScreen.Details.name)
                            }
                        )
                    }
                    composable(route = AppScreen.Wedding.name) {
                        val weddings = remember(uiState.recommendations) {
                            uiState.recommendations.filter { it.category == Category.WEDDING }
                        }
                        CategoryScreen(
                            recommendations = weddings,
                            onItemClicked = {
                                viewModel.setRecommendationInfo(it)
                                navController.navigate(AppScreen.Details.name)
                            }
                        )
                    }
                    composable(route = AppScreen.Hotel.name) {
                        val hotels = remember(uiState.recommendations) {
                            uiState.recommendations.filter { it.category == Category.HOTEL }
                        }
                        CategoryScreen(
                            recommendations = hotels,
                            onItemClicked = {
                                viewModel.setRecommendationInfo(it)
                                navController.navigate(AppScreen.Details.name)
                            }
                        )
                    }
                    composable(route = AppScreen.Details.name) {
                        RecommendationInfoScreen(
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
            }
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
    onMenuClicked: (MenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    when (navigationType) {
        NavigationType.BOTTOM -> {
            BottomNavigation(modifier = modifier) {
                navItems.forEach { item ->
                    val isSelected =
                        currentDestination?.hierarchy?.any { it.route == item.label } == true
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
                navItems.forEach { item ->
                    val isSelected =
                        currentDestination?.hierarchy?.any { it.route == item.label } == true
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