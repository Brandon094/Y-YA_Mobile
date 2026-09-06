# Guía de Defensa Técnica: Proyecto YÁYA
**Para:** Mauro, Harold y Brandon (BH++ Team)  
**Objetivo:** Argumentación técnica para el Taller Integrador.

---

## 1. El Join Relacional (La "Jugada Maestra")
**Pregunta probable:** *¿Por qué la consulta de solicitudes es tan compleja?*

- **Respuesta:** "Por **eficiencia de red y optimización de latencia**. En lugar de hacer tres peticiones independientes (petición a solicitudes, luego a servicios, luego a perfiles), utilizamos un **Join nativo de Postgrest**."
- **Argumento Senior:** "Esto reduce el 'Overhead' de la red al abrir una sola conexión asíncrona que nos devuelve un objeto JSON estructurado con toda la información relacionada. Menos peticiones = Menos consumo de batería y datos para el usuario."

## 2. Seguridad de Datos (RLS)
**Pregunta probable:** *¿Es seguro tener las llaves de Supabase en el código?*

- **Respuesta:** "Sí, porque la seguridad no reside en la App (lado del cliente), sino en el servidor mediante **RLS (Row Level Security)**."
- **Argumento Senior:** "Implementamos políticas de seguridad a nivel de fila en la base de datos PostgreSQL. Esto garantiza que, aunque un atacante obtenga la API Key, el motor de la base de datos bloqueará cualquier intento de leer o modificar datos que no pertenezcan al ID del usuario autenticado."

## 3. Persistencia de Sesión
**Pregunta probable:** *¿Cómo logran que la sesión se mantenga activa al cerrar la App?*

- **Respuesta:** "Mediante un patrón Singleton y un **SettingsSessionManager**."
- **Argumento Senior:** "Utilizamos la librería *Multiplatform Settings* para persistir el token de sesión (JWT) de Supabase en las **SharedPreferences** de Android. Al arrancar la aplicación, el `SupabaseManager` verifica automáticamente la validez del token antes de cargar la interfaz, eliminando la necesidad de re-autenticación constante."

## 4. Navegación Type-Safe
**Pregunta probable:** *¿Qué ventaja tiene usar objetos @Serializable en las rutas?*

- **Respuesta:** "Garantizar la **integridad de la navegación en tiempo de compilación**."
- **Argumento Senior:** "Al migrar de rutas basadas en Strings a rutas basadas en objetos serializables, eliminamos el riesgo de 'Typos' (errores de escritura) que causan cierres inesperados (crashes). Si una ruta cambia o falta un parámetro, el IDE nos avisa antes de que el código se ejecute."

## 5. Arquitectura de Perfil Universal (No Doble Fricción)
**Pregunta probable:** *¿Cómo manejan que un usuario sea cliente y prestador al mismo tiempo?*

- **Respuesta:** "Mediante una arquitectura de **Rol Dinámico**."
- **Argumento Senior:** "Evitamos la 'Doble Fricción' (tener que crear dos cuentas). Usamos una única tabla de perfiles donde el campo `role` define las capacidades de la interfaz. Esto simplifica la base de datos, evita la redundancia de datos y permite que un prestador contrate servicios de otros colegas con su misma identidad."

## 6. Arquitectura MVVM + UDF
**Pregunta probable:** *¿Por qué eligieron MVVM?*

- **Respuesta:** "Para implementar el **Flujo de Datos Unidireccional (UDF)**."
- **Argumento Senior:** "El ViewModel centraliza la lógica de negocio y expone el estado de forma reactiva a la UI. Esto asegura que la interfaz de usuario sea una representación fiel del estado de los datos en Supabase, facilitando el mantenimiento y permitiendo que varios desarrolladores trabajen en módulos diferentes sin interferencias."

## 7. Seguridad Pre-flight
**Pregunta probable:** *¿Por qué validan duplicados antes de llamar a Auth?*

- **Respuesta:** "Para evitar la creación de **usuarios huérfanos** y mejorar la experiencia de usuario (UX)."
- **Argumento Senior:** "Si enviamos el registro directamente a Supabase Auth y luego falla la inserción en la tabla de perfiles (por cédula duplicada), el usuario queda creado en Auth pero sin perfil. Al validar 'Pre-flight', garantizamos que el registro sea exitoso en ambas capas o no se realice en absoluto."

## 8. Onboarding Inteligente (Spotlight)
**Pregunta probable:** *¿Cómo reducen la curva de aprendizaje del usuario?*

- **Respuesta:** "Mediante un **Motor de Tutoriales Nativo (Spotlight)**."
- **Argumento Senior:** "Implementamos una capa de asistencia visual en Compose que ilumina los componentes clave de la interfaz. Esto guía al usuario paso a paso en sus primeras interacciones, reduciendo el abandono de la app y eliminando la necesidad de leer manuales externos."

---

### 💡 Tips de Oro para la Sustentación:
1.  **Tecnología:** Si preguntan por Kotlin, mencionen que usan la **versión 2.2.10 con el compilador K2** por su velocidad.
2.  **Base de Datos:** Siempre refiéranse a la base de datos como **PostgreSQL** (le da más peso que decir solo Supabase).
3.  **UI:** Si preguntan por el diseño, digan que usan **Material Design 3 (M3)** siguiendo las guías oficiales de Google.

---
*Documento de apoyo técnico - BH++ Team 2026*
