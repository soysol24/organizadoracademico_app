# Documentación del Proyecto: Organizador Académico

Este proyecto es una aplicación Android diseñada para ayudar a los estudiantes a gestionar su vida académica, incluyendo materias, horarios, profesores y tareas.

## 🛠 Arquitectura
El proyecto sigue los principios de **Clean Architecture** y el patrón de diseño **MVVM (Model-View-ViewModel)**, facilitando la escalabilidad y las pruebas.

---

## 📂 Estructura del Proyecto (Capa por Capa)

### 1. `data/` (Capa de Datos)
Encargada de la persistencia y recuperación de información.
*   **`local/`**: Implementación de la base de datos local usando **Room**.
    *   **`dao/`**: Interfaces de acceso a datos (`UsuarioDao`, `MateriaDao`, `HorarioDao`, `ProfesorDao`, `ImagenDao`).
    *   **`database/`**: Configuración de `AppDatabase` y convertidores de tipos.
    *   **`entities/`**: Tablas de la base de datos que representan a los usuarios, sus materias y horarios.
*   **`repository/`**: Implementaciones concretas de los repositorios definidos en la capa de dominio.

### 2. `domain/` (Capa de Dominio)
Contiene la lógica de negocio pura, independiente de frameworks externos.
*   **`model/`**: Clases de datos (POJOs) que representan las entidades del negocio.
*   **`repository/`**: Interfaces que definen cómo se deben manipular los datos.
*   **`usercase/`**: Clases que ejecutan acciones específicas (ej. `GetMateriasUseCase`, `LoginUseCase`).

### 3. `presentation/` (Capa de Interfaz de Usuario)
Construida totalmente con **Jetpack Compose**.
*   **`login/` / `registro/`**: Flujo de autenticación de usuarios.
*   **`materia/`**: Gestión de asignaturas (Lista, detalles, añadir).
*   **`horario/`**: Visualización y gestión de las horas de clase.
*   **`perfil/`**: Información del usuario y configuración.
*   **`navigation/`**: Centraliza el `NavGraph` y las `Routes` para la navegación entre pantallas.
*   **`theme/`**: Definición de colores, tipografías y estilos visuales de la app.

### 4. `di/` (Inyección de Dependencias)
*   Configuración de módulos para proveer instancias de la base de datos, repositorios y ViewModels de manera automática a toda la aplicación.

### 5. `hardware/`
*   Componentes para interactuar con hardware específico del dispositivo, como la cámara para fotos de tareas o sensores.

---

## 📄 Archivos Clave en la Raíz
*   **`MainActivity.kt`**: Punto de entrada de la aplicación; configura el contenedor de Compose y el grafo de navegación.
*   **`OrganizadorApplication.kt`**: Clase de aplicación donde se inicializan librerías globales (como el framework de DI).

---

## 🚀 Funcionalidades Principales
1.  **Gestión de Usuarios:** Registro e inicio de sesión local.
2.  **Control de Materias:** Registro de nombres, créditos y profesores asociados.
3.  **Horarios:** Organización semanal de clases.
4.  **Imágenes:** Posibilidad de asociar imágenes a materias o tareas.
