package cn.nyc1.campuscheckin.service;

import cn.nyc1.campuscheckin.dto.ActiveCheckInTaskResponse;
import cn.nyc1.campuscheckin.dto.CheckInRecordResponse;
import cn.nyc1.campuscheckin.dto.CheckInResultResponse;
import cn.nyc1.campuscheckin.dto.CheckInTaskResponse;
import cn.nyc1.campuscheckin.dto.CourseResponse;
import cn.nyc1.campuscheckin.dto.CreateCheckInTaskRequest;
import cn.nyc1.campuscheckin.dto.SubmitCheckInRequest;
import cn.nyc1.campuscheckin.entity.CheckInRecord;
import cn.nyc1.campuscheckin.entity.CheckInTask;
import cn.nyc1.campuscheckin.entity.Student;
import cn.nyc1.campuscheckin.entity.Teacher;
import cn.nyc1.campuscheckin.mapper.CheckInRecordMapper;
import cn.nyc1.campuscheckin.mapper.CheckInTaskMapper;
import cn.nyc1.campuscheckin.mapper.CourseMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckInService {
    private static final String TYPE_PASSWORD = "PASSWORD";
    private static final String TYPE_QR_CODE = "QR_CODE";

    private final AuthService authService;
    private final CourseMapper courseMapper;
    private final CheckInTaskMapper checkInTaskMapper;
    private final CheckInRecordMapper checkInRecordMapper;

    public CheckInService(
            AuthService authService,
            CourseMapper courseMapper,
            CheckInTaskMapper checkInTaskMapper,
            CheckInRecordMapper checkInRecordMapper
    ) {
        this.authService = authService;
        this.courseMapper = courseMapper;
        this.checkInTaskMapper = checkInTaskMapper;
        this.checkInRecordMapper = checkInRecordMapper;
    }

    public CheckInTaskResponse activeTask(Long courseId) {
        CourseResponse course = courseMapper.findById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        return checkInTaskMapper.findActiveResponseByCourseId(courseId);
    }

    public List<ActiveCheckInTaskResponse> activeTasksForCurrentStudent() {
        Student student = authService.currentStudent();
        return checkInTaskMapper.findActiveResponsesByStudentId(student.getStudentId());
    }

    public List<CheckInTaskResponse> teacherTasks(Long courseId) {
        Teacher teacher = authService.currentTeacher();
        if (courseId != null) {
            CourseResponse course = courseMapper.findById(courseId);
            if (course == null) {
                throw new IllegalArgumentException("课程不存在");
            }
            if (!teacher.getTeacherId().equals(course.getTeacherId())) {
                throw new IllegalArgumentException("只能查看自己课程的签到任务");
            }
        }
        return checkInTaskMapper.findResponsesByTeacherId(teacher.getTeacherId(), courseId);
    }

    @Transactional
    public CheckInTaskResponse createTask(CreateCheckInTaskRequest request) {
        Teacher teacher = authService.currentTeacher();
        CourseResponse course = courseMapper.findById(request.getCourseId());
        if (course == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        if (!teacher.getTeacherId().equals(course.getTeacherId())) {
            throw new IllegalArgumentException("只能为自己的课程发起签到");
        }
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new IllegalArgumentException("开始时间必须早于截止时间");
        }
        String checkInType = resolveCheckInType(request.getCheckInType());
        String password = trimToNull(request.getPassword());
        String qrToken = null;
        if (TYPE_PASSWORD.equals(checkInType)) {
            if (password == null) {
                throw new IllegalArgumentException("课堂口令不能为空");
            }
        } else {
            qrToken = UUID.randomUUID().toString().replace("-", "");
        }

        CheckInTask task = new CheckInTask();
        task.setCourseId(request.getCourseId());
        task.setTitle(request.getTitle());
        task.setCheckInType(checkInType);
        task.setPassword(TYPE_PASSWORD.equals(checkInType) ? password : null);
        task.setQrToken(qrToken);
        task.setStartTime(request.getStartTime());
        task.setEndTime(request.getEndTime());
        task.setCreatedBy(teacher.getTeacherId());
        task.setStatus(resolveTaskStatus(request.getStartTime(), request.getEndTime(), LocalDateTime.now()));
        checkInTaskMapper.insert(task);

        CheckInTaskResponse response = new CheckInTaskResponse();
        response.setTaskId(task.getTaskId());
        response.setCourseId(task.getCourseId());
        response.setCourseName(course.getCourseName());
        response.setTitle(task.getTitle());
        response.setCheckInType(task.getCheckInType());
        response.setStartTime(task.getStartTime());
        response.setEndTime(task.getEndTime());
        response.setStatus(task.getStatus());
        return response;
    }

    @Transactional
    public CheckInTaskResponse endTask(Long taskId) {
        Teacher teacher = authService.currentTeacher();
        CheckInTaskResponse existing = checkInTaskMapper.findResponseByIdAndTeacherId(taskId, teacher.getTeacherId());
        if (existing == null) {
            throw new IllegalArgumentException("签到任务不存在，或无权操作该任务");
        }
        if ("ENDED".equals(existing.getStatus())) {
            return existing;
        }
        if (!"ACTIVE".equals(existing.getStatus())) {
            throw new IllegalArgumentException("只能手动截止进行中的签到任务");
        }

        LocalDateTime now = LocalDateTime.now();
        int updated = checkInTaskMapper.endTask(taskId, teacher.getTeacherId(), now);
        if (updated == 0) {
            throw new IllegalArgumentException("签到任务截止失败，请刷新后重试");
        }
        return checkInTaskMapper.findResponseByIdAndTeacherId(taskId, teacher.getTeacherId());
    }

    @Transactional
    public CheckInResultResponse submit(SubmitCheckInRequest request) {
        Student student = authService.currentStudent();
        CheckInTask task = checkInTaskMapper.findById(request.getTaskId());
        if (task == null) {
            throw new IllegalArgumentException("签到任务不存在");
        }
        if ("CANCELLED".equals(task.getStatus())) {
            throw new IllegalArgumentException("签到任务已取消");
        }
        if ("ENDED".equals(task.getStatus())) {
            throw new IllegalArgumentException("签到已结束");
        }
        if (courseMapper.countActiveEnrollment(task.getCourseId(), student.getStudentId()) == 0) {
            throw new IllegalArgumentException("你未加入该课程");
        }
        CheckInRecord existing = checkInRecordMapper.findByTaskIdAndStudentId(task.getTaskId(), student.getStudentId());
        if (existing != null) {
            throw new IllegalArgumentException("请勿重复签到");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(task.getStartTime())) {
            throw new IllegalArgumentException("签到尚未开始");
        }
        if (now.isAfter(task.getEndTime())) {
            throw new IllegalArgumentException("签到已结束");
        }

        String checkInType = resolveCheckInType(task.getCheckInType());
        if (TYPE_PASSWORD.equals(checkInType)) {
            String password = trimToNull(request.getPassword());
            if (password == null || !password.equals(task.getPassword())) {
                throw new IllegalArgumentException("签到口令错误");
            }
        } else if (TYPE_QR_CODE.equals(checkInType)) {
            String qrToken = trimToNull(request.getQrToken());
            if (qrToken == null || !qrToken.equals(task.getQrToken())) {
                throw new IllegalArgumentException("二维码无效或已过期");
            }
        }

        String status = "SIGNED";
        CheckInRecord record = new CheckInRecord();
        record.setTaskId(task.getTaskId());
        record.setCourseId(task.getCourseId());
        record.setStudentId(student.getStudentId());
        record.setCheckInTime(now);
        record.setStatus(status);
        record.setRemark("SIGNED".equals(status) ? "按时签到" : "迟到签到");
        checkInRecordMapper.insert(record);

        CourseResponse course = courseMapper.findById(task.getCourseId());
        CheckInResultResponse response = new CheckInResultResponse();
        response.setRecordId(record.getRecordId());
        response.setTaskId(task.getTaskId());
        response.setCourseId(task.getCourseId());
        response.setCourseName(course == null ? "" : course.getCourseName());
        response.setCheckInTime(now);
        response.setStatus(status);
        response.setMessage("SIGNED".equals(status) ? "签到成功" : "签到成功，状态为迟到");
        return response;
    }

    public List<CheckInRecordResponse> studentRecords(
            Long courseId,
            String status,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Student student = authService.currentStudent();
        if (courseId != null && courseMapper.countActiveEnrollment(courseId, student.getStudentId()) == 0) {
            throw new IllegalArgumentException("你未加入该课程");
        }
        String normalizedStatus = trimToNull(status);
        if (normalizedStatus != null && !List.of("SIGNED", "LATE", "ABSENT", "EXCEPTION").contains(normalizedStatus)) {
            throw new IllegalArgumentException("签到状态筛选条件不正确");
        }
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }
        return checkInRecordMapper.findResponsesByStudentId(
                student.getStudentId(),
                courseId,
                normalizedStatus,
                startDate,
                endDate
        );
    }

    private String resolveTaskStatus(LocalDateTime startTime, LocalDateTime endTime, LocalDateTime now) {
        if (now.isBefore(startTime)) {
            return "NOT_STARTED";
        }
        if (now.isAfter(endTime)) {
            return "ENDED";
        }
        return "ACTIVE";
    }

    private String resolveCheckInType(String checkInType) {
        String normalized = trimToNull(checkInType);
        if (normalized == null) {
            return TYPE_PASSWORD;
        }
        normalized = normalized.toUpperCase(Locale.ROOT);
        if (!TYPE_PASSWORD.equals(normalized) && !TYPE_QR_CODE.equals(normalized)) {
            throw new IllegalArgumentException("签到方式不支持");
        }
        return normalized;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
