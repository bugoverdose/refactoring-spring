package study.refactoringspring.config

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import study.refactoringspring.controller.response.ErrorResponse

@ControllerAdvice
class ExceptionHandler {
    private val logger = LoggerFactory.getLogger(this::javaClass.name)

    // 아래에서 설정하지 않은 모든 Exception 종류에 대해 업캐스팅되어 동작.
    @ExceptionHandler(value = [Exception::class])
    fun internalServerError(e: Exception) =
        ResponseEntity(ErrorResponse(error = "알 수 없는 에러가 발생했습니다."), HttpStatus.INTERNAL_SERVER_ERROR).also {
            logger.error(e.message, e) // 디버깅을 위해 에러 메시지만 서버에 기록
        }

    @ExceptionHandler(value = [DataNotFoundException::class])
    fun notfound(e: RuntimeException) =
        ResponseEntity(ErrorResponse(error = e.message ?: "해당 데이터를 찾을 수 없습니다."), HttpStatus.NOT_FOUND)

    @ExceptionHandler(value = [BadRequestException::class])
    fun badRequest(e: RuntimeException) =
        ResponseEntity(ErrorResponse(error = e.message ?: "잘못된 요청입니다"), HttpStatus.BAD_REQUEST)

    @ExceptionHandler(value = [NotAllowedException::class])
    fun notAllowed(e: RuntimeException) =
        ResponseEntity(ErrorResponse(error = e.message ?: "접근 권한이 없습니다."), HttpStatus.FORBIDDEN)
}

class DataNotFoundException(message: String? = null) : RuntimeException(message)

class BadRequestException(message: String? = null) : RuntimeException(message)

class NotAllowedException(message: String? = null) : RuntimeException(message)