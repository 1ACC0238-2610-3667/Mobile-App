# Instalación y ejecución

[Volver al proyecto](../README.md) · [Arquitectura y API](ARQUITECTURA.md)

## Requisitos

| Componente | Configuración del repositorio |
| --- | --- |
| JDK del daemon | 21, declarado en `gradle/gradle-daemon-jvm.properties`. |
| Gradle | 9.4.1 mediante el wrapper incluido. |
| Android Gradle Plugin | 9.1.1 en `gradle/libs.versions.toml`. |
| SDK de compilación | API 36, minor 1 (`36.1`). |
| SDK objetivo | API 36. |
| Dispositivo mínimo | API 24 / Android 7.0. |
| Backend | API REST compatible y datos de prueba para representante, hogar y miembros. |

Usa Android Studio con soporte para esta configuración. El nivel de compatibilidad del código Java es 11; es distinto del JDK 21 que ejecuta Gradle. No necesitas instalar Gradle globalmente.

## 1. Preparar el entorno

```powershell
git clone https://github.com/1ACC0238-2610-3667/Mobile-App.git
cd Mobile-App
java -version
.\gradlew.bat --version
```

En Android Studio:

1. Abre la carpeta raíz `Mobile-App`.
2. Instala desde SDK Manager la plataforma Android correspondiente a `compileSdk` 36.1 y las herramientas solicitadas por la sincronización.
3. Configura el JDK de Gradle y sincroniza el proyecto.
4. Configura la ubicación del Android SDK. Android Studio puede crear el archivo local `local.properties`; no lo añadas al repositorio.
5. Crea un dispositivo virtual con API 24 o superior, o conecta un teléfono con depuración USB.

La primera ejecución puede necesitar red para descargar Gradle, el toolchain y dependencias. En macOS o Linux usa `./gradlew` en lugar de `.\gradlew.bat`; si falta el permiso de ejecución, habilítalo con `chmod +x gradlew`.

## 2. Configurar la API

Edita `.baseUrl(...)` en:

```text
app/src/main/java/com/appsmoviles/splitly/model/client/RetrofitClient.kt
```

El valor incluido es `https://harmonix-mobile-backend.onrender.com/api/v1/`. El backend se mantiene fuera de este repositorio; su disponibilidad y compatibilidad deben comprobarse por separado.

Para un backend local en el equipo anfitrión y un emulador Android estándar:

```kotlin
.baseUrl("http://10.0.2.2:5070/api/v1/")
```

Conserva la barra final. Desde un teléfono físico utiliza la IP LAN del servidor y comprueba conectividad y firewall. `localhost` en el dispositivo no apunta a tu computadora. El manifiesto actual declara `INTERNET` y permite tráfico HTTP sin cifrar; utiliza HTTPS en un entorno de producción.

El cliente no carga `.env` ni obtiene la URL de `BuildConfig`. La configuración se modifica en el código. Si vas a verificar el flujo con Splitly Member, apunta ambos clientes al mismo servicio: sus valores predeterminados son distintos.

## 3. Compilar y ejecutar

Desde PowerShell, en la raíz del proyecto:

```powershell
.\gradlew.bat :app:assembleDebug
```

El APK de depuración se genera en `app/build/outputs/apk/debug/app-debug.apk`.

Con un dispositivo conectado:

```powershell
.\gradlew.bat :app:installDebug
```

Abre **Splitly** en el dispositivo. También puedes seleccionar el módulo `app` y pulsar Run en Android Studio.

## 4. Verificar

```powershell
.\gradlew.bat :app:lintDebug :app:testDebugUnitTest
```

Con un dispositivo o emulador disponible:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

`ExampleUnitTest` comprueba `2 + 2`; `ExampleInstrumentedTest` comprueba `com.appsmoviles.splitly`. Estas pruebas no validan autenticación, distribución de gastos ni aprobación de pagos.

Verificación manual recomendada con datos de prueba:

1. Registra una cuenta de representante e inicia sesión.
2. Crea un hogar y verifica que aparece en el listado.
3. Incorpora un miembro mediante el flujo compatible del backend y Splitly Member; confirma que aparece en el hogar.
4. Crea un gasto con importe y fechas. Verifica recibo, contribución y cuotas individuales.
5. Confirma que las cuotas reparten el importe en partes iguales entre los miembros recuperados.
6. Notifica un pago desde Splitly Member, revisa la solicitud en el dashboard y apruébala.
7. Vuelve a consultar ambas aplicaciones y comprueba los estados e importes.
8. Actualiza una preferencia y comprueba su persistencia; cierra sesión.

## Distribución

El proyecto no define una configuración de firma release propia. Antes de distribuirlo, prepara la firma, revisa la URL de producción y las políticas de copia de seguridad y red. No subas claves de firma ni contraseñas. Los comandos anteriores generan o instalan builds de desarrollo; no publican en una tienda.

## Diagnóstico

| Síntoma | Qué revisar |
| --- | --- |
| Gradle no inicia o no encuentra Java | JDK 21, configuración del daemon y acceso a las descargas del toolchain. |
| SDK no encontrado | SDK Manager y ruta local en `local.properties`. |
| Plataforma de compilación ausente | SDK correspondiente a API 36 minor 1. |
| No hay conexión a la API | URL, barra final, disponibilidad del servidor y red del dispositivo. |
| Respuesta 401/403 | Sesión, token, rol y permisos asignados por el backend. |
| «No hay miembros en este hogar» | Asociaciones del hogar devueltas por la API antes de crear gastos. |
| Un gasto falla tras crear el recibo | Comprueba qué recursos se crearon antes de reintentar: no hay rollback en el cliente. |
| Ajuste oscuro sin efecto global | El tema sigue la configuración del sistema; la preferencia remota no está conectada. |
