package com.example.organizadoracademico.di

import com.example.organizadoracademico.hardware.camera.CameraManager
import com.example.organizadoracademico.hardware.camera.ImageSaver
import com.example.organizadoracademico.hardware.vibration.VibratorManager
import org.koin.dsl.module

val hardwareModule = module {
    single { CameraManager(get()) }
    single { ImageSaver(get()) }
    single { VibratorManager(get()) }
}