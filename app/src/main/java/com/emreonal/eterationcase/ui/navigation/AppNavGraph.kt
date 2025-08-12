package com.emreonal.eterationcase.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.emreonal.eterationcase.R
import com.emreonal.eterationcase.ui.cart.CartScreen
import com.emreonal.eterationcase.ui.cart.CartViewModel
import com.emreonal.eterationcase.ui.detail.ProductDetailRootScreen
import com.emreonal.eterationcase.ui.favorites.FavoritesScreen
import com.emreonal.eterationcase.ui.list.ProductListRootScreen
import com.emreonal.eterationcase.ui.list.ProductUiModel
import com.emreonal.eterationcase.ui.profile.ProfileScreen
import com.emreonal.eterationcase.ui.theme.Main

sealed class Screen(val route: String, val label: String, val icon: Int = 0) {
    data object Home : Screen(route = "home", label = "Home", icon = R.drawable.ic_home)
    data object Cart : Screen(route = "cart", label = "Cart", icon = R.drawable.ic_cart)
    data object Favorites :
        Screen(route = "favorites", label = "Favorites", icon = R.drawable.ic_favorites)

    data object Profile : Screen(route = "profile", label = "Profile", icon = R.drawable.ic_profile)
    data object Detail : Screen(route = "detail/{productId}", label = "Detail") {
        fun route(productId: String) = "detail/$productId"
    }
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = hiltViewModel()

    Scaffold(
        bottomBar = {
            BottomBar(
                navController = navController,
                cartCount = cartViewModel.totalItemCount.collectAsState().value
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {

                ProductListRootScreen(
                    onOpenDetail = { productUi ->
                        navController.navigateToDetail(productUi)
                    }
                )
            }

            composable(Screen.Detail.route) { backStackEntry ->
                val product = remember {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<ProductUiModel>("product")
                }

                LaunchedEffect(Unit) {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.remove<ProductUiModel>("product")
                }

                if (product == null) {
                    LaunchedEffect("missing_product") {
                        navController.popBackStack()
                    }
                    return@composable
                }

                ProductDetailRootScreen(
                    onBack = { navController.popBackStack() },
                    product = product
                )
            }

            composable(Screen.Cart.route) {
                CartScreen(onComplete = {
                    cartViewModel.clear()
                    navController.popBackStack()
                })
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onOpenDetail = { productUi ->
                        navController.navigateToDetail(productUi)
                    }
                )
            }

            composable(route = Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}

@Composable
private fun BottomBar(navController: NavHostController, cartCount: Int) {
    val items = listOf(Screen.Home, Screen.Cart, Screen.Favorites, Screen.Profile)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(tonalElevation = 0.dp) {
        items.forEach { screen ->
            val selected =
                currentRoute?.startsWith(screen.route.removeSuffix("/{productId}")) == true
            NavigationBarItem(
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Main
                ),
                onClick = {
                    navController.navigate(screen.route.replace("/{productId}", "")) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    if (screen == Screen.Cart && cartCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge {
                                    Text(cartCount.toString())
                                }
                            }) {
                            Icon(
                                painter = painterResource(id = screen.icon),
                                contentDescription = screen.label
                            )
                        }
                    } else {
                        Icon(painterResource(id = screen.icon), contentDescription = screen.label)
                    }
                }
            )
        }
    }
}

private fun NavHostController.navigateToDetail(product: ProductUiModel) {
    currentBackStackEntry?.savedStateHandle?.set("product", product)
    navigate(Screen.Detail.route(product.id))
}