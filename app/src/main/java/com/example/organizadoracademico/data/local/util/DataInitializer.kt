package com.example.organizadoracademico.data.local.util

import com.example.organizadoracademico.data.local.database.AppDatabase
import com.example.organizadoracademico.data.local.entities.MateriaEntity
import com.example.organizadoracademico.data.local.entities.ProfesorEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataInitializer(private val db: AppDatabase) {

    suspend fun populateIfEmpty() = withContext(Dispatchers.IO) {
        // Usamos el conteo general, sin filtrar por usuario
        if (db.materiaDao().getCountGlobal() == 0) {

            val profesores = listOf(
                ProfesorEntity(nombre = "Dr. Carlos"),
                ProfesorEntity(nombre = "Mtro. Alonso M"),
                ProfesorEntity(nombre = "Mtro. Horacio"),
                ProfesorEntity(nombre = "Mtra. Diana"),
                ProfesorEntity(nombre = "Mtro. Renan"),
                ProfesorEntity(nombre = "Mtro. Alonso G"),
                ProfesorEntity(nombre = "Mtro. Viviana"),
                ProfesorEntity(nombre = "Mtro. Ramses"),
                ProfesorEntity(nombre = "Mtro. Alejandro"),
                ProfesorEntity(nombre = "Mtro. Viviana B"),
                ProfesorEntity(nombre = "Mtro. Miguel E"),
                ProfesorEntity(nombre = "Mtro. Sirgei"),
                ProfesorEntity(nombre = "Mtro. Marcelo"),
                ProfesorEntity(nombre = "Mtro. Ali")
            )
            profesores.forEach { db.profesorDao().insert(it) }

            val materias = listOf(
                // 1er Cuatrimestre
                MateriaEntity(nombre = "Inglés I", color = "Gris", icono = "🗣️"),
                MateriaEntity(nombre = "Desarrollo Humano y Valores", color = "Rosa", icono = "🧠"),
                MateriaEntity(nombre = "Fundamentos Matemáticos", color = "Azul", icono = "➕"),
                MateriaEntity(nombre = "Fundamentos de Redes", color = "Naranja", icono = "🌐"),
                MateriaEntity(nombre = "Física", color = "Cian", icono = "🧪"),
                MateriaEntity(nombre = "Fundamentos de Programación", color = "Verde", icono = "⌨️"),
                MateriaEntity(nombre = "Comunicación y Habilidades Digitales", color = "Amarillo", icono = "📝"),

                // 2do Cuatrimestre
                MateriaEntity(nombre = "Inglés II", color = "Gris", icono = "🗣️"),
                MateriaEntity(nombre = "Habilidades Socioemocionales y Manejo de Conflictos", color = "Rosa", icono = "🤝"),
                MateriaEntity(nombre = "Cálculo Diferencial", color = "Azul", icono = "📐"),
                MateriaEntity(nombre = "Conmutación y Enrutamiento de Redes", color = "Naranja", icono = "🔌"),
                MateriaEntity(nombre = "Probabilidad y Estadística", color = "Azul", icono = "📊"),
                MateriaEntity(nombre = "Programación Estructurada", color = "Verde", icono = "🏗️"),
                MateriaEntity(nombre = "Sistemas Operativos", color = "Rojo", icono = "🖥️"),

                // 3er Cuatrimestre
                MateriaEntity(nombre = "Inglés III", color = "Gris", icono = "🗣️"),
                MateriaEntity(nombre = "Desarrollo del Pensamiento y Toma de Decisiones", color = "Rosa", icono = "💡"),
                MateriaEntity(nombre = "Cálculo Integral", color = "Azul", icono = "♾️"),
                MateriaEntity(nombre = "Tópicos de Calidad para el Diseño de Software", color = "Morado", icono = "💎"),
                MateriaEntity(nombre = "Bases de Datos", color = "Azul Marino", icono = "🗄️"),
                MateriaEntity(nombre = "Programación Orientada a Objetos", color = "Verde", icono = "📦"),
                MateriaEntity(nombre = "Proyecto Integrador I", color = "Dorado", icono = "🚀"),

                // 4to Cuatrimestre
                MateriaEntity(nombre = "Inglés IV", color = "Gris", icono = "🗣️"),
                MateriaEntity(nombre = "Ética Profesional", color = "Blanco", icono = "⚖️"),
                MateriaEntity(nombre = "Cálculo de Varias Variables", color = "Azul", icono = "📈"),
                MateriaEntity(nombre = "Aplicaciones Web", color = "Naranja", icono = "🌐"),
                MateriaEntity(nombre = "Estructura de Datos", color = "Verde", icono = "🌲"),
                MateriaEntity(nombre = "Desarrollo de Aplicaciones Móviles", color = "Morado", icono = "📱"),
                MateriaEntity(nombre = "Análisis y Diseño de Software", color = "Rosa", icono = "🖍️"),

                // 5to Cuatrimestre
                MateriaEntity(nombre = "Inglés V", color = "Gris", icono = "🗣️"),
                MateriaEntity(nombre = "Liderazgo de Equipos de Alto Desempeño", color = "Rosa", icono = "🏆"),
                MateriaEntity(nombre = "Ecuaciones Diferenciales", color = "Azul", icono = "🖍️"),
                MateriaEntity(nombre = "Aplicaciones Web Orientadas a Servicios", color = "Naranja", icono = "☁️"),
                MateriaEntity(nombre = "Bases de Datos Avanzadas", color = "Azul Marino", icono = "🗃️"),
                MateriaEntity(nombre = "Estándares y Métricas para el Desarrollo de Software", color = "Morado", icono = "📏"),
                MateriaEntity(nombre = "Proyecto Integrador II", color = "Dorado", icono = "⚙️"),

                // 7mo Cuatrimestre (Nivel Ingeniería)
                MateriaEntity(nombre = "Inglés VI", color = "Gris", icono = "🗣️"),
                MateriaEntity(nombre = "Habilidades Gerenciales", color = "Rosa", icono = "👔"),
                MateriaEntity(nombre = "Fundamentos de Inteligencia Artificial", color = "Cian", icono = "🤖"),
                MateriaEntity(nombre = "Ética y Legislación en TI", color = "Blanco", icono = "📜"),
                MateriaEntity(nombre = "Programación para Inteligencia Artificial", color = "Verde", icono = "🧠"),
                MateriaEntity(nombre = "Administración de Servidores", color = "Rojo", icono = "🗄️"),
                MateriaEntity(nombre = "Formulación de Proyectos de Tecnología", color = "Dorado", icono = "📝"),

                // 8vo Cuatrimestre
                MateriaEntity(nombre = "Inglés VII", color = "Gris", icono = "🗣️"),
                MateriaEntity(nombre = "Electrónica Digital", color = "Cian", icono = "📟"),
                MateriaEntity(nombre = "Optativa I", color = "Verde Lima", icono = "⚙️"),
                MateriaEntity(nombre = "Seguridad Informática", color = "Rojo", icono = "🛡️"),
                MateriaEntity(nombre = "Evaluación de Proyectos de Tecnología", color = "Dorado", icono = "📊"),

                // 9no Cuatrimestre
                MateriaEntity(nombre = "Inglés VIII", color = "Gris", icono = "🗣️"),
                MateriaEntity(nombre = "Gestión de Proyectos de Tecnología", color = "Dorado", icono = "📂"),
                MateriaEntity(nombre = "Optativa II", color = "Verde Lima", icono = "⚙️"),
                MateriaEntity(nombre = "Internet de las Cosas", color = "Cian", icono = "📡"),
                MateriaEntity(nombre = "Ciencia de Datos", color = "Azul", icono = "📉"),
                MateriaEntity(nombre = "Informática Forense", color = "Rojo", icono = "🔍"),
                MateriaEntity(nombre = "Proyecto Integrador III", color = "Dorado", icono = "🛠️")
            ).mapIndexed { index, materia ->
                // Alinea los IDs locales con el catálogo remoto estable (1..N)
                materia.copy(id = index + 1)
            }
            materias.forEach { db.materiaDao().insert(it) }
        }
    }
}