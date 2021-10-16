package study.refactoringspring.service3

import study.refactoringspring.service3.command.CreateUser
import study.refactoringspring.service3.command.EditNerdPoint
import study.refactoringspring.service3.event.NerdPointEdited
import study.refactoringspring.service3.event.UserCreated
import study.refactoringspring.service3.model.FullUserInfo
import study.refactoringspring.service3.model.UserInfo
import study.refactoringspring.service3.query.GetByStatus

interface EventDrivenService {
    fun getMe(userId: Long): FullUserInfo
    fun getByStatus(query: GetByStatus): List<UserInfo>
    fun createUser(command: CreateUser): UserCreated
    fun editNerdPoint(command: EditNerdPoint): NerdPointEdited
}