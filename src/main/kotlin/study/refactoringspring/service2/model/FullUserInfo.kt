package study.refactoringspring.service2.model

import study.refactoringspring.db.UserEntity

data class FullUserInfo (
    val id: Long,
    val name: String,
    val nerdPoint: Long,
    val status: String
){
    companion object {
        fun of(
            e: UserEntity
        ): FullUserInfo =
            FullUserInfo(
                id = e.id,
                name = e.name,
                nerdPoint = e.nerdPoint,
                status = e.status.toString()
            )
    }
}