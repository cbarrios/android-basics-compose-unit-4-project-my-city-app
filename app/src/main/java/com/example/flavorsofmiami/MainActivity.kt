package com.example.flavorsofmiami

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Church
import androidx.compose.material.icons.outlined.Hotel
import androidx.compose.material.icons.outlined.Loyalty
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.flavorsofmiami.ui.CategoryScreen
import com.example.flavorsofmiami.ui.RecommendationInfoScreen
import com.example.flavorsofmiami.ui.RecommendationViewModel
import com.example.flavorsofmiami.ui.theme.FlavorsOfMiamiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlavorsOfMiamiTheme {
                MyCityApp(onFinished = { finish() })
            }
        }
    }
}

enum class AppScreen(@StringRes val title: Int) {
    Church(R.string.category_church),
    Wedding(R.string.category_wedding),
    Hotel(R.string.category_hotel),
    Details(R.string.recommendation_details)
}

// (1) App main layout: Scaffold with AppBar, AppNavigation and NavHost + (bottom/rail/drawer navigation and list/list-detail layouts)
@Composable
fun MyCityApp(
    modifier: Modifier = Modifier,
    viewModel: RecommendationViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    widthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    onFinished: () -> Unit = {}
) {

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
            AppNavigation(
                navItems = listOf(
                    BottomMenuItem(
                        AppScreen.Church.name,
                        Icons.Outlined.Church,
                        Icons.Filled.Church
                    ),
                    BottomMenuItem(
                        AppScreen.Wedding.name,
                        Icons.Outlined.Loyalty,
                        Icons.Filled.Loyalty
                    ),
                    BottomMenuItem(
                        AppScreen.Hotel.name,
                        Icons.Outlined.Hotel,
                        Icons.Filled.Hotel
                    )
                ),
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
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
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
                            // If we are in list-detail layout (extended window size)
                            // TODO: Call onFinished() for list-detail layout only

                            // Else if we are in details go back
                            navController.navigateUp()
                        }
                    )
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

// (3) Bottom navigation bar layout
@Composable
fun AppNavigation(
    navItems: List<BottomMenuItem>,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
    onMenuClicked: (BottomMenuItem) -> Unit
) {
    BottomNavigation(modifier = modifier) {
        navItems.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.label } == true
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

data class BottomMenuItem(
    val label: String,
    val icon: ImageVector,
    val iconSelected: ImageVector
)

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DefaultPreview() {
    FlavorsOfMiamiTheme {
        MyCityApp()
    }
}