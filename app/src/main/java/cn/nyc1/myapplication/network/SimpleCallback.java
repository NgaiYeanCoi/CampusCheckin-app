package cn.nyc1.myapplication.network;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class SimpleCallback<T> implements Callback<ApiResponse<T>> {
    @Override
    public void onResponse(@NonNull Call<ApiResponse<T>> call, @NonNull Response<ApiResponse<T>> response) {
        ApiResponse<T> body = response.body();
        if (response.isSuccessful() && body != null && body.isSuccess()) {
            onSuccess(body.data);
            return;
        }
        String message = body == null || body.message == null ? "请求失败" : body.message;
        onError(message);
    }

    @Override
    public void onFailure(@NonNull Call<ApiResponse<T>> call, @NonNull Throwable t) {
        onError("后端不可用或网络连接失败");
    }

    public abstract void onSuccess(T data);

    public abstract void onError(String message);
}
