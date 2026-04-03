package com.example.organizadoracademico.di

import com.example.organizadoracademico.data.remote.ApiService
import com.example.organizadoracademico.data.remote.RetrofitClient
import org.koin.dsl.module

val networkModule = module {
    single<ApiService> { RetrofitClient.createApiService(get()) }
}


