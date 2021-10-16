package study.refactoringspring.service1

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import study.refactoringspring.config.BadRequestException
import study.refactoringspring.config.DataNotFoundException
import study.refactoringspring.db.Status
import study.refactoringspring.db.UserEntity
import study.refactoringspring.db.UserRepository
import study.refactoringspring.service1.command.CreateUser
import study.refactoringspring.service1.command.EditNerdPoint
import study.refactoringspring.service1.model.FullUserInfo
import study.refactoringspring.service1.model.UserInfo
import javax.annotation.PostConstruct

//@Service // 최대한 verbose하게 작성한 코드
class VerboseService(
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(this::javaClass.name)

    private val nerdBaseLine = 10 // 10점 이상이면 너드, 미만이면 일반인

    fun getMe(userId: Long): FullUserInfo =
        userRepository.findById(userId).orElseThrow(::DataNotFoundException).let {
            FullUserInfo(
                id = it.id,
                name = it.name,
                nerdPoint = it.nerdPoint,
                status = it.status.toString()
            )
        }

    fun getByStatus(status: String): List<UserInfo> =
        if (status == "NERD"){
            userRepository.findAllByStatus(Status.NERD).map {
                UserInfo(
                    id = it.id,
                    name = it.name,
                    nerdPoint = it.nerdPoint,
                )
            }
        } else if (status == "NORMAL") {
            userRepository.findAllByStatus(Status.NORMAL).map {
                UserInfo(
                    id = it.id,
                    name = it.name,
                    nerdPoint = it.nerdPoint,
                )
            }
        } else {
            throw BadRequestException()
        }

    fun createUser(command: CreateUser): Unit {
        if (userRepository.findByName(command.name).isPresent)
            throw BadRequestException("동명이인이 존재합니다.")

        val newUser = userRepository.save(
                UserEntity(
                    name = command.name,
                    nerdPoint = 0
                )
            )
        // 신규 사용자 정보를 로깅
        logger.info("여어 ${newUser.name}쿤! 어서오고")
    }

    fun editNerdPoint(command: EditNerdPoint): Unit =
        userRepository.findById(command.userId).orElseThrow(::DataNotFoundException).let {
            if (command.additionalPoint == 0) {
                throw BadRequestException()
            }
            val updatedPoint = it.nerdPoint + command.additionalPoint

            val prevStatus = it.status

            if (updatedPoint >= nerdBaseLine) {
                userRepository.save(
                    it.copy(
                        status = Status.NERD,
                        nerdPoint = updatedPoint
                    )
                )
                // 너드로 변경되었는지의 여부를 별도로 기록
                if (prevStatus == Status.NORMAL){
                    logger.info("${it.name}님이 너드가 되었습니다.")
                }
            } else {
                userRepository.save(
                    it.copy(
                        status = Status.NORMAL,
                        nerdPoint = updatedPoint
                    )
                )
                if (prevStatus == Status.NERD){
                    logger.info("${it.name}님께서 안타깝게도 일반인이 되었습니다.")
                }
            }
        }

    @PostConstruct
    fun init() {
        val nameList = listOf(
            "jeong", "jinu", "jinwoo", "zinu", "henry", "liha", "mingsu", "cloud",
            "bunny bernie", "no more build", "woori", "stevy", "vegi",
        )

        for (personName in nameList){
            createUser(CreateUser(name = personName))
        }
    }
}