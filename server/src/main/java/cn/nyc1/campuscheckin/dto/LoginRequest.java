package cn.nyc1.campuscheckin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "登录请求")
public class LoginRequest {

    @Schema(description = "登录账号", example = "s20260001")
    @NotBlank(message = "账号不能为空")
    private String username;

    @Schema(description = "登录密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "登录角色：STUDENT 或 TEACHER", example = "STUDENT", allowableValues = {"STUDENT", "TEACHER"})
    @NotBlank(message = "角色不能为空")
    private String role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
