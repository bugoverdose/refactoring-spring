package study.refactoringspring.service3.event

import study.refactoringspring.db.UserEntity

data class UserCreated(
    val userName: String,
)

data class NerdPointEdited(
    val user: UserEntity,
    val statusChanged: Boolean,
)