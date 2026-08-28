# 🤖 Agente de Desarrollo YÁYA - Manual de Instrucciones Senior

Este documento define las leyes inmutables del código para el ecosistema **YÁYA**. Cualquier agente de IA o desarrollador debe adherirse a estos estándares para mantener la integridad Premium del software.

## 1. Metodología Obligatoria: Atomic Design
Toda la interfaz de usuario debe construirse siguiendo la jerarquía atómica en `ui/components/`:
- **Atoms (`atoms/`):** Componentes base (`YayaButton`, `YayaLogo`, `YayaAvatar`). Deben ser 100% reutilizables y sin lógica de red.
- **Molecules (`molecules/`):** Unidades funcionales simples (`RatingIndicator`, `YayaNegotiationDialog`).
- **Organisms (`organisms/`):** Secciones de pantalla orquestadas (`ServiceCard`, `HomeTopBar`).
- **Páginas (`screens/`):** Orquestadores finales que inyectan el ViewModel.

## 2. Filosofía DRY & Formatter Engine
**Prohibido duplicar lógica de formateo.**
- Toda transformación de moneda, fecha o tiempo debe centralizarse en `com.bhplusplus.yaya.utils.FormatterUtils`.
- Formato de Moneda estándar: **Compacto** (Ej: $ 50k, $ 1.2M).

## 3. Estándar Clean MVVM
- **Vistas Pasivas:** Las funciones Composable no deben realizar cálculos, parsing de fechas o decisiones de negocio.
- **UiState:** El ViewModel debe entregar un modelo de datos final y strings ya formateados. La vista solo "pinta".
- **Reactividad:** Priorizar `Supabase Realtime` para mantener la UI sincronizada sin intervención del usuario.

## 4. Accesibilidad & Diseño Universal
- **Blindaje 200% Font:** Todos los layouts deben ser resilientes a fuentes gigantes.
- **Técnicas de Control:**
    - Usar `FlowRow` para listas de elementos pequeños (días, badges).
    - Usar `Modifier.weight(1f)` para permitir que los textos respiren y se recorten con elipses.
    - Usar `sizeIn` para contenedores circulares.
    - `Modifier.verticalScroll` obligatorio en formularios.

## 5. Protocolo de Red & Observabilidad
- **Skeleton Ready:** Todas las pantallas de carga deben implementar un `ShimmerEffect` (Skeletons) que imite la estructura final.
- **Handshake Flow:** El flujo de servicios debe respetar estrictamente los estados: `pending` -> `accepted` -> `in_progress` -> `completed`.
- **Manejo de Errores Senior:** Prohibido dejar bloques `catch` vacíos. Es obligatorio usar `CrashReporter.logException(e)` para notificar errores no fatales a la consola de Firebase.

## 6. Gobernanza Git & Commits
Usar **Conventional Commits** estrictos:
- `feat:` Funcionalidades que añaden valor al usuario.
- `refactor:` Mejoras de estructura (Ej. migración a Atomic Design).
- `design:` Ajustes puramente visuales o de UX.
- `fix:`, `docs:`, `chore:`.

---
*Propiedad Intelectual de **BH++** - 2026*
