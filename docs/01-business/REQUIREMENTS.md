# Requisitos del Proyecto - YÁYA

## 1. Requisitos Funcionales (RF)

### Gestión de Usuarios
- **RF-01:** El sistema permitirá el registro de usuarios mediante correo y contraseña.
- **RF-02:** El sistema permitirá el inicio de sesión seguro.
- **RF-03:** El usuario podrá elegir entre dos roles: Cliente o Prestador de Servicios.
- **RF-04:** El usuario podrá editar su perfil (foto, descripción, contacto).

### Catálogo de Servicios
- **RF-05:** El sistema mostrará una lista de categorías de servicios disponibles.
- **RF-06:** El sistema permitirá filtrar prestadores por calificación y cercanía.
- **RF-07:** El usuario podrá ver el detalle de un prestador antes de contratar.

### Contratación
- **RF-08:** El cliente podrá enviar una solicitud de servicio a un prestador.
- **RF-09:** El prestador podrá aceptar o rechazar solicitudes.

## 2. Requisitos No Funcionales (RNF)

### Rendimiento
- **RNF-01:** La aplicación debe cargar la pantalla principal en menos de 2 segundos.
- **RNF-02:** Las imágenes deben estar optimizadas para minimizar el consumo de datos.

### Seguridad
- **RNF-03:** Toda la comunicación con el backend debe realizarse vía HTTPS.
- **RNF-04:** Las credenciales de Supabase deben protegerse mediante `secrets.properties` u ocultarse en el código compilado (En proceso: actualmente hardcoded por facilidad de desarrollo inicial).

### Usabilidad
- **RNF-05:** La interfaz debe seguir las guías de Material Design 3.
- **RNF-06:** La aplicación debe ser compatible con modo claro y oscuro.

---
*Analista de Software: BH++*
