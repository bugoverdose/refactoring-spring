package study.refactoringspring.service2

import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import study.refactoringspring.config.BadRequestException
import study.refactoringspring.config.DataNotFoundException
import study.refactoringspring.db.UserRepository
import study.refactoringspring.service2.model.UserInfo
import study.refactoringspring.service2.command.CreateUser
import study.refactoringspring.service2.command.EditNerdPoint
import study.refactoringspring.service2.model.FullUserInfo

// 인터페이스 주입시 복수의 구현체가 있으면 @Primary가 붙은 구현체 하나만 주입하도록 지정
@Primary
@Service
class RefactoredServicePrimaryImpl(
    private val refactoredService: RefactoredServiceImpl,
    private val userRepository: UserRepository
): RefactoredService {
    override fun getMe(userId: Long): FullUserInfo =
        refactoredService.getMe(userId)

    override fun getByStatus(status: String): List<UserInfo> {
        if (status != "NERD" && status != "NORMAL")
            throw BadRequestException()

        return refactoredService.getByStatus(status)
    }

    override fun createUser(command: CreateUser) {
        if (userRepository.findByName(command.name).isPresent)
            throw BadRequestException("동명이인이 존재합니다.")

        refactoredService.createUser(command)
    }

    override fun editNerdPoint(command: EditNerdPoint) {
        userRepository.findById(command.userId).orElseThrow(::DataNotFoundException).let {
            if (command.additionalPoint == 0)
                throw BadRequestException()

            refactoredService.editNerdPoint(command)
        }
    }
}
