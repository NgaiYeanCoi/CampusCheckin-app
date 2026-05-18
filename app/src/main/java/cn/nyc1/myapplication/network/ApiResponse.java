package cn.nyc1.myapplication.network;

public class ApiResponse<T> {
    public int code;
    public String message;
    public T data;

    public boolean isSuccess() {
        return code == 0;
    }
}
