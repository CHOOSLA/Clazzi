package com.example.clazzi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.clazzi.model.Vote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope;
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class VoteViewModel : ViewModel(){
    private val _vote = MutableStateFlow<Vote?>(null);
    val vote: StateFlow<Vote?> = _vote.asStateFlow()

    fun loadVote(voteId : String){
        Firebase.firestore.collection("votes").document(voteId)
            .addSnapshotListener { snapshot, error ->
                if(error != null){
                    // 오류처리
                    return@addSnapshotListener
                }
                if(snapshot != null && snapshot.exists()){
                    val vote = snapshot.toObject(Vote::class.java)
                    _vote.value = vote
                }
            }

    }
}