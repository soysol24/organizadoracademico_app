package com.example.organizadoracademico.di

import com.example.organizadoracademico.data.repository.*
import com.example.organizadoracademico.domain.repository.*
import com.example.organizadoracademico.push.PushTokenUploader
import com.example.organizadoracademico.push.PushTokenUploaderImpl
import com.example.organizadoracademico.domain.usercase.horario.*
import com.example.organizadoracademico.domain.usercase.imagen.*
import com.example.organizadoracademico.domain.usercase.materia.*
import com.example.organizadoracademico.domain.usercase.profesor.GetProfesoresUseCase
import com.example.organizadoracademico.domain.usercase.usuario.GetUsuarioUseCase
import com.example.organizadoracademico.domain.usercase.usuario.LoginUseCase
import com.example.organizadoracademico.domain.usercase.usuario.LogoutUseCase
import com.example.organizadoracademico.domain.usercase.usuario.RegistroUseCase
import com.example.organizadoracademico.domain.usercase.imagen.GetImagenUseCase
import com.example.organizadoracademico.presentation.horario.crear.CrearHorarioViewModel
import com.example.organizadoracademico.presentation.horario.ver.VerHorarioViewModel
import com.example.organizadoracademico.presentation.imagen.camara.CamaraViewModel
import com.example.organizadoracademico.presentation.imagen.detalle.DetalleImagenViewModel
import com.example.organizadoracademico.presentation.imagen.galeria.GaleriaViewModel
import com.example.organizadoracademico.presentation.imagen.nota.NotaViewModel
import com.example.organizadoracademico.presentation.login.LoginViewModel
import com.example.organizadoracademico.presentation.main.MainViewModel
import com.example.organizadoracademico.presentation.materia.MisMateriasViewModel
import com.example.organizadoracademico.presentation.perfil.PerfilViewModel
import com.example.organizadoracademico.presentation.registro.RegistroViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule = module {
    single { com.example.organizadoracademico.data.local.util.SessionManager(get()) }
    single<PushTokenUploader> { PushTokenUploaderImpl(get(), get()) }

    // Repositorios
    single<IMateriaRepository> { MateriaRepositoryImpl(get(), get()) }
    single<IProfesorRepository> { ProfesorRepositoryImpl(get(), get()) }
    single<IHorarioRepository> { HorarioRepositoryImpl(get(), get(), get(), get(), get(), get(), get()) }
    single<IImagenRepository> { ImagenRepositoryImpl(get(), get(), get(), get(), get()) }
    single<IUsuarioRepository> { UsuarioRepositoryImpl(get(), get(), get(), get(), get()) }
}

val useCaseModule = module {
    // Horarios
    factory { AddHorarioUseCase(get()) }
    factory { GetHorariosUseCase(get()) }
    factory { DeleteHorarioUseCase(get()) }
    factory { UpdateHorarioUseCase(get()) }

    // Materias y profesores
    factory { GetMateriasUseCase(get()) }
    factory { AddMateriaUseCase(get()) }
    factory { GetProfesoresUseCase(get()) }

    // Imágenes
    factory { GetImagenesPorMateriaUseCase(get()) }
    factory { GetImagenUseCase(get()) }
    factory { SaveImagenConNotaUseCase(get()) }
    factory { UpdateNotaUseCase(get()) }
    factory { ToggleFavoritaUseCase(get()) }
    factory { DeleteImagenUseCase(get()) }

    // Usuario
    factory { LoginUseCase(get()) }
    factory { RegistroUseCase(get()) }
    factory { GetUsuarioUseCase(get()) }
    factory { LogoutUseCase(get()) }
}

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { RegistroViewModel(get()) }
    viewModel { MainViewModel(get(), get(), get(), get()) }
    viewModel {
        MisMateriasViewModel(
            getMateriasUseCase = get(),
            getHorariosUseCase = get(),
            getImagenesPorMateriaUseCase = get(),
            sessionManager = get()
        )
    }

    viewModel { VerHorarioViewModel(get(), get(), get(), get(), get()) }
    viewModel { CrearHorarioViewModel(get(), get(), get(), get()) }
    viewModel { GaleriaViewModel(get(), get(), get(), get()) }
    viewModel { CamaraViewModel(get()) }
    viewModel { NotaViewModel(get(), get(), get(), get()) }
    viewModel { PerfilViewModel(get(), get(), get(), get()) }
    viewModel { DetalleImagenViewModel(get(), get(), get(), get(), get()) }
}