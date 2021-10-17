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
    // 참고: Spring AOP PointCut 표현식 정리(https://icarus8050.tistory.com/8)
    // @Pointcut : 어떤 메서드가 호출되었을 때 AOP가 동작할지를 지정
    // 특정 어노테이션이 붙은 경우, 특정 클래스명 형식의 클래스들에 대해서만 동작하도록 등록하는 등 다양하게 지정 가능.

    // 조건1) service3 하위의 ~ServiceImpl 클래스의 모든 메서드에 대해 동작
    // @Pointcut("execution(* study.refactoringspring.service3..*ServiceImpl.*(..))") // 둘 다 동일하게 동작
    @Pointcut("within(study.refactoringspring.service3.*ServiceImpl)")
    fun service3() {}

    // 조건2) 해당 어노테이션이 붙어있는 경우에만 동작
    @Pointcut("@annotation(study.refactoringspring.config.PublishEvent)")
    fun annotated() {}

    // 두 가지 조건을 동시에 충족하면 동작. 주의: AOP는 요청의 단위가 아니라 실행의 단위
    // cf) 사실 해당 프로젝트의 경우 두 개 중 하나만 충족시키도록 설정해도 무관함. 예시코드일뿐.
    @Around("service3() && annotated()")
    fun doPublishEvent(joinPoint: ProceedingJoinPoint): Any? {
        val result = joinPoint.proceed()
        eventPublisher.publishEvent(result)
        // 커스텀 어노테이션을 사용하지 않고, result의 상위클래스 타입 등을 체크하는 등 구현방법은 다양함
        return result
    }
}
