package study.refactoringspring.service3

import org.springframework.stereotype.Service
import study.refactoringspring.config.BadRequestException
import study.refactoringspring.config.DataNotFoundException
import study.refactoringspring.config.PublishEvent
import study.refactoringspring.db.Status
import study.refactoringspring.db.UserRepository
import study.refactoringspring.service3.command.CreateUser
import study.refactoringspring.service3.command.EditNerdPoint
import study.refactoringspring.service3.event.NerdPointEdited
import study.refactoringspring.service3.event.UserCreated
import study.refactoringspring.service3.model.FullUserInfo
import study.refactoringspring.service3.model.UserInfo
import study.refactoringspring.service3.query.GetByStatus
import javax.annotation.PostConstruct

@Service
class EventDrivenServiceImpl(
    private val userRepository: UserRepository
): EventDrivenService {

    private val nerdBaseLine = 10 // 10점 이상이면 너드, 미만이면 일반인

    override fun getMe(userId: Long): FullUserInfo =
        userRepository.findById(userId).orElseThrow(::DataNotFoundException).let {
            FullUserInfo.of(it)
        }

    override fun getByStatus(query: GetByStatus): List<UserInfo> =
        userRepository.findAllByStatus(query.getEnumStatus()).map {
            UserInfo.of(it)
        }

    @PublishEvent // 반환값 받아서 eventPublisher.publishEvent(반환값) 실행시키는 커스텀 어노테이션(config 참고)
    override fun createUser(command: CreateUser): UserCreated {
        if (userRepository.findByName(command.name).isPresent) // DB 조회 작업이 필요한 방어로직이므로 서비스에서 실행
            throw BadRequestException("동명이인이 존재합니다.")

        userRepository.save(command.toEntity()).let {
            return UserCreated(userName=it.name)
        }
    }

    @PublishEvent
    override fun editNerdPoint(command: EditNerdPoint): NerdPointEdited =
        userRepository.findById(command.userId).orElseThrow(::DataNotFoundException).let {
            val updatedPoint = it.nerdPoint + command.additionalPoint

            val newStatus = if (updatedPoint >= nerdBaseLine) {
                Status.NERD
            } else {
                Status.NORMAL
            } // 코틀린의 경우 삼항연산자가 없어서 이런건 좀 더러움

            val changed: Boolean = (it.status != newStatus)

            userRepository.save(
                it.copy(
                    status = newStatus,
                    nerdPoint = updatedPoint
                )
            )
            return NerdPointEdited(user = it, statusChanged = changed)
        }

    @PostConstruct // 이 부분의 createUser의 각 실행에 대해 AOP가 동작하지 않는 이유는? => 숙제
    fun init() {
        val nameList = listOf(
            "jeong", "jinu", "jinwoo", "zinu", "henry", "liha", "mingsu", "cloud",
            "bunny bernie", "no more build", "woori", "stevy",
        )

        for (personName in nameList) {
            createUser(CreateUser(name = personName))
        }
    }
}
