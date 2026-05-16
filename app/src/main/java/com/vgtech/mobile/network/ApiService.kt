package com.vgtech.mobile.network

import com.vgtech.mobile.network.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── AUTH ─────────────────────────────────────────────────────────────
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>

    // ── USUARIOS ─────────────────────────────────────────────────────────
    @GET("usuarios")
    suspend fun getUsuarios(@Query("puesto") puesto: String? = null): Response<List<UsuarioDto>>

    @GET("usuarios/{uid}")
    suspend fun getUsuario(@Path("uid") uid: String): Response<UsuarioDto>

    @POST("usuarios")
    suspend fun createUsuario(@Body request: CreateUsuarioDto): Response<Map<String, String>>

    @PUT("usuarios/{uid}")
    suspend fun updateUsuario(@Path("uid") uid: String, @Body request: UpdateUsuarioDto): Response<Map<String, String>>

    // ── PROYECTOS ────────────────────────────────────────────────────────
    @GET("proyectos")
    suspend fun getProyectos(
        @Query("supervisorUid") supervisorUid: String? = null,
        @Query("consultorUid")  consultorUid: String? = null,
        @Query("proveedorUid")  proveedorUid: String? = null
    ): Response<List<ProyectoDto>>

    @GET("proyectos/{id}")
    suspend fun getProyecto(@Path("id") id: String): Response<ProyectoDto>

    @POST("proyectos")
    suspend fun createProyecto(@Body request: CreateProyectoDto): Response<Map<String, String>>

    @PUT("proyectos/{id}")
    suspend fun updateProyecto(@Path("id") id: String, @Body request: UpdateProyectoDto): Response<Map<String, String>>

    // ── VACACIONES ───────────────────────────────────────────────────────
    @GET("vacaciones")
    suspend fun getVacaciones(@Query("empleadoUid") empleadoUid: String? = null): Response<List<VacacionDto>>

    @POST("vacaciones")
    suspend fun createVacacion(@Body request: CreateVacacionDto): Response<Map<String, String>>

    @PATCH("vacaciones/{id}/estado")
    suspend fun updateVacacionEstado(@Path("id") id: String, @Body request: Map<String, String>): Response<Map<String, String>>

    // ── REGISTRO DE HORAS ────────────────────────────────────────────────
    @GET("registro-horas")
    suspend fun getRegistroHoras(@Query("empleadoUid") empleadoUid: String? = null): Response<List<RegistroHorasDto>>

    @POST("registro-horas")
    suspend fun createRegistroHoras(@Body request: CreateRegistroHorasDto): Response<Map<String, String>>

    // ── PROGRESO ─────────────────────────────────────────────────────────
    @GET("progreso")
    suspend fun getProgreso(
        @Query("proyectoId") proyectoId: String? = null,
        @Query("proveedorUid") proveedorUid: String? = null
    ): Response<List<ProgresoDto>>

    @POST("progreso")
    suspend fun createProgreso(@Body request: CreateProgresoDto): Response<Map<String, String>>

    @PUT("progreso/{id}/evaluar")
    suspend fun evaluarProgreso(@Path("id") id: String, @Body request: EvaluarProgresoDto): Response<Map<String, String>>

    // ── INVITACIONES ─────────────────────────────────────────────────────
    @GET("invitaciones")
    suspend fun getInvitaciones(
        @Query("proveedorUid") proveedorUid: String? = null,
        @Query("supervisorUid") supervisorUid: String? = null
    ): Response<List<InvitacionDto>>

    @POST("invitaciones")
    suspend fun createInvitacion(@Body request: CreateInvitacionDto): Response<Map<String, String>>

    @PATCH("invitaciones/{id}/estado")
    suspend fun updateInvitacionEstado(@Path("id") id: String, @Body request: Map<String, String>): Response<Map<String, String>>

    // ── COTIZACIONES ─────────────────────────────────────────────────────
    @GET("cotizaciones")
    suspend fun getCotizaciones(
        @Query("proveedorUid") proveedorUid: String? = null,
        @Query("proyectoId")   proyectoId: String? = null
    ): Response<List<CotizacionDto>>

    @POST("cotizaciones")
    suspend fun createCotizacion(@Body request: CreateCotizacionDto): Response<Map<String, String>>

    @PATCH("cotizaciones/{id}/cliente-estado")
    suspend fun updateCotizacionClienteEstado(@Path("id") id: String, @Body request: Map<String, String>): Response<Map<String, String>>

    @PATCH("cotizaciones/{id}/confirmar-supervisor")
    suspend fun confirmarCotizacionSupervisor(@Path("id") id: String): Response<Map<String, String>>

    // ── EVALUACIONES ─────────────────────────────────────────────────────
    @GET("evaluaciones")
    suspend fun getEvaluaciones(@Query("evaluadoUid") evaluadoUid: String? = null): Response<List<EvaluacionDto>>

    @POST("evaluaciones")
    suspend fun createEvaluacion(@Body request: CreateEvaluacionDto): Response<Map<String, String>>

    // ── FASES DE PAGO ────────────────────────────────────────────────────
    @GET("fases-pago")
    suspend fun getFasesPago(@Query("proveedorId") proveedorId: String? = null): Response<List<FasePagoDto>>

    @POST("fases-pago")
    suspend fun createFasePago(@Body request: CreateFasePagoDto): Response<Map<String, String>>

    @PATCH("fases-pago/{id}/pagar")
    suspend fun marcarFasePagada(@Path("id") id: String): Response<Map<String, String>>

    @DELETE("fases-pago/{id}")
    suspend fun deleteFasePago(@Path("id") id: String): Response<Map<String, String>>

    // ── TRANSACCIONES ────────────────────────────────────────────────────
    @GET("transacciones")
    suspend fun getTransacciones(@Query("proveedorId") proveedorId: String? = null): Response<List<TransaccionDto>>

    @POST("transacciones")
    suspend fun createTransaccion(@Body request: CreateTransaccionDto): Response<Map<String, String>>

    // ── MENSAJES CHAT ────────────────────────────────────────────────────
    @GET("mensajes")
    suspend fun getMensajes(
        @Query("proyectoId") proyectoId: String? = null,
        @Query("remitenteUid") remitenteUid: String? = null
    ): Response<List<MensajeChatDto>>

    @POST("mensajes")
    suspend fun createMensaje(@Body request: CreateMensajeChatDto): Response<Map<String, String>>

    // ── FASES DE PROYECTO ────────────────────────────────────────────────
    @GET("fases-proyecto")
    suspend fun getFasesProyecto(@Query("proyectoId") proyectoId: String? = null): Response<List<FaseProyectoDto>>

    @POST("fases-proyecto")
    suspend fun createFaseProyecto(@Body request: CreateFaseProyectoDto): Response<Map<String, String>>

    @PUT("fases-proyecto/{id}")
    suspend fun updateFaseProyecto(@Path("id") id: String, @Body request: UpdateFaseProyectoDto): Response<Map<String, String>>

    // ── CANCELACIONES ────────────────────────────────────────────────────
    @GET("cancelaciones")
    suspend fun getCancelaciones(): Response<List<CancelacionDto>>

    @POST("cancelaciones")
    suspend fun createCancelacion(@Body request: CreateCancelacionDto): Response<Map<String, String>>

    @PATCH("cancelaciones/{id}/estado")
    suspend fun updateCancelacionEstado(@Path("id") id: String, @Body request: Map<String, String>): Response<Map<String, String>>
}
