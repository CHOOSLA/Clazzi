package com.example.clazzi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clazzi.ui.screens.AuthScreen
import com.example.clazzi.ui.screens.CreateVoteScreen
import com.example.clazzi.ui.screens.VoteListScreen
import com.example.clazzi.ui.screens.VoteScreen
import com.example.clazzi.ui.theme.ClazziTheme
import com.example.clazzi.viewmodel.VoteListViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {


    fun onVoteClicked(voteId: String) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClazziTheme {
                val navController = rememberNavController()
                val viewListViewModel = viewModel<VoteListViewModel>()
                val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
                NavHost(
                    navController = navController,
                    startDestination = if (isLoggedIn) "voteList" else "auth"
                ) {

                    composable("auth") {
                        AuthScreen(navController)
                    }
//                    composable("vote") {
//                        VoteScreen(
//                            navController = navController
//                        )
//                    }

                    composable("mypage") {
                        AuthScreen(navController)
                    }
                    composable("voteList") {
                        VoteListScreen(
                            navController = navController,
                            viewModel = viewListViewModel,
                            onVoteClicked = { voteId ->
                                navController.navigate("vote/$voteId")

                            }
                        )
                    }

                    composable("vote/{voteId}") { backStackEntry ->
                        val voteId = backStackEntry.arguments?.getString("voteId") ?: "1"
                        val vote = viewListViewModel.getVoteById(voteId)
                        VoteScreen(
                            voteId = voteId,
                            navController = navController,
                            voteListViewModel = viewListViewModel
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
                            viewListViewModel
                            ,
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




