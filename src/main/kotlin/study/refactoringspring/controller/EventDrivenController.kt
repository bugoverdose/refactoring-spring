package study.refactoringspring.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import study.refactoringspring.controller.request.CreateUserInput
import study.refactoringspring.controller.request.EditNerdPointInput
import study.refactoringspring.controller.request.StatusInput
import study.refactoringspring.controller.response.SuccessResponse
import study.refactoringspring.service3.command.CreateUser
import study.refactoringspring.service3.command.EditNerdPoint
import study.refactoringspring.service3.model.FullUserInfo
import study.refactoringspring.service3.model.UserInfo
import study.refactoringspring.service3.EventDrivenService
import study.refactoringspring.service3.query.GetByStatus
import javax.transaction.Transactional

@RequestMapping("refactoring")
@RestController
class EventDrivenController(
    private val userService: EventDrivenService,
) {
    @GetMapping("/me")
    fun getMe(
        @RequestHeader("USER-ID") userId: Long // 커스텀 HTTP HEADER 값을 통해 현재 사용자 식별
    ): SuccessResponse<FullUserInfo> =
        SuccessResponse(data = userService.getMe(userId))

    @GetMapping("/users/{statusInput}")
    fun getByStatus(
        @PathVariable statusInput: StatusInput,
    ): SuccessResponse<List<UserInfo>> =
        SuccessResponse(
            data = userService.getByStatus(
                query = GetByStatus(statusInput.status)
            )
        )

    // @Transactional : DB 변경 로직에 필요. 에러 발생시 롤백 등 기능 제공.
    @Transactional
    @PostMapping("/user")
    fun createUser(
        @RequestBody createUserInput: CreateUserInput,
    ): SuccessResponse<Unit> = // 서비스 자체는 이벤트를 반환하므로, 컨트롤러단에 별도의 작업 필요
        userService.createUser(
            command = CreateUser(createUserInput.name)
        ).let { SuccessResponse(data=Unit) }

    @Transactional
    @PutMapping("/user")
    fun editNerdPoint(
        @RequestBody editNerdPointInput: EditNerdPointInput,
    ): SuccessResponse<Unit> =
        userService.editNerdPoint(
            command = EditNerdPoint(
                userId = editNerdPointInput.userId,
                additionalPoint = editNerdPointInput.additionalPoint.toInt()
            )
        ).let { SuccessResponse(data=Unit) }
}