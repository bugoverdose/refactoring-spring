package study.refactoringspring.controller.response

sealed class Response

data class SuccessResponse<R>(
    val ok: Boolean = true,
    val error: String? = null,
    val data: R,
) : Response()
// Unit Return인 경우 디폴트 형식 : { "ok": true, "error": null, "data": {} }

data class ErrorResponse(
    val ok: Boolean = false,
    val error: String,
    val data: String? = null
) : Response()
// 에러 발생시 형식 : { "ok": false, "error": "에러메시지", "data": null }
