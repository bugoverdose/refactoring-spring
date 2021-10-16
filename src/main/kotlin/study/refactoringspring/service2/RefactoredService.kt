package study.refactoringspring.service2

import study.refactoringspring.service2.command.CreateUser
import study.refactoringspring.service2.command.EditNerdPoint
import study.refactoringspring.service2.model.FullUserInfo
import study.refactoringspring.service2.model.UserInfo

interface RefactoredService {
    fun getMe(userId: Long): FullUserInfo
    fun getByStatus(status: String): List<UserInfo>
    fun createUser(command: CreateUser)
    fun editNerdPoint(command: EditNerdPoint)
}