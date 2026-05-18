package cn.nyc1.campuscheckin.controller;

import cn.nyc1.campuscheckin.common.ApiResponse;
import cn.nyc1.campuscheckin.dto.LoginRequest;
import cn.nyc1.campuscheckin.dto.LoginResponse;
import cn.nyc1.campuscheckin.config.OpenApiConfig;
import cn.nyc1.campuscheckin.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "认证接口", description = "学生和教师登录、当前用户、退出登录")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "登录", description = "学生或教师使用账号、密码和角色登录，成功后返回 SA-Token token。")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户", description = "根据 Authorization 请求头中的 token 返回当前登录用户信息。")
    @SecurityRequirement(name = OpenApiConfig.AUTHORIZATION_SCHEME)
    public ApiResponse<LoginResponse> me() {
        return ApiResponse.success(authService.currentUser());
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "注销当前 token 对应的登录状态。")
    @SecurityRequirement(name = OpenApiConfig.AUTHORIZATION_SCHEME)
    public ApiResponse<Void> logout() {
        authService.logout();
        return ApiResponse.success(null);
    }
}
