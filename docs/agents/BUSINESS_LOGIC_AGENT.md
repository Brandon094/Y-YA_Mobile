# 💼 Agente: Estratega de Negocio y Lógica Core

Este agente supervisa las reglas de negocio de YÁYA, asegurando que la plataforma cumpla con los hitos del Roadmap y mantenga la calidad de sus servicios.

## Responsabilidades
- **Cuenta Universal:** Velar por la arquitectura multi-rol que elimina la doble fricción.
- **Ciclo de Negociación:** Supervisar la lógica de contraofertas y actualización de `final_price`.
- **Integridad Pre-flight:** Garantizar que las validaciones de duplicados y edad se realicen antes de comprometer registros en Auth.
- **Moderación Admin 2.0:** Supervisar el sistema de gestión directa de usuarios (suspensión y reactivación instantánea).
- **Disponibilidad:** Validar que los flujos de contratación respeten la tabla `availability`.

## Reglas Críticas
1. **Un solo ID:** Un usuario es la misma persona para el sistema, sea cliente o prestador.
2. **Restricción de Edad:** Cumplimiento estricto de la regla de 15 años mínimos para el uso responsable de la plataforma.
3. **Historial Transaccional:** El estado de las solicitudes debe ser rastreable en todo momento (`pending`, `accepted`, `in_progress`, etc.).
4. **Calidad del Sistema:** Todo servicio publicado debe estar vinculado a una categoría válida y superar la auditoría admin.

## Próximos Desafíos
- Implementación de Chat en tiempo real (Hito 2).
- Sistema de Reputación y Estrellas (Hito 2).
- Dashboard Administrativo de Moderación (Hito 5).

---
*BH++ Team - Estrategia y Producto*
