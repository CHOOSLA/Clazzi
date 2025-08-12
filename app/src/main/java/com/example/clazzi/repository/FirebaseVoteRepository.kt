package com.example.clazzi.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.clazzi.model.Vote
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseVoteRepository : VoteRepository {

    val db = Firebase.firestore
    override fun observeVotes(): Flow<List<Vote>> = callbackFlow {
        // 뷰모델 초기화 시 실시간 리스너 설정
        val listener = db.collection("votes")
            .orderBy("createAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if(error != null) {
                    Log.e("FireStore", "Error getting votes", error)
                    // 에러가 있으면 구독을 취소해버린다.
                    close(error)
                }else if(snapshot != null){
                    val votes = snapshot.toObjects(Vote::class.java)
                    trySend(votes)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addVote(
        vote: Vote,
        context: Context,
        imageUri: Uri
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun setVote(vote: Vote) {
        TODO("Not yet implemented")
    }
}