package com.example.szakdolg.logging;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LogApiService {
    @POST("logs/crash-report")
    Call<Void> sendCrashReport(
            @Body CrashLog crashLog
    );
}
