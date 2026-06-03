package cn.nyc1.campuscheckin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "签到任务统计汇总")
public class CheckInTaskSummaryResponse {

    @Schema(description = "应到人数", example = "4")
    private Integer totalCount;

    @Schema(description = "已签到人数", example = "2")
    private Integer signedCount;

    @Schema(description = "迟到人数", example = "0")
    private Integer lateCount;

    @Schema(description = "未签到人数", example = "1")
    private Integer unsignedCount;

    @Schema(description = "缺勤人数", example = "1")
    private Integer absentCount;

    @Schema(description = "异常人数", example = "0")
    private Integer exceptionCount;

    @Schema(description = "出勤率百分比", example = "50.00")
    private BigDecimal attendanceRate;

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getSignedCount() {
        return signedCount;
    }

    public void setSignedCount(Integer signedCount) {
        this.signedCount = signedCount;
    }

    public Integer getLateCount() {
        return lateCount;
    }

    public void setLateCount(Integer lateCount) {
        this.lateCount = lateCount;
    }

    public Integer getUnsignedCount() {
        return unsignedCount;
    }

    public void setUnsignedCount(Integer unsignedCount) {
        this.unsignedCount = unsignedCount;
    }

    public Integer getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(Integer absentCount) {
        this.absentCount = absentCount;
    }

    public Integer getExceptionCount() {
        return exceptionCount;
    }

    public void setExceptionCount(Integer exceptionCount) {
        this.exceptionCount = exceptionCount;
    }

    public BigDecimal getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(BigDecimal attendanceRate) {
        this.attendanceRate = attendanceRate;
    }
}
