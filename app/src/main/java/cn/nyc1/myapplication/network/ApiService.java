package cn.nyc1.myapplication.network;

import java.util.List;

import cn.nyc1.myapplication.model.ActiveCheckInTask;
import cn.nyc1.myapplication.model.AttendanceStats;
import cn.nyc1.myapplication.model.CheckInRecord;
import cn.nyc1.myapplication.model.CheckInResult;
import cn.nyc1.myapplication.model.CheckInTaskDetail;
import cn.nyc1.myapplication.model.CheckInTask;
import cn.nyc1.myapplication.model.Course;
import cn.nyc1.myapplication.model.CreateCheckInTaskRequest;
import cn.nyc1.myapplication.model.LoginRequest;
import cn.nyc1.myapplication.model.LoginResponse;
import cn.nyc1.myapplication.model.SubmitCheckInRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @GET("auth/me")
    Call<ApiResponse<LoginResponse>> me(@Header("Authorization") String token);

    @POST("auth/logout")
    Call<ApiResponse<Void>> logout(@Header("Authorization") String token);

    @GET("student/courses")
    Call<ApiResponse<List<Course>>> getStudentCourses(@Header("Authorization") String token);

    @GET("student/schedule")
    Call<ApiResponse<List<Course>>> getStudentSchedule(@Header("Authorization") String token);

    @GET("student/check-in-tasks/active")
    Call<ApiResponse<List<ActiveCheckInTask>>> getActiveStudentCheckInTasks(@Header("Authorization") String token);

    @POST("student/check-in")
    Call<ApiResponse<CheckInResult>> submitCheckIn(
            @Header("Authorization") String token,
            @Body SubmitCheckInRequest request
    );

    @GET("student/check-in-records")
    Call<ApiResponse<List<CheckInRecord>>> getStudentCheckInRecords(@Header("Authorization") String token);

    @GET("teacher/courses")
    Call<ApiResponse<List<Course>>> getTeacherCourses(@Header("Authorization") String token);

    @POST("teacher/check-in-tasks")
    Call<ApiResponse<CheckInTask>> createCheckInTask(
            @Header("Authorization") String token,
            @Body CreateCheckInTaskRequest request
    );

    @GET("teacher/check-in-tasks")
    Call<ApiResponse<List<CheckInTask>>> getTeacherCheckInTasks(
            @Header("Authorization") String token,
            @Query("courseId") Long courseId
    );

    @POST("teacher/check-in-tasks/{taskId}/end")
    Call<ApiResponse<CheckInTask>> endCheckInTask(
            @Header("Authorization") String token,
            @Path("taskId") long taskId
    );

    @GET("teacher/check-in-tasks/{taskId}/detail")
    Call<ApiResponse<CheckInTaskDetail>> getTeacherCheckInTaskDetail(
            @Header("Authorization") String token,
            @Path("taskId") long taskId
    );

    @GET("teacher/courses/{courseId}/attendance-stats")
    Call<ApiResponse<List<AttendanceStats>>> getAttendanceStats(
            @Header("Authorization") String token,
            @Path("courseId") long courseId
    );

    @GET("courses/{courseId}")
    Call<ApiResponse<Course>> getCourseDetail(
            @Header("Authorization") String token,
            @Path("courseId") long courseId
    );

    @GET("courses/{courseId}/active-check-in-task")
    Call<ApiResponse<CheckInTask>> getActiveCheckInTask(
            @Header("Authorization") String token,
            @Path("courseId") long courseId
    );
}
