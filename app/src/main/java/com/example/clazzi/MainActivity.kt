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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.clazzi.ui.screens.ChatRoomScreen
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
import com.google.firebase.firestore.FirebaseFirestore

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object VoteList : BottomNavItem("voteList", Icons.AutoMirrored.Filled.List, "투표")
    object Chat : BottomNavItem("chat", Icons.AutoMirrored.Filled.Chat, "채팅")
    object MyPage : BottomNavItem("mypage", Icons.Default.Person, "마이페이지")
}

class MainActivity : ComponentActivity() {


    fun onVoteClicked(voteId: String) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            // 컴포저블에서 상태가 변경될 떄 UI가 갱신이 될 수 있는 이유는 Compost가 상태를 관찰하고 있기 때문
            // setContent{} 안에 작성되는 모든 스테이트(상태)만 컴포즈에서 관찰이 가능 하다.
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

                val auth = FirebaseAuth.getInstance()
                val isLoggedIn = auth.currentUser != null

                // 사용자 등록 ( 앱 시작시 닉네임 저장 )
                LaunchedEffect(auth.currentUser) {
                    auth.currentUser?.let { user ->
                        val nickname = user.uid.take(4)
                        FirebaseFirestore.getInstance().collection("users")
                            .document(user.uid)
                            .set(mapOf("nickname" to nickname))
                    }
                }

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
                        MainScreen(voteListViewModel, navController)
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

                    composable("chatRoom/{chatRoomId}/{otherUserId}/{otherUserNickname}") { backStackEntry ->
                        val chatRoomId = backStackEntry.arguments?.getString("chatRoomId") ?: ""
                        val otherUserId = backStackEntry.arguments?.getString("otherUserId") ?: ""
                        val otherUserNickname = backStackEntry.arguments?.getString("otherUserNickname") ?: ""
                        ChatRoomScreen(
                            chatRoomId,
                            otherUserId,
                            otherUserNickname
                        )
                    }

                    composable("mypage") {
                        MyPageScreen(navController)
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(voteListViewModel: VoteListViewModel, parentNavController: NavHostController) {
    val navController = rememberNavController()

    Scaffold(bottomBar = {
        // 화면 하단에 여러 페이지로 이동하는 탭을 제공
        BottomNavigationBar(navController = navController)

    }) { innerPadding ->
        // 네브호스트 , 네브컨틀로러 : 화면간의 이동을 관리하는 역할
        // 네브호스트 : 각 화면의 경로를 정해 놓은 곳
        NavHost(
            navController = navController,
            startDestination = "voteList",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.VoteList.route) {
                VoteListScreen(
                    parentNavController = navController,
                    viewModel = voteListViewModel,
                    onVoteClicked = { voteId ->
                        parentNavController.navigate("vote/$voteId")

                    }
                )
            }


            composable(BottomNavItem.Chat.route) {
                ChatScreen(parentNavController)
            }

            composable("chatRoom/{chatRoomId}/{otherUserId}/{otherUserNickname}") { backStackEntry ->
                val chatRoomId = backStackEntry.arguments?.getString("chatRoomId") ?: ""
                val otherUserId = backStackEntry.arguments?.getString("otherUserId") ?: ""
                val otherUserNickname = backStackEntry.arguments?.getString("otherUserNickname") ?: ""
                ChatRoomScreen(
                    chatRoomId,
                    otherUserId,
                    otherUserNickname
                )
            }

            composable(BottomNavItem.MyPage.route) {
                MyPageScreen(parentNavController)
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

