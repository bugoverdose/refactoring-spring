package study.refactoringspring.service1.command

sealed class UserCommand

data class CreateUser(
    val name: String,
) : UserCommand()

data class EditNerdPoint(
    val userId: Long,
    val additionalPoint: Int, // 양수 혹은 음수값 모두 가능하도록
) : UserCommand()