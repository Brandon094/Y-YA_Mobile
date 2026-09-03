# 🚀 Guía de Lanzamiento: Google Play Store - YÁYA

Este documento detalla los pasos críticos para pasar de un entorno de desarrollo a la publicación oficial en la tienda de aplicaciones de Android.

---

## 1. 🛡️ Preparación Técnica (Build for Production)

### 🔹 Firmado de Aplicación (Signing)
*   [x] **Generar KeyStore:** Archivo `yaya-production.jks` generado exitosamente (Alias: `yaya-key`).
*   [x] **Registrar SHA en Firebase:** Huellas SHA-1 (BD:B9) y SHA-256 (E4:DE) obtenidas para vinculación con servicios de Google.
*   [x] **Configurar Build Variants:** Configurada variante de compilación `release` con firmado e integración de secretos.
*   [x] **Generar App Bundle (AAB):** Google Play exige el formato `.aab` generado vía `bundleRelease`.

### 🔹 Optimización y Limpieza
*   [x] **Minificación (R8/ProGuard):** Habilitado `isMinifyEnabled = true` e `isShrinkResources = true` en el build de release.
*   [x] **Símbolos de Depuración Nativos:** Configurado `ndk { debugSymbolLevel = 'FULL' }` en `app/build.gradle.kts` para desofuscación y diagnóstico de crashes nativos en Play Console.
*   [x] **Versionado:** Incrementado `versionCode` a `6` (`versionName "1.0.1"`) en `app/build.gradle.kts`.
*   [x] **Permisos de Google Play (AD_ID):** Declaración explícita del permiso `com.google.android.gms.permission.AD_ID` en `AndroidManifest.xml` para cumplimiento de políticas de publicidad/analítica.
*   [ ] **Iconos Adaptativos:** Verificar que el logo de YÁYA se vea bien en todas las máscaras de iconos de Android.

---

## 2. 📝 Activos de Marketing (Store Listing)

### 🔹 Información Básica
*   [x] **Título:** YÁYA Conecta & Contrata.
*   [x] **Descripción Corta:** Encuentra expertos, negocia precios justos y contrata servicios de forma segura.
*   [x] **Descripción Larga:** Finalizada y cargada en la consola (resaltando Handshake y Subasta).
*   [x] **URL de Política de Privacidad:** Link oficial configurado: [https://y-ya-d5929.web.app/privacidad.html](https://y-ya-d5929.web.app/privacidad.html).
*   [x] **URL de Eliminación de Datos:** Link oficial configurado: [https://y-ya-d5929.web.app/eliminar-cuenta.html](https://y-ya-d5929.web.app/eliminar-cuenta.html).

### 🔹 Contenido Visual
*   [ ] **Icono de la App:** 512x512 px, PNG o WEBP de 32 bits.
*   [ ] **Gráfico de funciones:** 1024x500 px.
*   [ ] **Capturas de pantalla (Screenshots):** Al menos 4 capturas de alta calidad (Home, Detalle, Chat, Negociación).
*   [ ] **Video promocional (Opcional):** Link de YouTube.

---

## 3. ⚖️ Cumplimiento Legal y Privacidad

### 🔹 Requisitos Críticos
*   [x] **URL de Política de Privacidad:** Link público verificado: [https://y-ya-d5929.web.app/privacidad.html](https://y-ya-d5929.web.app/privacidad.html).
*   [ ] **Clasificación de contenido:** Completar el cuestionario de IARC en el console.
*   [ ] **Seguridad de los datos:** Declarar qué datos recopila YÁYA (ID, Correo, Ubicación aproximada, etc.).

---

## 4. ⚙️ Configuración en Google Play Console

### 🔹 Pasos Administrativos
*   [ ] **Cuenta de Desarrollador:** Registro y pago único de $25 USD.
*   [ ] **Pruebas Internas:** Subir el AAB a la pista de pruebas para Mauro, Harold y Brandon.
*   [ ] **Acceso a la App:** Proporcionar credenciales de prueba (Usuario y Clave) para que los revisores de Google puedan entrar a la App.

---

## 🏁 Cronograma de Publicación
1.  **Semana 1:** Generación de firma y pruebas de Build Release.
2.  **Semana 2:** Preparación de capturas de pantalla y video.
3.  **Semana 3:** Registro en Console y carga de documentos legales.
4.  **Semana 4:** Envío a revisión de Google (puede tardar de 3 a 7 días).

---
*BH++ Team - Rumbo al Lanzamiento Global*
