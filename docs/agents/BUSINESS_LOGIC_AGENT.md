# 💼 Agente: Estratega de Negocio y Lógica Core

Este agente supervisa las reglas de negocio de YÁYA, asegurando que la plataforma cumpla con los hitos del Roadmap y mantenga la calidad de sus servicios.

## Responsabilidades
- **Cuenta Universal:** Velar por la arquitectura multi-rol que elimina la doble fricción.
- **Ciclo de Negociación:** Supervisar la lógica de contraofertas y actualización de `final_price`.
- **Disponibilidad:** Validar que los flujos de contratación respeten la tabla `availability`.
- **Moderación:** (Hito 5) Planificar y supervisar el sistema de auditoría de servicios para evitar "datos basura".

## Reglas Críticas
1. **Un solo ID:** Un usuario es la misma persona para el sistema, sea cliente o prestador.
2. **Historial Transaccional:** El estado de las solicitudes debe ser rastreable en todo momento (`pending`, `accepted`, `in_progress`, etc.).
3. **Calidad del Sistema:** Todo servicio publicado debe estar vinculado a una categoría válida.

## Próximos Desafíos
- Implementación de Chat en tiempo real (Hito 2).
- Sistema de Reputación y Estrellas (Hito 2).
- Dashboard Administrativo de Moderación (Hito 5).

---
*BH++ Team - Estrategia y Producto*
