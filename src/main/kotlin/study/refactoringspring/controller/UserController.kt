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
import study.refactoringspring.service1.VerboseService
import study.refactoringspring.service1.command.CreateUser
import study.refactoringspring.service1.command.EditNerdPoint
import study.refactoringspring.service1.model.FullUserInfo
import study.refactoringspring.service1.model.UserInfo
import javax.transaction.Transactional

@RequestMapping("refactoring")
@RestController
class UserController(
    private val userService: VerboseService,
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
            data = userService.getByStatus(statusInput.status)
        )

    // @Transactional : DB 변경 로직에 필요. 에러 발생시 롤백 등 기능 제공.
    @Transactional
    @PostMapping("/user")
    fun createUser(
        @RequestBody createUserInput: CreateUserInput,
    ): SuccessResponse<Unit> = // Unit은 Java의 void와 유사. 아무것도 반환하지 않는 형식의 반환형.
        SuccessResponse(
            data = userService.createUser(
                command = CreateUser(createUserInput.name)
            )
        )

    @Transactional
    @PutMapping("/user")
    fun editNerdPoint(
        @RequestBody editNerdPointInput: EditNerdPointInput,
    ): SuccessResponse<Unit> =
        SuccessResponse(
            data = userService.editNerdPoint(
                command = EditNerdPoint(
                    userId = editNerdPointInput.userId,
                    additionalPoint = editNerdPointInput.additionalPoint.toInt()
                )
            )
        )
}