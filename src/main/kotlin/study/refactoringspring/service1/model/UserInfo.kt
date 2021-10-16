package study.refactoringspring.service1.model

// model은 Front로 보낼 응답에 사용될 객체의 형식
data class UserInfo (
    val id: Long,
    val name: String,
    val nerdPoint: Long, // 실제 DB 테이블에 저장된 User 엔티티의 형식과 달라도 됨
)