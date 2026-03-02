package com.example.organizadoracademico.di

import com.example.organizadoracademico.data.repository.*
import com.example.organizadoracademico.domain.repository.*
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
    single<IMateriaRepository> { MateriaRepositoryImpl(get(), get()) }
    single<IProfesorRepository> { ProfesorRepositoryImpl(get(), get()) }
    single<IHorarioRepository> { HorarioRepositoryImpl(get(), get()) }
    single<IImagenRepository> { ImagenRepositoryImpl(get(), get()) }
    single<IUsuarioRepository> { UsuarioRepositoryImpl(get(), get()) }
}

val useCaseModule = module {
    // Horario
    factory { GetHorariosUseCase(get<IHorarioRepository>()) }
    factory { AddHorarioUseCase(get<IHorarioRepository>()) }
    factory { DeleteHorarioUseCase(get<IHorarioRepository>()) }

    // Materia
    factory { GetMateriasUseCase(get<IMateriaRepository>()) }
    factory { AddMateriaUseCase(get<IMateriaRepository>()) }

    // Profesor
    factory { GetProfesoresUseCase(get<IProfesorRepository>()) }

    // Imagen
    factory { GetImagenesPorMateriaUseCase(get<IImagenRepository>()) }
    factory { GetImagenUseCase(get<IImagenRepository>()) }
    factory { SaveImagenConNotaUseCase(get<IImagenRepository>()) }
    factory { UpdateNotaUseCase(get<IImagenRepository>()) }
    factory { DeleteImagenUseCase(get<IImagenRepository>()) }

    // Usuario
    factory { LoginUseCase(get<IUsuarioRepository>()) }
    factory { RegistroUseCase(get<IUsuarioRepository>()) }
    factory { GetUsuarioUseCase(get<IUsuarioRepository>()) }
    factory { LogoutUseCase() }
}

val viewModelModule = module {
    viewModel { LoginViewModel(loginUseCase = get()) }
    viewModel { RegistroViewModel(get()) }
    viewModel { MainViewModel(get()) }

    viewModel {
        CrearHorarioViewModel(
            getMateriasUseCase = get(),
            getProfesoresUseCase = get(),
            addHorarioUseCase = get()
        )
    }

    viewModel {
        VerHorarioViewModel(
            getHorariosUseCase = get(),
            getMateriasUseCase = get(),
            getProfesoresUseCase = get(),
            deleteHorarioUseCase = get()
        )
    }

    viewModel {
        MisMateriasViewModel(
            getMateriasUseCase = get(),
            getImagenesPorMateriaUseCase = get()
        )
    }

    viewModel {
        GaleriaViewModel(
            getImagenesPorMateriaUseCase = get(),
            getMateriasUseCase = get(),
            deleteImagenUseCase = get()
        )
    }

    viewModel {
        CamaraViewModel(
            imageSaver = get()
        )
    }

    viewModel {
        NotaViewModel(
            saveImagenConNotaUseCase = get(),
            imageSaver = get(),
            vibratorManager = get()
        )
    }

    viewModel {
        PerfilViewModel(
            getUsuarioUseCase = get(),
            logoutUseCase = get(),
            getMateriasUseCase = get(),
            getHorariosUseCase = get(),
            getImagenesPorMateriaUseCase = get(),
            vibratorManager = get()
        )
    }

    viewModel {
        DetalleImagenViewModel(
            getImagenUseCase = get(),
            updateNotaUseCase = get(),
            deleteImagenUseCase = get(),
            getMateriasUseCase = get(),
            vibratorManager = get()
        )
    }
}