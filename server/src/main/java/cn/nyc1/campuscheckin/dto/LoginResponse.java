package cn.nyc1.campuscheckin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "登录响应")
public class LoginResponse {

    @Schema(description = "SA-Token token", example = "e4be5ad9-c3d2-4fd0-aa60-c785e9ee449f")
    private String token;

    @Schema(description = "用户 ID", example = "1001")
    private Long userId;

    @Schema(description = "登录账号", example = "s20260001")
    private String username;

    @Schema(description = "用户角色", example = "STUDENT", allowableValues = {"STUDENT", "TEACHER"})
    private String role;

    @Schema(description = "角色资料 ID，学生为 studentId，教师为 teacherId", example = "1")
    private Long profileId;

    @Schema(description = "展示名称", example = "林一凡")
    private String displayName;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
