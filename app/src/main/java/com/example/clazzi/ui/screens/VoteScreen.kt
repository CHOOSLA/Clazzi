package com.example.clazzi.ui.screens

import android.R.attr.onClick
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.clazzi.model.Vote
import com.example.clazzi.model.VoteOption
import com.example.clazzi.ui.theme.ClazziTheme
import com.example.clazzi.viewmodel.VoteListViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoteScreen(vote: Vote, navController: NavController, viewModel: VoteListViewModel) {
    var selectionOption by remember { mutableStateOf(0) }
    var hasVoted by remember { mutableStateOf(false) }
    var coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "투표") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text(
                text = buildAnnotatedString {
                    append("친구들과 ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("서로 투표")
                    }
                    append("하며\n")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("익명")
                    }
                    append("으로 마음을 전해요")
                }, fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = vote.title, style = TextStyle(
                    fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = if (vote.imageUrl != null) rememberAsyncImagePainter(vote.imageUrl) else
                    painterResource(id = android.R.drawable.ic_menu_gallery),
                contentDescription = "투표 사진",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(Modifier.height(20.dp))

            vote.voteOptions.forEachIndexed { index, voteOption ->
                Button(
                    onClick = {
                        selectionOption = index
                    },
                    colors = ButtonDefaults.buttonColors(
                        if (selectionOption == index) Color(0xFF13F8A5) else Color.LightGray.copy(
                            alpha = 0.5f
                        )
                    ),
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(voteOption.optionText)
                }

                Spacer(Modifier.height(20.dp))
            }

            Button(
                onClick = {
                    if (!hasVoted) {
                        coroutineScope.launch {
                            val user = FirebaseAuth.getInstance().currentUser
                            val uid = user?.uid ?: "0"
                            val voteId = uid
                            val selectedOption = vote.voteOptions[selectionOption]

                            val updatedOption = selectedOption.copy(
                                voters = selectedOption.voters + voteId
                            )

                            val updatedOptions = vote.voteOptions.mapIndexed { index, option ->
                                if (index == selectionOption) updatedOption else option
                            }
                            val updatedVote = vote.copy(
                                voteOptions = updatedOptions
                            )

                            viewModel.setVote(updatedVote)
                            hasVoted = true;
                        }
                    }
                },
                enabled = !hasVoted,
                modifier = Modifier.width(200.dp)
            ) {
                Text("투표하기")
            }

        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun VoteScreenPreview() {
//    ClazziTheme {
//        VoteScreen(
//            Vote(
//                "1", "오늘 점심 뭐먹을 까요", listOf(
//                    VoteOption("1", "삼겹살"),
//                    VoteOption("2", "치킨"),
//                    VoteOption("3", "피자"),
//                )
//            ),
//            navController = Any(
//        )
//    }
//}