package com.example.zavod.api

import com.example.zavod.model.EquipmentResponse
import com.example.zavod.model.ProfileResponse
import com.example.zavod.model.RepairRequest
import com.example.zavod.model.RepairTypesResponse
import com.example.zavod.model.ScheduleResponse
import com.example.zavod.model.StartResponse
import com.example.zavod.model.Step
import com.example.zavod.model.StepRequest
import com.example.zavod.model.StepResult
import com.example.zavod.model.auth.LoginRequest
import com.example.zavod.model.auth.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("check/start/{tagId}/{typeCheck}")
    fun startCheck(
        @Header("Authorization") authorization: String,
        @Path("tagId") tagId: String,
        @Path("typeCheck") typeCheck: String,
        @Query("equipment_id") equipmentId: Int?,
        @Query("template_id") templateId: Int?,
        @Query("schedule_id") scheduleId: Int?
    ): Call<StartResponse>

    @POST("check/step")
    fun checkStep(
        @Body request: StepRequest
    ): Call<StepResult>

    @POST("check/cancel/{inspectionId}")
    fun cancelCheck(
        @Header("Authorization") token: String,
        @Path("inspectionId") inspectionId: Int
    ): Call<ResponseBody>

    @Multipart
    @POST("/step/upload")
    fun uploadStep(
        @Part("sessionId") sessionId: RequestBody,
        @Part("stepId") stepId: RequestBody,
        @Part("tagId") tagId: RequestBody,
        @Part("value") value: RequestBody,
        @Part("comment") comment: RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<StepResult>

    @POST("repair")
    fun createRepair(
        @Header("Authorization") authorization: String,
        @Body request: RepairRequest
    ): Call<Void>

    @GET("/repair/types")
    fun getRepairTypes(): Call<RepairTypesResponse>

    @GET("step/{stepId}")
    fun getStep(
        @Path("stepId") stepId: String
    ): Call<Step>

    @POST("/auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @GET("profile/{pass_id}")
    fun getProfile(
        @Path("pass_id") passId: String
    ): Call<ProfileResponse>

    @GET("schedule/{pass_id}")
    fun getSchedule(
        @Path("pass_id") passId: String
    ): Call<ScheduleResponse>

    @GET("equipment/{pass_id}")
    fun getEquipment(
        @Path("pass_id") passId: String
    ): Call<EquipmentResponse>

    @GET("hint")
    fun getHintImage(
        @Query("equipment_id") equipmentId: Int,
        @Query("check_type") checkType: String
    ): Call<ResponseBody>
}