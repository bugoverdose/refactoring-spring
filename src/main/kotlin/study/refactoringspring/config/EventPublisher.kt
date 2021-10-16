package study.refactoringspring.config

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Aspect // AOP
@Component
class EventPublisher(
    private val eventPublisher: ApplicationEventPublisher
) {
    // @Pointcut : 어떤 경우에 AOP가 동작할지를 지정
    // ex) @Pointcut("within(study.refactoringspring..*ServiceImpl)") : ~ServiceImpl로 끝나는 모든 클래스들 지정
    // 특정 어노테이션이 붙은 경우, 특정 클래스명 형식의 클래스들에 대해서만 동작하도록 등록하는 등 다양하게 지정 가능.

    // 조건1) service3 하위의 모든 메서드에 대해 동작
    @Pointcut("within(study.refactoringspring.service3..*.*(..))")
    fun service3() {}

    // 조건2) 해당 어노테이션이 붙어있는 경우에만 동작
    @Pointcut("@annotation(study.refactoringspring.config.PublishEvent)")
    fun annotated() {}

    // 두 가지 조건을 동시에 충족하면 동작. 주의: AOP는 요청의 단위가 아니라 실행의 단위
    @Around("service3() && annotated()")
    fun doPublishEvent(joinPoint: ProceedingJoinPoint): Any? {
        val result = joinPoint.proceed()
        eventPublisher.publishEvent(result)
        return result
    }
}
