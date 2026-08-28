# Arquitectura del Portal Web - YÁYA

El Portal Web de YÁYA es una infraestructura estática de alto rendimiento diseñada bajo principios de **Atomic Design** y **DRY (Don't Repeat Yourself)**. A diferencia de la aplicación móvil, su propósito es puramente informativo, legal y comercial (Embudo de Conversión).

## 1. Stack Tecnológico
*   **Lenguaje:** HTML5 / JavaScript (ES6+).
*   **Estilos:** Tailwind CSS (via CDN para máxima velocidad de despliegue).
*   **Arquitectura de Componentes:** Motor JS propio (`components.js`) para inyección de elementos atómicos.
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

## 4. Pipeline de Despliegue
El despliegue está automatizado mediante la Firebase CLI:
1.  **Directorio Raíz:** `portal_web/`
2.  **Comando:** `firebase deploy --only hosting`
3.  **Dominio:** [https://y-ya-d5929.web.app](https://y-ya-d5929.web.app)

---
*Documentación Web por BH++ Team - 2026*
