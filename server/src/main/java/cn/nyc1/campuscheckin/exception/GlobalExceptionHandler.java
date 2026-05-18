package cn.nyc1.campuscheckin.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.nyc1.campuscheckin.common.ApiResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public ApiResponse<Void> handleNotLogin(NotLoginException ex) {
        return ApiResponse.error(401, "请先登录");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Void> handleIllegalArgument(IllegalArgumentException ex) {
        return ApiResponse.error(400, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidation(MethodArgumentNotValidException ex) {
        String message = "参数不完整";
        if (!ex.getBindingResult().getFieldErrors().isEmpty()) {
            message = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        }
        return ApiResponse.error(400, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return ApiResponse.error(400, "请求体格式错误");
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception ex) {
        return ApiResponse.error(500, "服务器内部错误");
    }
}
