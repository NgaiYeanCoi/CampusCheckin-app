package cn.nyc1.campuscheckin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "学生提交签到请求")
public class SubmitCheckInRequest {

    @Schema(description = "签到任务 ID", example = "1")
    @NotNull(message = "签到任务ID不能为空")
    private Long taskId;

    @Schema(description = "课堂签到口令，PASSWORD 模式使用", example = "246810")
    private String password;

    @Schema(description = "二维码静态 token，QR_CODE 模式使用", example = "a1b2c3d4e5f6")
    private String qrToken;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQrToken() {
        return qrToken;
    }

    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }
}
