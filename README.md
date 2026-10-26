<div align="center">

# Splitly · Mobile App

**Organiza tu hogar, distribuye los gastos y lleva el control de los aportes.**

Aplicación Android para la gestión de hogares compartidos desde el rol de representante.

**Kotlin · Jetpack Compose · Material 3 · Retrofit**

[Instalación](docs/INSTALACION.md) · [Arquitectura](docs/ARQUITECTURA.md) · [App del miembro](https://github.com/1ACC0238-2610-3667/Splitly_Member)

</div>

---

## ¿Qué es Splitly?

Splitly conecta a quienes comparten los gastos de un hogar. Esta aplicación concentra la experiencia del **representante**: administrar hogares y miembros, registrar recibos, repartir sus importes y aprobar pagos notificados por los miembros.

El proyecto se complementa con [Splitly Member](https://github.com/1ACC0238-2610-3667/Splitly_Member), desarrollado en Flutter para consultar cuotas y solicitar la revisión de pagos. Ambos clientes necesitan una misma instancia compatible de la API para trabajar con los mismos hogares.

## Funcionalidades

| Área | Qué permite hacer |
| --- | --- |
| Acceso | Registrar una cuenta de representante, iniciar sesión y cerrar sesión. |
| Dashboard | Consultar cantidad de hogares y miembros, importes recaudados y pendientes, y solicitudes de pago por aprobar. |
| Hogares | Crear, consultar, editar y eliminar hogares del representante. |
| Miembros | Consultar miembros y enviar invitaciones mediante la API. |
| Gastos | Crear un recibo, su contribución y las cuotas individuales con reparto en partes iguales. |
| Historial | Consultar recibos y el detalle de las contribuciones y cuotas asociadas. |
| Configuración | Consultar y actualizar preferencias de modo oscuro y notificaciones en el backend. |

La aplicación registra y revisa aportes; **no procesa transferencias ni integra una pasarela de pagos**.

## Recorrido del producto

1. Registra una cuenta e inicia sesión como representante.
2. Crea un hogar y comparte su identificador con los miembros.
3. Consulta los miembros asociados y crea un gasto con importe y fechas.
4. El cliente divide el gasto en partes iguales entre los miembros recuperados de la API.
5. Los miembros consultan sus cuotas y notifican pagos desde Splitly Member.
6. Revisa las solicitudes en el dashboard y aprueba las que correspondan.

## Puesta en marcha

Necesitas Android Studio compatible con el proyecto, JDK **21**, SDK de compilación **36.1** y un emulador o dispositivo Android **7.0 / API 24** o superior.

```powershell
git clone https://github.com/1ACC0238-2610-3667/Mobile-App.git
cd Mobile-App
.\gradlew.bat --version
```

Abre el directorio en Android Studio, sincroniza Gradle y ejecuta el módulo `app`. El proyecto utiliza Gradle Wrapper **9.4.1**, Android Gradle Plugin **9.1.1** y Compose BOM **2026.02.01**.

La API está configurada en `app/src/main/java/com/appsmoviles/splitly/model/client/RetrofitClient.kt`:

```text
https://harmonix-mobile-backend.onrender.com/api/v1/
```

Puedes sustituirla por tu backend compatible. No hay lectura de `.env` implementada; conserva la barra final que requiere la URL base de Retrofit. La dirección configurada no garantiza disponibilidad del servicio.

Consulta la [guía de instalación](docs/INSTALACION.md) para preparar el SDK, configurar una API local, compilar y verificar el flujo.

## Estructura y tecnologías

```text
app/src/main/java/com/appsmoviles/splitly/
├── MainActivity.kt     # Inicialización y contenido Compose
├── view/               # Pantallas y navegación
├── viewmodel/          # Estado de presentación y operaciones remotas
├── model/
│   ├── beans/          # Modelos y DTOs
│   ├── client/         # Retrofit, OkHttp, autenticación y sesión
│   ├── response/       # Interfaces de los servicios REST
│   └── db/             # Archivo OpenHelper, sin implementación actual
├── ui/theme/           # Colores, tipografía y tema Material
└── util/               # Utilidades de traducción
```

La UI utiliza Jetpack Compose y Material 3; Navigation Compose organiza las rutas. Los ViewModels usan corrutinas y estado observable de Compose. Retrofit y Gson conectan con la API; un interceptor OkHttp añade el token. SharedPreferences conserva datos de sesión.

Las versiones están en [gradle/libs.versions.toml](gradle/libs.versions.toml) y [app/build.gradle.kts](app/build.gradle.kts).

## Verificación y estado actual

```powershell
.\gradlew.bat :app:assembleDebug :app:lintDebug :app:testDebugUnitTest
```

Con un emulador o dispositivo conectado:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

Las pruebas incluidas solo comprueban una suma y el identificador del paquete; no cubren los flujos de negocio.

Aspectos pendientes de la implementación actual:

- Las operaciones de gastos dependen de varias peticiones: el cliente no realiza una transacción atómica ni revierte automáticamente creaciones parciales.
- El reparto implementado es equitativo; no calcula cuotas proporcionales a los ingresos.
- `OpenHelper.kt` solo declara el paquete: no hay persistencia SQLite ni modo offline de negocio implementado en esta versión de `main`.
- El ajuste remoto de modo oscuro no controla el tema global, que usa la configuración del sistema. No hay integración de notificaciones push ni traducción completa de las pantallas.
- La compilación release requiere preparar la firma para su distribución.

## Documentación y colaboración

| Guía | Contenido |
| --- | --- |
| [Instalación y ejecución](docs/INSTALACION.md) | Entorno, backend, compilación, diagnóstico y verificación manual. |
| [Arquitectura y API](docs/ARQUITECTURA.md) | Capas, sesión, servicios REST y ciclo de creación y aprobación de cuotas. |

Al contribuir, describe el comportamiento modificado y adjunta resultados de compilación, lint, pruebas y verificación manual. Mantén la compatibilidad del contrato con Splitly Member y evita subir credenciales, tokens, `local.properties` o claves de firma. El repositorio no incluye un archivo `LICENSE`.
