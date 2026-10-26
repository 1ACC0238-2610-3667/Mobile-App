# Arquitectura y contrato consumido

[Volver al proyecto](../README.md) · [Instalación](INSTALACION.md)

## Organización

La aplicación utiliza pantallas Compose, ViewModels y servicios Retrofit. Los ViewModels llaman directamente a los servicios; no existe una capa de repositorios independiente en esta versión.

| Capa | Ubicación dentro de `com.appsmoviles.splitly` | Responsabilidad |
| --- | --- | --- |
| Inicio | `MainActivity.kt` | Inicializar cliente HTTP y sesión, crear ViewModels y abrir la navegación. |
| Presentación | `view/` | Login, registro, dashboard, hogares, miembros, gastos, historial y ajustes. |
| Navegación | `view/nav/` | Rutas raíz y rutas internas, barra inferior y menú lateral. |
| Estado | `viewmodel/` | Estado observable de Compose, corrutinas y coordinación de llamadas a la API. |
| Modelos | `model/beans/` | Entidades, respuestas y DTOs de creación. |
| Red y sesión | `model/client/` | Retrofit, Gson, OkHttp, interceptor Bearer y preferencias de sesión. |
| Servicios | `model/response/` | Interfaces REST de autenticación, hogares, miembros, gastos y ajustes. |
| Tema | `ui/theme/` | Tipografía y esquemas Material, incluidos colores dinámicos. |

El recorrido de datos es **pantalla → ViewModel → servicio Retrofit → API → estado observable → pantalla**. Las operaciones remotas se coordinan mediante corrutinas; las pantallas consumen estados de carga, resultados y mensajes de error.

## Inicialización y navegación

`MainActivity` inicializa `OkHttpClientObject` y `CredentialsSessionManager` antes de montar la UI. Esa secuencia es necesaria: el cliente Retrofit obtiene su instancia de OkHttp al inicializarse.

`Navigator` abre `LogIn`, permite pasar a `SignUp` y, después del acceso, a `Main`. `MainScreen` contiene las rutas `Dashboard`, `Expenses`, `Households`, `Members`, `Settings` y `Contributions`.

## Autenticación y estado local

La sesión se almacena en SharedPreferences bajo `splitly_prefs`, incluyendo el token y datos del usuario. `AuthInterceptor` añade `Authorization: Bearer <token>` si existe, excepto para las rutas de inicio de sesión y registro. El cierre de sesión limpia las preferencias.

El formulario de registro envía `Representative` en la solicitud. La autorización efectiva corresponde al backend; el rol mostrado por el cliente no sustituye sus controles de acceso.

No hay renovación automática de token implementada. El archivo `model/db/OpenHelper.kt` solo contiene la declaración de paquete; no proporciona SQLite, caché de gastos ni sincronización offline. Las preferencias locales no constituyen un almacén cifrado de credenciales.

## Servicios REST

Las rutas son relativas a `/api/v1/`. Esta tabla resume las interfaces del cliente; no sustituye la especificación ni las validaciones del backend.

| Área | Métodos y rutas declarados |
| --- | --- |
| Autenticación | `POST authentication/sign-in`, `POST authentication/sign-up`. |
| Usuarios | `GET user`, `GET user/user/{id}`. |
| Hogares | `GET house_hold/{id}`, `GET house_hold/representative/{representativeId}`, `POST house_hold`, `PUT house_hold/{id}`, `DELETE house_hold/{id}`. |
| Miembros | `GET household_member/household/{householdId}`, `POST household_member`. |
| Invitaciones | `POST invitations`, `GET invitations/pending` con `email` y `householdId`. |
| Recibos | `GET bills`, `GET bills/byhousehold/{householdId}`, `POST bills`, `PUT bills/byid/{id}`, `DELETE bills/{id}`. |
| Contribuciones | `POST contribution`, `GET contribution/byhouseholdid/{householdId}`, `GET contribution/bybillid/{billId}`, `DELETE contribution/{id}`. |
| Cuotas individuales | `GET member_contribution/bymemberid/{memberId}`, `GET member_contribution/bycontributionid/{contributionId}`, `POST member_contribution`, `PUT member_contribution/{id}/approve`. |
| Ajustes | `GET settings` con `userId`, `POST settings`, `PUT settings/{id}`. |

Las interfaces `IncomeAllocationWebService` y `UserIncomeWebService` no añaden operaciones propias. No prueban que exista un reparto por ingresos implementado en esta aplicación.

## Creación de un gasto

`ContributionViewModel.createFullExpense` coordina estas operaciones:

1. Consulta los miembros del hogar y detiene el proceso si la lista está vacía.
2. Crea un recibo mediante `POST bills`.
3. Crea la contribución asociada con descripción, fecha límite y `strategy = 1`.
4. Calcula `totalAmount / members.size`.
5. Crea una cuota por miembro mediante peticiones concurrentes a `POST member_contribution`.
6. Notifica el éxito a la pantalla cuando terminan las peticiones.

El reparto actual es igualitario y no utiliza los ingresos que guarda Splitly Member. El cliente tampoco aplica un ajuste explícito de redondeo a centavos. La cadena de peticiones no es una transacción: un error puede dejar recursos creados parcialmente, por lo que debe revisarse el estado del servidor antes de reintentar.

## Revisión de pagos y resumen

El miembro solicita revisión desde Splitly Member mediante `PUT member_contribution/{id}/request`. `DashboardViewModel` recorre los hogares del representante, sus miembros y sus cuotas para construir los totales y la lista de aprobaciones.

- `done`, `paid` y `approved` se consideran pagados, sin distinguir mayúsculas.
- Los estados que contienen `review` o `request` aparecen como solicitudes de aprobación.
- El resto se suma al importe pendiente. Las solicitudes en revisión quedan separadas de ese total.

La aprobación utiliza `PUT member_contribution/{id}/approve` y después recarga el resumen. El servidor debe validar que el representante pueda aprobar esa cuota.

## Preferencias y límites actuales

`SettingsViewModel` consulta y actualiza preferencias remotas. El tema global `SplitlyTheme` sigue `isSystemInDarkTheme()` y no recibe el ajuste remoto. Tampoco hay integración de notificaciones push. Existen utilidades de traducción, pero las pantallas actuales conservan textos literales.

Las pruebas de plantilla no cubren las operaciones de negocio. Al cambiar contratos, estados de cuota o cálculos, verifica el flujo completo con Splitly Member y un backend de prueba, además de compilar, ejecutar lint y correr las pruebas disponibles.
