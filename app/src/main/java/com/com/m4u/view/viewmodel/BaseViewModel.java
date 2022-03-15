package com.com.m4u.view.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class BaseViewModel extends ViewModel {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    protected APICallBack apiCallBack;

    public void setApiCallBack(APICallBack apiCallBack) {
        this.apiCallBack = apiCallBack;
    }

    protected Retrofit getWebApi() {
        return new Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient
                        .Builder()
                        .callTimeout(30, TimeUnit.SECONDS)
                        .build())
                .build();
    }

    protected <T> Callback<T> initCallBack(String apiKey) {
        return new Callback<T>() {

            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (response.code() == 200) {
                    apiSuccess(apiKey, response.body());
                } else {
                    apiError(apiKey, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                apiException(apiKey, t);
            }
        };

    }

    protected void apiException(String apiKey, Throwable t) {
        apiCallBack.apiException(apiKey, t);
    }

    protected <T> void apiError(String apiKey, Response<T> response) {
        apiCallBack.apiError(apiKey, response);
    }

    protected <T> void apiSuccess(String apiKey, T body) {
        apiCallBack.apiSuccess(apiKey, body);
    }

    public interface APICallBack {
        void apiSuccess(String key, Object data);

        void apiError(String key, Object data);

        void apiException(String key, Object data);
    }
}
