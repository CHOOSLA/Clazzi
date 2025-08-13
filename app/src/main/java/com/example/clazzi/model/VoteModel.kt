package com.example.clazzi.model

import java.util.Date

// 데이터와 UI 상태를 관리하고, 생명주기 변화에 안전하게 데이터를 보존
data class Vote(
    val id: String = "",
    val title: String = "",
    val createAt: Date? = null,
    val voteOptions: List<VoteOption> = emptyList<VoteOption>(),
    val imageUrl: String? = null,
    val deadline: Date? = null,
){
    val optionCount: Int // 투표 세부 항목 개 수
        get() = voteOptions.size
}

data class VoteOption(
    val id: String = "",
    val optionText : String = "",
    val voters: List<String> = emptyList(), // 각 투표의 투표자 Id 저장
){
    val voteCount: Int
        get() = voters.size
}