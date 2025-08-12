package com.example.clazzi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.clazzi.repository.FirebaseVoteRepository
import com.example.clazzi.repository.RestApiVoteRepository
import com.example.clazzi.repository.network.ApiClient
import com.example.clazzi.ui.screens.AuthScreen
import com.example.clazzi.ui.screens.ChatScreen
import com.example.clazzi.ui.screens.CreateVoteScreen
import com.example.clazzi.ui.screens.MyPageScreen
import com.example.clazzi.ui.screens.VoteListScreen
import com.example.clazzi.ui.screens.VoteScreen
import com.example.clazzi.ui.theme.ClazziTheme
import com.example.clazzi.viewmodel.VoteListViewModel
import com.example.clazzi.viewmodel.VoteListViewModelFactory
import com.example.clazzi.viewmodel.VoteViewModel
import com.example.clazzi.viewmodel.VoteViewModelFactory
import com.google.firebase.auth.FirebaseAuth

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object VoteList : BottomNavItem("voteList", Icons.AutoMirrored.Filled.List, "투표")
    object Chat : BottomNavItem("chat", Icons.AutoMirrored.Filled.List, "채팅")
    object MyPage : BottomNavItem("mypage", Icons.AutoMirrored.Filled.List, "마이페이지")
}

class MainActivity : ComponentActivity() {


    fun onVoteClicked(voteId: String) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            ClazziTheme {
                val navController = rememberNavController()
//                val repo = RestApiVoteRepository(ApiClient.voteApiService)
                val repo = FirebaseVoteRepository()
//                val viewListViewModel = viewModel<VoteListViewModel>()
                val voteListViewModel: VoteListViewModel = viewModel(
                    factory = VoteListViewModelFactory(repo)
                )

                val voteViewModel: VoteViewModel = viewModel(
                    factory = VoteViewModelFactory(repo)
                )
                val isLoggedIn = FirebaseAuth.getInstance().currentUser != null


                NavHost(
                    navController = navController,
                    startDestination = if (isLoggedIn) "main" else "auth"
                ) {

                    composable("auth") {
                        AuthScreen(navController)
                    }
//                    composable("vote") {
//                        VoteScreen(
//                            navController = navController
//                        )
//                    }

                    composable("main") {
                        MainScreen(voteListViewModel)
                    }


                    composable(
                        "vote/{voteId}",
                        deepLinks = listOf(
                            navDeepLink { uriPattern = "clazzi://vote/{voteId}" },
                            navDeepLink {
                                uriPattern = "https://clazzi-54344.web.app/vote/{voteId}"
                            }
                        )
                    ) { backStackEntry ->
                        val voteId = backStackEntry.arguments?.getString("voteId") ?: "1"
                        val vote = voteListViewModel.getVoteById(voteId)
                        VoteScreen(
                            voteId = voteId,
                            navController = navController,
                            voteViewModel = voteViewModel,
                            voteListViewModel = voteListViewModel
                        )
//                        if (vote != null) {
//                            VoteScreen(
//                                voteId = voteId,
//                                navController = navController,
//                                voteListViewModel = viewListViewModel
//                            )
//                        } else {
//                            val context = LocalContext.current
//                            Toast.makeText(context, "해당 투표가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
//                        }

                    }

//                    composable("createVote") {
//                        CreateVoteScreen(
//                            navController,
//                            onVoteCreate = { vote ->
//                                navController.popBackStack()
//                                viewListViewModel.addVote(vote)
//                            })
//                    }

                    composable("createVote") {
                        CreateVoteScreen(
                            voteListViewModel,
//                            onVoteCreate = { vote ->
//                                navController.popBackStack()
//                                viewListViewModel
//                            }
                            navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(voteListViewModel: VoteListViewModel) {
    val navController = rememberNavController()

    Scaffold(bottomBar = {
        BottomNavigationBar(navController = navController)

    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "voteList",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.VoteList.route) {
                VoteListScreen(
                    navController = navController,
                    viewModel = voteListViewModel,
                    onVoteClicked = { voteId ->
                        navController.navigate("vote/$voteId")

                    }
                )
            }


            composable(BottomNavItem.Chat.route) {
                ChatScreen()
            }

            composable(BottomNavItem.MyPage.route) {
                MyPageScreen(navController)
            }
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.VoteList,
        BottomNavItem.Chat,
        BottomNavItem.MyPage
    )

    BottomNavigation {
        val currentRoute = navController
            .currentBackStackEntryAsState().value?.destination?.route

        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {

                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

        }
    }
}

