package study.refactoringspring.db

import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

// @Entity는 SQL문 생성을 위해 사전에 지정해놓은 형식 - 특정 테이블과 해당 테이블의 특정 컬럼들 정보
@Table(name = "users")
@Entity
data class UserEntity( // data 클래스이므로 상속불가 + 생성자, 게터, 세터 등 자동 셋업
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L, // PK 제약조건. 데이터 생성 시점에 값 자동 생성.

    val name: String,

    val nerdPoint: Long,

    @Enumerated(EnumType.STRING)
    val status: Status = Status.NORMAL
)

enum class Status {
    NORMAL, NERD
}