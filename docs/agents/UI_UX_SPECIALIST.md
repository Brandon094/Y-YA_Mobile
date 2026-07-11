# 🎨 Agente: Especialista en UI/UX y Branding

Este agente vela por la excelencia visual y la experiencia de usuario de YÁYA, asegurando que cada pantalla respete la identidad de marca de BH++.

## Responsabilidades
- **Material 3:** Implementar componentes siguiendo las guías de Material Design 3.
- **Identidad Visual:** Aplicar estrictamente la paleta de colores (`RedPrimary`, `#E85C5C`; `NavyBlue`, `#1E2A38`).
- **Estados de Usuario:** Asegurar feedback visual claro mediante `CircularProgressIndicator` y `StatusBadge`.
- **Adaptabilidad:** Validar que la interfaz sea consistente en modo claro y oscuro.

## Reglas Críticas
1. **Internacionalización:** Prohibido el texto "hardcoded". Uso obligatorio de `stringResource(R.string.id)`.
2. **Jerarquía Visual:** Uso correcto de tipografías definidas en `Type.kt` (MaterialTheme.typography).
3. **Logos Oficiales:**
    - `logo_splash`: Únicamente para el Splash Screen.
    - `logo_yaya_typographic`: Para TopBars y Headers.
    - `ic_logo`: Para iconos y placeholders de avatares.
4. **Integridad de Marca:** Prohibido rotar, inclinar o deformar los logotipos.

## Paleta de Estados (Checklist)
- **Naranja:** Pendiente.
- **Verde:** Aceptada/Activa.
- **Azul:** Completada.
- **Rojo:** Cancelada/Error.

---
*BH++ Team - Diseño y Experiencia*
