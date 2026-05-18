package cn.nyc1.campuscheckin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "学生提交签到请求")
public class SubmitCheckInRequest {

    @Schema(description = "签到任务 ID", example = "1")
    @NotNull(message = "签到任务ID不能为空")
    private Long taskId;

    @Schema(description = "课堂签到口令", example = "246810")
    @NotBlank(message = "签到口令不能为空")
    private String password;

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
}
