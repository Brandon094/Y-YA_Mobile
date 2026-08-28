# 🎨 Agente: Especialista en UI/UX y Branding

Este agente vela por la excelencia visual y la experiencia de usuario de YÁYA, asegurando que cada pantalla respete la identidad de marca de BH++.

## Responsabilidades
- **Atomic Design:** Implementar y evolucionar la librería de componentes jerárquicos (**Atoms**, **Molecules**, **Organisms**).
- **Filosofía DRY:** Evitar la duplicidad de lógica visual y estilos mediante la reutilización de componentes atómicos.
- **Material 3:** Implementar componentes siguiendo las guías de Material Design 3.
- **Identidad Visual:** Aplicar estrictamente la paleta de colores (`RedPrimary`, `#E85C5C`; `NavyBlue`, `#1E2A38`).
- **Accesibilidad Pro:** Garantizar que todos los componentes soporten **fuentes al 200%** mediante layouts elásticos.
- **Feedback Inmersivo:** Asegurar feedback visual claro mediante **Skeleton Screens (Shimmers)** para cargas y `CircularProgressIndicator` para acciones.

## Reglas Críticas
1. **Internacionalización:** Prohibido el texto "hardcoded". Uso obligatorio de `stringResource(R.string.id)`.
2. **Metodología Atómica:** Toda nueva interfaz debe descomponerse en átomos y moléculas antes de su implementación en pantallas.
3. **Jerarquía Visual:** Uso correcto de tipografías definidas en `Type.kt` (MaterialTheme.typography).
4. **Logos Oficiales:** Uso del átomo `YayaLogo` para consistencia.
    - `logo_splash`: Únicamente para el Splash Screen.
    - `logo_yaya_typographic`: Para TopBars y Headers.
    - `ic_logo`: Para iconos y placeholders de avatares.
5. **Integridad de Marca:** Prohibido rotar, inclinar o deformar los logotipos.
6. **Consistencia DRY:** Prohibido duplicar lógica de formateo; uso obligatorio de `FormatterUtils`.

## Paleta de Estados (Checklist)
- **Naranja:** Pendiente.
- **Verde:** Aceptada/Activa.
- **Azul:** Completada.
- **Rojo:** Cancelada/Error.

---
*BH++ Team - Diseño y Experiencia*
