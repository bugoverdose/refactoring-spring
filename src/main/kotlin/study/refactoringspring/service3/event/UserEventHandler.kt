package study.refactoringspring.service3.event

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import study.refactoringspring.db.Status

@Component
class UserEventHandler {
    private val logger = LoggerFactory.getLogger(this::javaClass.name)

    @EventListener
    fun handle(event: UserCreated) {
        logger.info("여어 ${event.userName}쿤! 어서오고")
    }

    @EventListener
    fun handle(event: NerdPointEdited) {
        val (user, statusChanged) = event // Destructuring Declarations : 자스의 Object Destructuring 같은 것
        if (!statusChanged) return

        if (user.status == Status.NERD) {
            logger.info("${user.name}님이 너드가 되었습니다.")
        } else {
            logger.info("${user.name}님께서 안타깝게도 일반인이 되었습니다.")
        }
    }
}