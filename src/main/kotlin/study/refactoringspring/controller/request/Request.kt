package study.refactoringspring.controller.request

data class StatusInput(
    val status: String
)

data class CreateUserInput(
    val name: String,
)

data class EditNerdPointInput(
    val userId: Long,
    val additionalPoint: Long
)
