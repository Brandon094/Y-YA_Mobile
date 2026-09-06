# 🎓 GUÍA DE PRESENTACIÓN PARA EL STAND - FERIA DE PROYECTOS SENA 2026

**Proyecto:** YÁYA (Ecosistema Móvil Android + Portal Web)  
**Programa:** Tecnólogo en Análisis y Desarrollo de Software (ADSO)  
**Equipo:** BH++ Team (Brandon Daza y Equipo)  
**Versión:** `v1.2.0` (versionCode 8 - Play Store Ready)  
**Portal Web Activo:** [https://y-ya-d5929.web.app](https://y-ya-d5929.web.app)  

---

## 🗣️ 1. GUIÓN EXPLICATIVO PARA EL STAND (1 A 2 MINUTOS)

### **Bienvenida e Introducción Amigable:**
> *"¡Hola! Bienvenidos a nuestro stand. Les presentamos **YÁYA**, la plataforma de intermediación de servicios locales que desarrollamos como proyecto de nuestro Tecnólogo en el SENA.*  
> *Identificamos una necesidad muy común en nuestra región: Cuando alguien en La Plata, Nátaga, Paicol o Neiva necesita un electricista, un plomero o un técnico de sistemas, termina pidiendo números en grupos de WhatsApp sin saber cuánto cobran ni si son personas de confianza.*  
> *Con YÁYA, creamos una solución digital integral que conecta directamente a clientes con prestadores independientes de la región."*

---

## 📲 2. DEMOSTRACIÓN EN VIVO (PASO A PASO EN EL CELULAR Y LAPTOP)

Para mostrar el proyecto a los compañeros e instructores que visiten el stand, sigan esta secuencia de 5 pasos prácticos:

```
+-----------------------------------------------------------------------------------+
| PASO 1: FILTRADO GEOGRÁFICO POR MUNICIPIO                                         |
| Muestra la barra superior de la App. Cambia la ubicación de "La Plata" a "Nátaga" |
| o "Neiva" para mostrar cómo el catálogo se actualiza al instante con los          |
| servicios disponibles en ese municipio específico.                               |
+-----------------------------------------------------------------------------------+
| PASO 2: AGENDAMIENTO INTELIGENTE Y WIZARD 2.0                                      |
| Muestra el Wizard de 2 pasos para la creación de servicios con su barra de        |
| progreso y duración estructurada. Intenta agendar una cita en una fecha pasada     |
| o fuera del horario del prestador para mostrar las validaciones.                  |
+-----------------------------------------------------------------------------------+
| PASO 3: NEGOCIACIÓN "HANDSHAKE" Y CHAT REALTIME                                   |
| Muestra el chat en tiempo real y cómo el cliente propone un precio y el           |
| prestador confirma el acuerdo mediante el mecanismo digital "Handshake".          |
+-----------------------------------------------------------------------------------+
| PASO 4: TUTORIAL SPOTLIGHT Y PERFIL 2.0                                           |
| Inicia el Tutorial Spotlight que guía al usuario por la interfaz con recortes     |
| y Anillo de Luz. Luego, en el perfil, muestra el rediseño por pestañas y las      |
| tarjetas de acceso rápido.                                                        |
+-----------------------------------------------------------------------------------+
| PASO 5: DEMOSTRACIÓN DEL PORTAL WEB EN LA LAPTOP (EMBUDO DE CONVERSIÓN)            |
| Muestra el Portal Web (https://y-ya-d5929.web.app/manuales) que actúa como        |
| embudo de conversión público, presentando la solución, manuales por rol          |
| y botón directo de descarga hacia Google Play Store.                               |
+-----------------------------------------------------------------------------------+
```

---

## 🛠️ 3. PUNTOS DESTACADOS PARA COMPAÑEROS E INSTRUCTORES ADSO

Si tus compañeros u otros instructores te preguntan sobre el desarrollo técnico del software, resalta estos puntos:

1. **Lenguaje y UI Móvil:** Desarrollada 100% nativa en **Kotlin** utilizando **Jetpack Compose** con arquitectura de componentes **Atomic Design** (Átomos, Moléculas, Organismos).
2. **Arquitectura:** Patrón **Clean MVVM** con `StateFlow` y reactividad de estados para que la vista sea totalmente tonta/declarativa.
3. **Backend & Base de Datos:** **Supabase (PostgreSQL en la nube)** con suscripciones en tiempo real (`Realtime WebSockets`), autenticación y almacenamiento de imágenes (`Storage`).
4. **Portal Web (Embudo de Conversión):** Construido en HTML5 + JavaScript modular con estilos en **Tailwind CSS**, desplegado en producción en **Firebase Hosting** de Google Cloud. Funciona como el **Embudo de Conversión** de la plataforma para presentar los beneficios, manuales por rol y dirigir a los usuarios a la descarga de la App en Google Play Store.
5. **Calidad y Seguridad:** Adaptación a Tema Oscuro/Claro, capa de anonimato para moderadores comunitarios y compilación `.aab` (`versionCode 6`) lista en Google Play Console.

---

## 💬 4. DINÁMICA DE INTERACCIÓN EN EL STAND

- *"¿Quieren probar la App en el celular de prueba?"*
- *"Pueden escanear el código QR del stand para abrir el Portal Web o leer el Manual de Uso oficial."*
- *"¡Gracias por pasar por nuestro stand de YÁYA!"*

---
*Guía de Presentación del Stand - Proyecto YÁYA v1.2.0 • BH++ Team SENA*
