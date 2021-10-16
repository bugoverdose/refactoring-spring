package study.refactoringspring.db

import java.util.Optional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

// Repository는 @Entity의 형식에 맞추어 다양한 SQL문을 자동 생성해주는 기능
// 디폴트로 주어지는 JPA 메서드도 존재.
// 그 외에는 인터페이스에 직접 매개변수 & 반환 형식 지정 필요. override로 덮어쓰는 것도 가능.
interface UserRepository : JpaRepository<UserEntity, Long> {

    fun findAllByStatus(status: Status): List<UserEntity>

    fun findByName(name: String): Optional<UserEntity>

//    override fun findAll(pageable: Pageable): Page<UserEntity>

}
