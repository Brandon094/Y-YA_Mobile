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
El portal web sigue el versionamiento unificado del proyecto YÁYA (**v1.1.0 - versionCode 5**), asegurando que la documentación y la cara pública correspondan siempre a la última versión estable del binario móvil.

## 6. Pipeline de Despliegue
El despliegue está automatizado mediante la Firebase CLI:
1.  **Directorio Raíz:** `portal_web/`
2.  **Comando:** `firebase deploy --only hosting`
3.  **Dominio:** [https://y-ya-d5929.web.app](https://y-ya-d5929.web.app)

---
*Documentación Web por BH++ Team - 2026*
