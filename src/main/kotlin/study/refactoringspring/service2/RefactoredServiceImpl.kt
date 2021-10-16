package study.refactoringspring.service2

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import study.refactoringspring.config.BadRequestException
import study.refactoringspring.config.DataNotFoundException
import study.refactoringspring.db.Status
import study.refactoringspring.db.UserEntity
import study.refactoringspring.db.UserRepository
import study.refactoringspring.service2.command.CreateUser
import study.refactoringspring.service2.command.EditNerdPoint
import study.refactoringspring.service2.model.FullUserInfo
import study.refactoringspring.service2.model.UserInfo
import javax.annotation.PostConstruct

//@Service
class RefactoredServiceImpl(
    private val userRepository: UserRepository
): RefactoredService {
    // PrimaryImpl에는 없지만 이쪽 구현체에만 존재하는 멤버변수 및 함수들 존재 가능
    private val logger = LoggerFactory.getLogger(this::javaClass.name)
    private val nerdBaseLine = 10 // 10점 이상이면 너드, 미만이면 일반인

    @PostConstruct // 인터페이스에서 정의하지 않은 함수
    fun init() {
        val nameList = listOf(
            "jeong", "jinu", "jinwoo", "zinu", "henry", "liha", "mingsu", "cloud",
            "bunny bernie", "no more build", "woori", "stevy", "vegi",
        )

        for (personName in nameList) {
            createUser(CreateUser(name = personName))
        }
    }

    override fun getMe(userId: Long): FullUserInfo =
        userRepository.findById(userId).orElseThrow(::DataNotFoundException).let {
            FullUserInfo.of(it) // model의 companion object의 'of' 함수 활용하여 UserEntity를 FullUserInfo 모델 형식으로 변환
        }

    override fun getByStatus(status: String): List<UserInfo> {
        // 여전히 더러움 => 사용자로부터 status 정보를 문자열로 받기 때문에 별도의 작업 필요 => query 클래스 사용 등 다양한 방식으로 해결 가능할 것으로 보임
        val filterCondition = if (status == "NERD") {
            Status.NERD
        } else{
            Status.NORMAL
        }

        return userRepository.findAllByStatus(filterCondition).map {
            UserInfo.of(it)
        }
    }

    override fun createUser(command: CreateUser): Unit {
        userRepository.save(command.toEntity()).let { // toEntity: 해당 command 인스턴스의 정보를 Entity 형식으로 변환
            logger.info("여어 ${it.name}쿤! 어서오고")
        }
    }

    // 중요: PrimayImpl에서 findById로 가져온 UserEntity 정보를 다시 findById로 요청시,
    // JPA pool에 캐슁된 데이터를 그대로 사용하는 것으로 보임. 즉 DB에 별도로 SQL문을 날려서 성능 저하가 발생하지는 않는 것으로 추측됨.
    override fun editNerdPoint(command: EditNerdPoint): Unit {
        userRepository.findById(command.userId).get().let {
            // get() 사용한 이유: 이전 단계에서 데이터 존재 여부 이미 체크했고 예외처리도 수행했으므로 DataNotFound 불필요
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

            if (!changed) return@let

            if (it.status == Status.NERD) { // it.status == newStatus로 수정되었으므로 newStatus 사용해도 무관.
                logger.info("${it.name}님이 너드가 되었습니다.")
            } else {
                logger.info("${it.name}님께서 안타깝게도 일반인이 되었습니다.")
            }
        }
    }
}