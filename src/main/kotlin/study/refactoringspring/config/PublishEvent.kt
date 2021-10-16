package study.refactoringspring.config

// 커스텀 어노테이션 지정
// 주의. 세부 설정 방식이 Java와 다름 (https://kotlinlang.org/docs/annotations.html)
@Target(AnnotationTarget.FUNCTION) // 클래스의 함수들 좌측에만 붙을 수 있는 어노테이션으로 지정
@Retention(AnnotationRetention.RUNTIME) // 컴파일된 클래스 파일에 포함 and/or 런타임에 보이도록 지정 (디폴트 값은 둘 다)
annotation class PublishEvent