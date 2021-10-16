package study.refactoringspring.service3.command

import study.refactoringspring.config.BadRequestException
import study.refactoringspring.db.UserEntity

sealed class UserCommand

data class CreateUser(
    val name: String,
) : UserCommand() {
    fun toEntity() = UserEntity(name = name, nerdPoint = 0)
}

data class EditNerdPoint(
    val userId: Long,
    val additionalPoint: Int, // 양수 혹은 음수값 모두 가능하도록
) : UserCommand() {
    init {
        if (additionalPoint == 0)
            throw BadRequestException()
    }
}
