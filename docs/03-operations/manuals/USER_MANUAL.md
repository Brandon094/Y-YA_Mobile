# Manual del Usuario Final - YÁYA (v1.2.0)

Bienvenido a YÁYA. La plataforma de intermediación donde el talento local se conecta con soluciones rápidas, transparentes y seguras. Este manual oficial orienta detalladamente el funcionamiento del sistema para cada rol de usuario.

---

## Visor del Manual Integrado en la Aplicación
Además de este documento técnico, el usuario puede consultar en cualquier momento el manual de uso directamente desde la aplicación móvil:
1. Abra la aplicación YÁYA y diríjase a la pestaña "Mi Perfil".
2. En el menú de opciones, seleccione "Manual de Uso de la App".
3. El sistema ejecutará la pantalla `LegalViewerScreen`, presentando el manual correspondiente según el rol del usuario autenticado (`ManualConstants.getManualContentForRole`):
   * **Manual para Clientes:** Desplegado para usuarios con rol de cliente (`role == "client"`).
   * **Manual para Prestadores:** Desplegado para usuarios con rol de prestador (`role == "provider"`).
   * **Manual Maestro de YÁYA:** Desplegado para usuarios con rol de administrador (`role == "admin"`).

---

## 1. Introducción y Requisitos del Sistema
YÁYA es un ecosistema móvil diseñado para conectar a trabajadores independientes (Prestadores) con usuarios o entidades que requieren servicios (Clientes), optimizado con filtrado geográfico por municipio y un motor centralizado de validación de datos.

### 1.1. Requisitos del Dispositivo
* **Sistema Operativo:** Android 8.0 (Oreo / API 26) o versión superior.
* **Conexión a Red:** Se requiere conectividad activa a Internet (Wi-Fi o datos móviles). La aplicación cuenta con un indicador automático de estado de red (`YayaOfflineBanner`).
* **Almacenamiento Libre:** Mínimo 50 MB de almacenamiento interno disponible.

---

## 2. Registro e Inicio de Sesión

### 2.1. Registro de Cuenta Nueva (Wizard de 3 Pasos)
1. Abra la aplicación y presione el botón "Regístrate".
2. Complete el flujo de registro guiado:
   * **Paso 1: Datos Personales:** Nombre Completo (letras y espacios), Documento de Identidad (6-12 dígitos) y Fecha de Nacimiento (Selección mediante calendario con **inhabilitación automática de fechas** para garantizar el cumplimiento de la edad mínima legal de **15 años**).
   * **Paso 2: Ubicación y Contacto:** Teléfono Móvil (10 dígitos) y Municipio del Huila.
   * **Paso 3: Credenciales:** Correo Electrónico y Contraseña Segura (mínimo 8 caracteres, mayúscula, minúscula y número).
3. **Selección de Rol Inicial:**
   * "Quiero servicios" (Cliente / Solicitante).
   * "Ofrecer talentos" (Prestador / Proveedor).
4. Acepte los Términos y Condiciones y la Política de Privacidad.
5. Presione "Registrarme". El sistema realizará una **Doble Verificación Previa** para asegurar que el documento y el correo no existan previamente.

### 2.2. Autenticación y Recuperación de Credenciales
1. Ingrese su correo electrónico y contraseña en la pantalla de acceso.
2. En caso de olvido de credenciales, presione "¿Olvidó su contraseña?" para recibir un enlace de restablecimiento en su correo registrado.

---

## 3. Guía por Roles: Capacidades y Restricciones (Facultades y Prohibiciones)

---

### 3.1. ROL CLIENTE (SOLICITANTE)

El usuario Cliente accede a YÁYA para buscar, negociar y contratar talentos locales con agendamiento preciso.

#### LO QUE PUEDE HACER EL CLIENTE (FACULTADES)
* **Búsqueda y Filtrado Geográfico por Municipio:** Filtrar servicios por municipio (La Plata, Nátaga, Paicol, Tesalia, Garzón, Neiva, Pitalito, Gigante o "Todos") desde el chip interactivo de la barra superior (`HomeTopBar`).
* **Navegación por Categorías:** Explorar ofertas organizadas por rubro (Hogar, Mascotas, Tecnología, Salud, Belleza, etc.) o realizar búsquedas por palabras clave.
* **Consulta de Reputación del Prestador:** Visualizar la puntuación promedio de estrellas y la cantidad de calificaciones del prestador directamente en la tarjeta de servicio (`ServiceCard`) y en la cabecera de perfil.
* **Agendamiento Inteligente:** Probar solicitudes especificando dirección de atención (mínimo 5 caracteres), fecha (exclusivamente en días habilitados por el prestador) y hora dentro del rango activo.
* **Negociación Directa de Tarifas ("Handshake"):** Proponer una oferta económica inicial y negociar contraofertas con el prestador hasta alcanzar un precio de acuerdo.
* **Mensajería Instantánea:** Mantener comunicación mediante chat en tiempo real para coordinar detalles técnicos de la solicitud.
* **Confirmación de Inicio ("Handshake Digital"):** Otorgar la aprobación final del precio convenido en la sección "Mis Pedidos" mediante la acción "CONFIRMAR Y EMPEZAR" para iniciar formalmente la prestación.
* **Calificación y Reseñas:** Evaluar el trabajo finalizado asignando entre 1 y 5 estrellas y redactando un comentario público sobre el prestador.

#### LO QUE NO PUEDE HACER EL CLIENTE (RESTRICCIONES)
* **Agendamiento en Pasado:** El sistema bloquea de forma estricta las solicitudes en fechas pasadas o en horas transcurridas durante el mismo día.
* **Citas Fuera de Jornada u Horarios Comprometidos:** No es posible seleccionar días u horas en que el prestador no preste servicio o mantenga compromisos previos.
* **Registro de Datos Inválidos:** Prohibido ingresar documentos de identidad de longitud diferente a 6-12 dígitos o números telefónicos con número de dígitos distinto a 10.
* **Uso Inadecuado del Chat:** Prohibido emplear el canal de mensajería para conductas engañosas, agresivas o fraudulentas. La reincidencia genera advertencias disciplinarias, suspensión de servicio o cancelación de cuenta.

---

### 3.2. ROL PRESTADOR (TALENTO)

El usuario Prestador publica sus habilidades, administra su jornada laboral y gestiona las solicitudes recibidas. Cuenta con acceso universal, pudiendo actuar también como cliente contratante.

#### LO QUE PUEDE HACER EL PRESTADOR (FACULTADES)
* **Publicación y Edición de Servicios (Wizard de 2 Pasos):** Registrar talentos mediante un proceso simplificado:
    * **Paso 1: Información General:** Título, descripción, precio y categoría.
    * **Paso 2: Multimedia y Ubicación:** Galería de fotos y municipio de cobertura.
    * **Selector de Duración Compuesta:** Define el tiempo estimado del servicio combinando Valor Numérico + Unidad (Minutos, Horas, Días).
* **Configuración de Jornada Maestra ("Mi Horario"):** Estipular su horario laboral general y días activos en la sección "Mi Perfil > Mi Horario de Trabajo". La vista es compacta (sin scroll) e incluye **Presets de configuración rápida** (atajos de 1 clic) para activar jornadas completas o turnos estándar instantáneamente.
* **Asignación Horaria por Servicio:** Asignar días y rangos de atención específicos para cada servicio ofertado mediante la función "Cargar mi jornada maestra".
* **Detección e Indicación de Traslapes:** Recibir alertas contextuales en pantalla si se intenta asignar a un servicio un horario que colisiona con otro servicio activo previamente registrado.
* **Gestión de Solicitudes:** Aceptar, rechazar o enviar contraofertas a las propuestas económicas de los clientes.
* **Consulta de Reputación y Reseñas:** Examinar el promedio de puntuación y el listado histórico de comentarios de clientes desde la opción "Mi Reputación y Reseñas" en la sección "MI TALENTO" del perfil.

#### LO QUE NO PUEDE HACER EL PRESTADOR (RESTRICCIONES)
* **Oferta Fuera de Jornada Maestra:** Inhabilitado para publicar servicios en días o rangos desactivados en su configuración de disponibilidad maestra.
* **Solapamiento Horario entre Servicios:** Bloqueo de guardado automático si dos servicios del mismo prestador coinciden en el mismo intervalo horario del mismo día (`ValidationUtils.isTimeRangeOverlapping`).
* **Publicaciones Engañosas:** Prohibido publicar tarifas irreales o imágenes no autorizadas. Toda publicación entra en estado "En Revisión" para auditoría administrativa antes de su difusión pública.

---

### 3.3. ROL ADMINISTRADOR (MODERACIÓN Y CALIDAD)

El Administrador supervisa la plataforma, audita la calidad de los contenidos y aplica los protocolos disciplinarios del sistema.

#### LO QUE PUEDE HACER EL ADMINISTRADOR (FACULTADES)
* **Acceso al Dashboard Administrativo:** Entrar al panel de control exclusivo desde el perfil de usuario (`ProfileScreen`).
* **Auditoría de Servicios:** Evaluar, aprobar, pausar o rechazar servicios publicados por la comunidad de prestadores.
* **Gestión de Reportes y Semáforo Disciplinario:** Evaluar denuncias agrupadas por usuario infractor según el nivel de gravedad:
  * Nivel Amarillo (1-2 Reportes): Recomendación de Llamado de Atención.
  * Nivel Naranja (3-4 Reportes): Recomendación de Suspensión Temporal (desactivación de servicios).
  * Nivel Rojo (5+ Reportes): Recomendación de Eliminación Definitiva de Cuenta.
* **Llamados de Atención Automáticos:** Emitir notificaciones preventivas vía chat desde el panel administrativo hacia el usuario infractor.
* **Sanciones Directas:** Desactivar de forma inmediata servicios de prestadores infractores o eliminar cuentas en casos de falta grave o reincidencia.
* **Capa de Anonimato Protegido:** Operar ante usuarios comunitarios bajo la denominación enmascarada "Equipo de Moderación YÁYA" con el isotipo oficial del sistema.

#### LO QUE NO PUEDE HACER EL ADMINISTRADOR (RESTRICCIONES)
* **Revelar Identidad Personal:** Prohibido compartir nombres o datos personales de moderadores con usuarios objeto de auditoría o sanción.
* **Alterar Acuerdos Legítimos:** Inhabilitado para modificar valores, fechas o condiciones pactadas válidamente entre cliente y prestador.

---

## 4. Preguntas Frecuentes (FAQ)

* **¿Cómo consulto el manual de uso desde la aplicación?**
  Diríjase a "Mi Perfil > Manual de Uso de la App". El sistema mostrará la versión técnica correspondiente a su rol.
* **¿Cómo se efectúa la coordinación del pago?**
  YÁYA facilita la negociación de tarifas mediante la herramienta "Handshake Digital". El pago final se efectúa de común acuerdo entre cliente y prestador según la modalidad acordada.
* **¿Puedo publicar servicios si mi registro inicial fue como cliente?**
  Sí. La cuenta de YÁYA es universal. Ingrese a "Mi Perfil > Mis Servicios" y presione el botón "+" para publicar su servicio.
* **¿Cómo se realiza el reporte de una conducta inadecuada?**
  Utilice el botón de reporte (icono de bandera) visible en el perfil del usuario o en la tarjeta del servicio para enviar una denuncia al equipo de moderación.

---

### Soporte Técnico
Para soporte directo o consultas institucionales, contacte a BH++ Team mediante los canales oficiales o el portal web del proyecto.

---
*YÁYA - Desarrollado por BH++ Team. Documento Oficial v1.2.0 (2026).*
