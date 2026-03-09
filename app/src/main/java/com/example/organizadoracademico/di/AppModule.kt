package com.example.organizadoracademico.di

import com.example.organizadoracademico.data.local.database.AppDatabase
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
    // 1. Provee la instancia de Firebase Firestore (fundamental)
    single { com.google.firebase.firestore.FirebaseFirestore.getInstance() }

    // 2. Base de datos y Sesión
    single { AppDatabase.getInstance(get()) }
    single { com.example.organizadoracademico.data.local.util.SessionManager(get()) }

    // 3. Servicios Remotos (Ahora con get() para inyectar Firestore)
    single { com.example.organizadoracademico.data.remote.MateriaFirestoreService(get()) }
    single { com.example.organizadoracademico.data.remote.ProfesorFirestoreService(get()) }
    single { com.example.organizadoracademico.data.remote.HorarioFirestoreService(get()) }
    single { com.example.organizadoracademico.data.remote.ImagenFirestoreService(get()) }
    single { com.example.organizadoracademico.data.remote.UsuarioFirestoreService(get()) }

    // ... resto de tus DAOs y Repositorios
    single { get<AppDatabase>().materiaDao() }
    single { get<AppDatabase>().profesorDao() }
    single { get<AppDatabase>().horarioDao() }
    single { get<AppDatabase>().imagenDao() }
    single { get<AppDatabase>().usuarioDao() }

    // REPOSITORIOS
    single<IMateriaRepository> { MateriaRepositoryImpl(get(), get(), get()) }
    single<IProfesorRepository> { ProfesorRepositoryImpl(get(), get(), get()) }
    single<IHorarioRepository> { HorarioRepositoryImpl(get(), get()) }
    single<IImagenRepository> { ImagenRepositoryImpl(get(), get()) }
    single<IUsuarioRepository> { UsuarioRepositoryImpl(get(), get()) }
}

val useCaseModule = module {
    // ... (Tus UseCases están perfectos como los pusiste)
    factory { GetHorariosUseCase(get()) }
    factory { AddHorarioUseCase(get()) }
    factory { DeleteHorarioUseCase(get()) }
    factory { GetMateriasUseCase(get()) }
    factory { AddMateriaUseCase(get()) }
    factory { GetProfesoresUseCase(get()) }
    factory { GetImagenesPorMateriaUseCase(get()) }
    factory { GetImagenUseCase(get()) }
    factory { SaveImagenConNotaUseCase(get()) }
    factory { UpdateNotaUseCase(get()) }
    factory { DeleteImagenUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { RegistroUseCase(get()) }
    factory { GetUsuarioUseCase(get()) }
    factory { LogoutUseCase() }
}

val viewModelModule = module {
    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegistroViewModel(get()) }
    viewModel { MainViewModel(get()) }

    viewModel {
        MisMateriasViewModel(
            getMateriasUseCase = get(),
            getImagenesPorMateriaUseCase = get(),
            sessionManager = get()
        )
    }

    // VerHorarioViewModel ahora pide 5 cosas:
    // 4 UseCases + SessionManager
    viewModel {
        VerHorarioViewModel(get(), get(), get(), get(), get())
    }

    // Asegúrate de que CrearHorarioViewModel también tenga los get() necesarios
    viewModel { CrearHorarioViewModel(get(), get(), get(), get()) }

    viewModel { GaleriaViewModel(get(), get(), get(), get())}
    viewModel { CamaraViewModel(get()) }
    viewModel { NotaViewModel(get(), get(), get()) }

    // PerfilViewModel suele necesitar el SessionManager para mostrar los datos del usuario actual
    viewModel { PerfilViewModel(get(), get(), get(), get(), get(), get(), get()) }

    viewModel { DetalleImagenViewModel(get(), get(), get(), get(), get()) }
}