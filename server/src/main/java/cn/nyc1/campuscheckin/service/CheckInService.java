package cn.nyc1.campuscheckin.service;

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
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckInService {

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

        CheckInTask task = new CheckInTask();
        task.setCourseId(request.getCourseId());
        task.setTitle(request.getTitle());
        task.setPassword(request.getPassword());
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
        if (!task.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("签到口令错误");
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

        String status = now.isAfter(task.getEndTime()) ? "LATE" : "SIGNED";
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

    public List<CheckInRecordResponse> studentRecords() {
        Student student = authService.currentStudent();
        return checkInRecordMapper.findResponsesByStudentId(student.getStudentId());
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
}
