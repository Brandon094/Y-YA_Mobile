# YÁYA - Conecta. Confía. Contrata.

**YÁYA** es una plataforma móvil moderna diseñada para revolucionar la forma en que se encuentran y contratan servicios locales. Nuestra misión es conectar talentos independientes con usuarios que buscan soluciones rápidas, seguras y de calidad.

---

## 🚀 Características Principales

- **Catálogo de Servicios:** Exploración intuitiva de diversas categorías (Limpieza, Cuidado de mascotas, Cocina, etc.).
- **Perfiles Detallados:** Información clara de los prestadores de servicios para generar confianza.
- **Flujo de Contratación Ágil:** Proceso simplificado desde la búsqueda hasta la confirmación de la reserva.
- **Gestión de Perfil:** Control total sobre los datos personales y roles (Cliente o Prestador).
- **Autenticación Segura:** Sistema robusto de inicio de sesión y recuperación de contraseña.

---

## 💰 Modelo de Negocio

**YÁYA** opera bajo un modelo de **Suscripción (SaaS)**:
- **Planes para Prestadores:** Suscripciones mensuales/anuales para destacar servicios y acceder a herramientas premium de gestión.
- **Acceso para Clientes:** Registro gratuito con opciones de suscripción para beneficios exclusivos y soporte prioritario.

---

## 🛠️ Stack Tecnológico

El proyecto está construido bajo los estándares más modernos de desarrollo en Android:

- **Lenguaje:** [Kotlin](https://kotlinlang.org/) (Coroutines & Flow)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Arquitectura declarativa)
- **Diseño:** [Material 3](https://m3.material.io/) (Material Design actual)
- **Backend as a Service (BaaS):** [Supabase](https://supabase.com/) (Auth, Database & Realtime)
- **Navegación:** [Jetpack Navigation Component](https://developer.android.com/guide/navigation) (Type-Safe Navigation)
- **Arquitectura:** MVVM (Model-View-ViewModel) para una separación clara de responsabilidades.
- **Redes:** [Ktor Client](https://ktor.io/) para comunicación con APIs.
- **Serialización:** Kotlinx Serialization para manejo de datos seguro.

---

## 📂 Estructura del Proyecto

El código está organizado siguiendo las mejores prácticas de modularización por capas:

```text
com.bhplusplus.yaya
├── data                # Repositorios y modelos de datos
│   └── models          # Clases de datos (Service, UserProfile)
├── navigation          # Configuración de rutas y NavHost con Type-Safety
├── ui
│   ├── screens         # Pantallas principales (Home, Login, Register, etc.)
│   └── theme           # Definición de colores, tipografía y estilo visual
└── MainActivity.kt     # Punto de entrada de la aplicación
```

---

## ⚙️ Instalación y Configuración

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/Brandon094/Y-YA_Mobile.git
   ```
2. **Abrir en Android Studio:**
   Asegúrate de tener instalada la versión **Ladybug** (o superior) para compatibilidad total con el plugin de Compose.
3. **Configurar Supabase:**
   Crea un proyecto en Supabase y añade tus credenciales en `SupabaseManager.kt` (o mediante variables de entorno en el futuro).
4. **Build & Run:**
   Sincroniza con Gradle y lanza la app en un emulador o dispositivo físico.

---

## 🤝 Contribuciones

Este proyecto es desarrollado por la marca **BH++**. Si deseas contribuir:
1. Crea un *Fork* del proyecto.
2. Crea tu rama de funcionalidad (`git checkout -b feature/NuevaFuncionalidad`).
3. Realiza un *Commit* de tus cambios (`git commit -m 'Add: Nueva Funcionalidad'`).
4. Sube los cambios (`git push origin feature/NuevaFuncionalidad`).
5. Abre un *Pull Request*.

---

## 📌 Estándares de Commits

Para mantener un historial limpio y organizado, seguimos la convención de **Conventional Commits**. Cada mensaje debe empezar con un prefijo que indique el tipo de cambio:

- **`feat:`** (Add) Nueva funcionalidad para el usuario.
- **`fix:`** (Fix) Corrección de un error o bug.
- **`refactor:`** Cambio en el código que no corrige errores ni añade funciones (ej. renombrar variables, reorganizar carpetas).
- **`docs:`** Cambios solo en la documentación (README, comentarios).
- **`style:`** Cambios que no afectan el significado del código (espaciado, formato, comas faltantes).
- **`chore:`** Tareas de mantenimiento, actualización de dependencias o configuración de herramientas.

**Ejemplo:** `feat: implementar validación de email en registro`

---

## 📝 Licencia

Este proyecto es de **Propiedad Exclusiva de BH++**. Todos los derechos están reservados. Queda prohibida la copia, distribución o modificación no autorizada. Consulta el archivo `LICENSE` para más detalles.

---
*Desarrollado con ❤️ por **BH++***
