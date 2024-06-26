package com.example.szakdolg.message;

import com.example.szakdolg.DTO.MessageBoard;



import java.util.ArrayList;


import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface MessageApiService {

    @GET("message/{token}/messageboardentries")
    Call<ArrayList<MessageBoard>> getLatestMessages(
            @Path("token") String token);

    @POST("message")
    Call<MessageEntry> sendMessage(
            @Body MessageEntry message
    );

    @GET("message/byconversationid/{id}")
    Call<ArrayList<MessageEntry>> getConversationMessages(
            @Path("id") long conversationId);

    @Multipart
    @POST("message/uploadimage") // Replace with your actual endpoint
    Call<ResponseBody> uploadFile(
            @Part MultipartBody.Part filePart);

}
