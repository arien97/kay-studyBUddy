package com.example.studybuddy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.studybuddy.features.chat.ChatScreen
import com.example.studybuddy.features.loading.LoadingRoute
import com.example.studybuddy.features.main.MainTabsRoute
import com.example.studybuddy.features.onboarding.OnboardingRoute
import com.example.studybuddy.features.signbylink.SignByLinkRoute
import com.example.studybuddy.features.signin.SignInRoute
import com.example.studybuddy.features.signup.SignUpRoute
import com.example.studybuddy.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        viewModel.handleIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.handleIntent(intent)

        setContent {
            AppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = NavigationRoute.Loading,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable<NavigationRoute.Loading> { LoadingRoute() }
                    composable<NavigationRoute.SignIn> {
                        SignInRoute(
                            signUp = { navController.navigate(NavigationRoute.SignUp) },
                            authFlow = {
                                navController.navigate(NavigationRoute.Onboarding) { popUpTo(0) }
                            },
                            emailLink = {
                                navController.navigate(NavigationRoute.EmailLink)
                            }
                        )
                    }
                    composable<NavigationRoute.SignUp> {
                        SignUpRoute(
                            authFlow = {
                                navController.navigate(NavigationRoute.Onboarding)
                                { popUpTo(0) }
                            },
                            backAction = { navController.popBackStack() },
                        )
                    }
                    composable<NavigationRoute.Onboarding> {
                        OnboardingRoute(
                            backAction = { navController.popBackStack() },
                            nextAction = { navController.navigate(NavigationRoute.Tabs) { popUpTo(0) } },
                        )
                    }
                    composable<NavigationRoute.Tabs> {
                        MainTabsRoute(
                            chatAction = { navController.navigate(it) },
                            signOut = { navController.navigate(NavigationRoute.SignIn) { popUpTo(0) } },
                            deleteAccount = { /* Implement delete account functionality */ }
                        )
                    }
                    composable<NavigationRoute.Chat> {
                        val info: NavigationRoute.Chat = it.toRoute()
                        ChatScreen(info)
                    }
                    composable<NavigationRoute.EmailLink> {
                        SignByLinkRoute(backAction = { navController.popBackStack() })
                    }
                }

                val lifecycle = LocalLifecycleOwner.current.lifecycle
                LaunchedEffect(Unit) {
                    viewModel.isAuthEvent.flowWithLifecycle(lifecycle = lifecycle)
                        .collect { isAuth ->
                            val route = if (isAuth) NavigationRoute.Tabs
                            else NavigationRoute.SignIn
                            navController.navigate(route) { popUpTo(0) }
                        }

                }
            }
        }
    }
}