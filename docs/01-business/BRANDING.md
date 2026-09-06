# Identidad de Marca - YÁYA

Este documento define los elementos visuales, verbales y estratégicos que conforman la identidad de **YÁYA**. Es la guía de referencia para diseñadores y desarrolladores para asegurar la consistencia de la marca en toda la plataforma.

## 1. Concepto de Marca
**YÁYA** es más que una aplicación; es un puente de confianza. El nombre evoca cercanía y rapidez, mientras que el acento en la primera "A" le otorga una personalidad distintiva y moderna.

- **Eslogan:** "Conecta. Confía. Contrata."
- **Misión:** Empoderar a talentos independientes y facilitar la vida de los usuarios mediante una conexión segura y eficiente.

## 2. Paleta de Colores
La paleta combina la energía del rojo con la estabilidad del azul marino, creando un equilibrio entre pasión por el servicio y profesionalismo técnico.

### Colores Principales
| Color | Hexadecimal | Uso Principal |
| :--- | :--- | :--- |
| **Rojo Primario** | `#E85C5C` | Color de marca, botones principales (CTA), identidad visual. |
| **Rojo Oscuro** | `#D94A4A` | Variación para estados activos, bordes y degradados. |
| **Azul Navy** | `#1E2A38` | Barras de navegación (TopAppBar) y elementos de contraste profesional. |
| **Rosa Salmón** | `#F26B6B` | (Proyectado) Energía y conexión humana para acciones secundarias. |

### Paleta Funcional (UI & Estados)
Utilizada para dar feedback claro al usuario sobre el estado de sus procesos:

| Estado | Fondo | Texto/Icono | Significado |
| :--- | :--- | :--- | :--- |
| **Pendiente** | `#FFF4E5` | `#FFFF9800` (Naranja) | Solicitudes en espera de revisión. |
| **Aceptada** | `#E8F5E9` | `#4CAF50` (Verde) | Servicios confirmados o activos. |
| **Completada** | `#E3F2FD` | `#2196F3` (Azul) | Servicios finalizados exitosamente. |
| **Cancelada** | `#FFFFEBEE` | `#F44336` (Rojo) | Servicios anulados o errores. |
| **Calificación**| - | `#FFFFB800` (Oro) | Estrellas y reputación del prestador. |

### Paleta de Superficie
- **Fondo Claro:** `#F8F9FA` (Gris muy claro para descanso visual).
- **Fondo Oscuro:** `#0B0E11` ("Deep Midnight" - El modo oscuro oficial unificado para máxima inmersión).
- **Superficie Oscura:** `#161B22` (Tarjetas y elementos elevados en modo oscuro Deep Midnight).
- **Texto Primario:** `#1A1A1A` (Gris casi negro para máxima legibilidad).

## 3. Elementos Gráficos
Contamos con tres iconos y logotipos oficiales que deben utilizarse según el contexto para mantener la coherencia de la marca:

- **Logo Full (logo_yaya_full):** Incluye el personaje y la tipografía completa. Su uso es exclusivo para el **Splash Screen**, proporcionando una bienvenida visual impactante.
- **Logo Tipográfico (logo_yaya_typographic):** Solo texto con los colores institucionales. Se utiliza prioritariamente en las **TopAppBar** y cabeceras de pantallas para reforzar la presencia de marca sin sobrecargar la interfaz.
- **Isotipo (ic_logo):** El entrelazado minimalista coral. Se utiliza para:
    - Icono oficial de la aplicación (Launcher icon).
    - Marcador de posición (Placeholder) para **avatares** de usuario.
    - Elementos de carga y componentes minimalistas.

### Restricciones de Uso
- **Integridad:** No se permite rotar, inclinar ni deformar el logotipo o isotipo bajo ninguna circunstancia para asegurar su legibilidad y reconocimiento.

## 4. Tono de Voz
- **Cercano:** Hablamos de tú a tú al usuario, pero manteniendo el respeto.
- **Transparente:** Sin letras pequeñas. La información de precios y condiciones es clara.
- **Eficiente:** Mensajes directos y orientados a la acción.

## 5. Tipografía
- **Jetpack Compose Default:** Utilizamos el sistema de tipografía estándar de Material 3, optimizado para legibilidad en dispositivos móviles, con variaciones de peso (Bold para títulos, Medium para botones).

---
*Manual de Identidad Visual - Año 2026*  
*Diseño por BH++ Team*
