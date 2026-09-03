# Arquitectura del Portal Web - YÁYA

El Portal Web de YÁYA es una infraestructura estática de alto rendimiento diseñada bajo principios de **Atomic Design** y **DRY (Don't Repeat Yourself)**. A diferencia de la aplicación móvil, su propósito es puramente informativo, legal y comercial (Embudo de Conversión).

## 1. Stack Tecnológico
*   **Lenguaje:** HTML5 / JavaScript (ES6+).
*   **Estilos:** Tailwind CSS (via CDN para máxima velocidad de despliegue).
*   **Arquitectura de Componentes:** Motor JS propio (`components.js`) para inyección de elementos atómicos con soporte responsive y accesibilidad Senior.
*   **Hosting:** Firebase Hosting (Google Cloud Infrastructure).

## 2. Metodología de Diseño: Web Atomic
Para mantener la consistencia con la App, se aplica una jerarquía de componentes en el archivo `js/components.js`:
*   **Átomos:** Definición de paleta de colores (`yayaRed`, `yayaNavy`), botones Pro y Badges institucionales.
*   **Moléculas:** Tarjetas de beneficios, pasos del ciclo de vida y elementos de navegación.
*   **Organismos:** Navbar responsivo con soporte de Modo Oscuro y Footer unificado con enlaces legales.

## 3. Funcionalidades Core
*   **Tema Dual:** Soporte nativo para Modo Claro y Modo Oscuro con persistencia en `localStorage`.
*   **Responsive UI:** Menú de hamburguesa y layouts elásticos que se adaptan a cualquier resolución móvil.
*   **Legal Rendering:** Motor de visualización jerárquico para documentos normativos (Markdown-lite).
*   **Manuales de Uso Interconectados y Segregados por Rol (`portal_web/manuales.html`):**
    *   **Selector Interactivo de Roles (`switchRoleTab`):** Controlador en JavaScript para conmutación fluida entre las guías operativas de *Rol Cliente*, *Rol Prestador* y *Rol Administrador*.
    *   **Matriz Atómica de Permisos:** Componentes con bloques destacados estilizados con Tailwind CSS que desglosan minuciosamente facultades ("LO QUE PUEDE HACER EL USUARIO" en contenedor verde `emerald`) y prohibiciones/restricciones ("LO QUE NO PUEDE HACER EL USUARIO" en contenedor rojo `rose`).
    *   **Sincronización con Manuales v1.1.0 de la App:** Alineación total con el motor interno de la aplicación (`ManualConstants.kt`), reflejando el filtrado por municipio, la jornada maestra, la prevención de traslapes horarios, la reputación de talentos y el semáforo disciplinario de moderación.
    *   **Estándar Técnico Sin Emojis:** Redacción formal acorde al estándar legal Markdown sin emojis, con soporte de Tema Dual e Interfaz Elástica Responsive.

## 4. Optimización y Accesibilidad
*   **Web Performance:** Reducción de CLS (Cumulative Layout Shift) mediante dimensiones explícitas y reserva de espacio (min-height). Optimización de LCP mediante pre-carga de recursos críticos.
*   **Inclusión:** Cumplimiento de estándares de accesibilidad con semántica HTML5, etiquetas ARIA para navegación móvil y contraste de color optimizado para legibilidad.

## 5. Versionamiento Semántico
El portal web sigue el versionamiento unificado del proyecto YÁYA (**v1.1.0 - versionCode 5**), asegurando que la documentación y la cara pública correspondan siempre a la última versión estable del binario móvil. El despliegue de la versión v1.1.0 consolida el Portal Web y el Portal de Manuales en producción.

## 6. Pipeline de Despliegue y Configuración de Hosting
El despliegue está automatizado mediante la Firebase CLI sobre la infraestructura de Firebase Hosting (Google Cloud Infrastructure).

### Configuración de Firebase (`firebase.json`)
Para garantizar el soporte correcto de una arquitectura **multi-página estática** sin interferencias de enrutamiento Single Page Application (SPA), la configuración de `firebase.json` omite las reglas de rewrites de SPA y habilita la resolución de URLs limpias:
*   **Directorio Público (`public`):** `"portal_web"`
*   **URLs Limpias (`cleanUrls`):** `true` (permite servir `/manuales` directamente a partir de `portal_web/manuales.html` omitiendo la extensión `.html` en las URLs de producción).
*   **Barras Finales (`trailingSlash`):** `false`
*   **Archivos Ignorados (`ignore`):** `["firebase.json", "**/.*", "**/node_modules/**"]`

### Confirmación de Despliegue en Producción
*   **Comando de Despliegue:** `firebase deploy --only hosting`
*   **Volumen de Publicación:** 13 archivos estáticos desplegados exitosamente.
*   **Dominio Principal:** [https://y-ya-d5929.web.app](https://y-ya-d5929.web.app)
*   **Portal Web de Manuales:** [https://y-ya-d5929.web.app/manuales](https://y-ya-d5929.web.app/manuales)

---
*Documentación Web por BH++ Team - 2026*
