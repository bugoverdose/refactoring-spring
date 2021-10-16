package study.refactoringspring.service3.query

import study.refactoringspring.config.BadRequestException
import study.refactoringspring.db.Status

sealed class UserQuery

data class GetByStatus(
    val status: String,
) : UserQuery() {
    init {
        if (status != "NERD" && status != "NORMAL")
            throw BadRequestException()
    }

    fun getEnumStatus() = if (status == "NERD") { // 아마 구글링해보면 더 좋은 방법이 있을 것
        Status.NERD
    } else{
        Status.NORMAL
    }
}