package com.example.organizadoracademico.data.remote

import com.example.organizadoracademico.data.remote.dto.AuthResponseDto
import com.example.organizadoracademico.data.remote.dto.CreateHorarioRequestDto
import com.example.organizadoracademico.data.remote.dto.DeleteResponseDto
import com.example.organizadoracademico.data.remote.dto.HorarioDto
import com.example.organizadoracademico.data.remote.dto.ImagenDto
import com.example.organizadoracademico.data.remote.dto.LoginRequestDto
import com.example.organizadoracademico.data.remote.dto.MateriaDto
import com.example.organizadoracademico.data.remote.dto.ProfesorDto
import com.example.organizadoracademico.data.remote.dto.PushTokenRequestDto
import com.example.organizadoracademico.data.remote.dto.RegisterRequestDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Endpoints HTTP de la API.
 * Iremos agregando aquí los endpoints reales (usuarios, materias, horarios, etc.).
 */
interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<AuthResponseDto>

    @POST("push/token")
    suspend fun registerPushToken(@Body request: PushTokenRequestDto): Response<Unit>

    @POST("devices/token")
    suspend fun registerDeviceToken(@Body request: PushTokenRequestDto): Response<Unit>

    @GET("materias")
    suspend fun getMaterias(): Response<List<MateriaDto>>

    @GET("profesores")
    suspend fun getProfesores(): Response<List<ProfesorDto>>

    @GET("horarios")
    suspend fun getHorarios(): Response<List<HorarioDto>>

    @POST("horarios")
    suspend fun createHorario(@Body request: CreateHorarioRequestDto): Response<HorarioDto>

    @PUT("horarios/{id}")
    suspend fun updateHorario(@Path("id") id: Int, @Body request: CreateHorarioRequestDto): Response<HorarioDto>

    @DELETE("horarios/{id}")
    suspend fun deleteHorario(@Path("id") id: Int): Response<DeleteResponseDto>

    @GET("imagenes")
    suspend fun getImagenes(@Query("materiaId") materiaId: Int? = null): Response<List<ImagenDto>>

    @Multipart
    @POST("imagenes")
    suspend fun uploadImagen(
        @Part file: MultipartBody.Part,
        @Part("materiaId") materiaId: RequestBody,
        @Part("nota") nota: RequestBody?,
        @Part("horarioId") horarioId: RequestBody?
    ): Response<ImagenDto>

    @DELETE("imagenes/{id}")
    suspend fun deleteImagen(@Path("id") id: Int): Response<DeleteResponseDto>

    // Endpoint simple para validar conectividad cuando exista en backend.
    @GET("health")
    suspend fun healthCheck(): Response<Map<String, Any>>
}
