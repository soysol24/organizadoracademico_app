# API Endpoints - Organizador Académico

Base URL: `/api`

Autenticación: la mayoría de rutas privadas requieren encabezado:
- `Authorization: Bearer <JWT_TOKEN>`

---

## Auth

### POST /auth/register
- Auth: No
- Content-Type: `application/json`
- Request body:
```json
{
  "nombre": "Ricardo",
  "email": "ricardo@example.com",
  "password": "secret",
  "fotoPerfil": "https://..." // opcional
}
```
- Response 201 JSON:
```json
{
  "user": {
    "id": 1,
    "nombre": "Ricardo",
    "email": "ricardo@example.com",
    "password": "<hashed_password>",
    "fotoPerfil": "https://..."
  },
  "token": "<jwt_token>"
}
```
- Error 400:
```json
{ "error": "Email already in use" }
```

### POST /auth/login
- Auth: No
- Content-Type: `application/json`
- Request body:
```json
{
  "email": "ricardo@example.com",
  "password": "secret"
}
```
- Response 200 JSON:
```json
{
  "user": { /* usuario, similar a register */ },
  "token": "<jwt_token>"
}
```
- Error 400:
```json
{ "error": "Invalid credentials" }
```

---

## Materias

### GET /materias
- Auth: No
- Response 200 JSON (array):
```json
[
  { "id": 1, "nombre": "Matemáticas", "color": "#ff0000", "icono": "" },
  { "id": 2, "nombre": "Física", "color": "#00ff00", "icono": "" }
]
```

### GET /materias/:id
- Auth: No
- Response 200 JSON:
```json
{ "id": 1, "nombre": "Matemáticas", "color": "#ff0000", "icono": "" }
```
- Error 404:
```json
{ "error": "Not found" }
```

---

## Profesores

### GET /profesores
- Auth: No
- Response 200 JSON (array):
```json
[
  { "id": 1, "nombre": "Juan Pérez" },
  { "id": 2, "nombre": "María López" }
]
```

### GET /profesores/:id
- Auth: No
- Response 200 JSON:
```json
{ "id": 1, "nombre": "Juan Pérez" }
```
- Error 404:
```json
{ "error": "Not found" }
```

---

## Horarios (privado)
Todas las rutas bajo `/horarios` requieren `Authorization: Bearer <token>` y se operan sobre el usuario autenticado.

### POST /horarios
- Auth: Sí
- Content-Type: `application/json`
- Request body:
```json
{
  "materiaId": 1,
  "profesorId": 2,
  "dia": "Lunes",
  "horaInicio": "08:00",
  "horaFin": "09:30",
  "color": "#ff8800"
}
```
- Response 201 JSON (objeto creado):
```json
{
  "id": 10,
  "usuarioId": 3,
  "materiaId": 1,
  "profesorId": 2,
  "dia": "Lunes",
  "horaInicio": "08:00",
  "horaFin": "09:30",
  "color": "#ff8800"
}
```
- Error 400: valida campos / relaciones

### GET /horarios
- Auth: Sí
- Query: none
- Response 200 JSON (array de horarios del usuario):
```json
[
  { "id": 10, "usuarioId": 3, "materiaId": 1, "profesorId": 2, "dia": "Lunes", "horaInicio": "08:00", "horaFin": "09:30", "color": "#ff8800" }
]
```

### PUT /horarios/:id
- Auth: Sí
- Content-Type: `application/json`
- Request body: (cualquier campo a actualizar)
```json
{ "horaFin": "10:00", "color": "#112233" }
```
- Response 200 JSON: objeto actualizado
- Error 404 si no existe o no pertenece al usuario

### DELETE /horarios/:id
- Auth: Sí
- Response 200 JSON:
```json
{ "deleted": true }
```
- Error 404 si no existe

---

## Imágenes (apuntes) (privado)
Rutas bajo `/imagenes` requieren `Authorization: Bearer <token>`.

### POST /imagenes
- Auth: Sí
- Content-Type: `multipart/form-data`
- Form fields:
  - `file`: archivo de imagen (campo obligatorio)
  - `materiaId`: id numérico
  - `nota`: texto (opcional)
- Example using `curl`:
```bash
curl -X POST "http://host/api/imagenes" \
  -H "Authorization: Bearer <token>" \
  -F "file=@/path/to/img.jpg" \
  -F "materiaId=1" \
  -F "nota=Apunte de prueba"
```
- Response 201 JSON (objeto Imagen):
```json
{
  "id": 5,
  "materiaId": 1,
  "usuarioId": 3,
  "uri": "https://.../apuntes/...jpg",
  "nota": "Apunte de prueba",
  "fecha": 1610000000000,
  "favorita": false
}
```
- Errores comunes 400:
  - `{ "error": "file required" }`
  - `{ "error": "invalid file type" }`
  - `{ "error": "Materia not found" }`

### GET /imagenes
- Auth: Sí
- Query params (opcionales): `materiaId` (filtrar por materia)
- Response 200 JSON (array):
```json
[
  { "id": 5, "materiaId": 1, "usuarioId": 3, "uri": "https://...", "nota": "...", "fecha": 1610000000000, "favorita": false }
]
```

### DELETE /imagenes/:id
- Auth: Sí
- Response 200 JSON:
```json
{ "deleted": true }
```
- Error 404 si no existe

---

## Notas generales
- Todas las respuestas de error siguen el formato `{ "error": "message" }`.
- Los endpoints privados usan el `id` del usuario obtenido del token (`req.user.id`).
- Los objetos devueltos corresponden a los modelos Sequelize: `Usuario`, `Materia`, `Profesor`, `Horario`, `Imagen`.
- Observa que actualmente la respuesta de `register`/`login` incluye el objeto `user` tal cual viene de la base de datos (incluye `password` hasheado). Si prefieres ocultar el campo `password`, puedo actualizar los controladores para sanitizar la respuesta.

---

Si quieres, puedo:
- Añadir ejemplos de requests completos con `curl` para cada endpoint.
- Modificar los controladores para omitir el campo `password` en las respuestas de `user`.
- Generar un archivo OpenAPI/Swagger a partir de esta documentación.
