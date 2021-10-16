package study.refactoringspring.service3.model

import study.refactoringspring.db.UserEntity

// model은 Front로 보낼 응답에 사용될 객체의 형식
data class UserInfo (
    val id: Long,
    val name: String,
    val nerdPoint: Long, // 실제 DB 테이블에 저장된 User 엔티티의 형식과 달라도 됨
){
    companion object {
        fun of(
            e: UserEntity
        ): UserInfo =
            UserInfo(
                id = e.id,
                name = e.name,
                nerdPoint = e.nerdPoint,
            )
    }
}