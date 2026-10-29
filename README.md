# CORPALIV App

Aplicación móvil Android para **CORPALIV** (Corporación de Padres y Amigos por el Limitado Visual) y su red sociolaboral **Alba Lab**. Su objetivo es que donantes, empresas y colaboradores conozcan la organización y pasen del interés a una solicitud de colaboración concreta: donación, alianza, voluntariado, compra de productos o capacitación.

Proyecto de la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles** (DUOC UC), Evaluación Parcial 2.

## Integrantes

- Alejandro Cuitiño
- Maximiliano Córdova

## Funcionalidades implementadas

| Requisito | Qué hace la app |
|---|---|
| Diseño visual con Material 3 | Tema propio con la paleta de corpaliv.cl (violeta, magenta y amarillo), tipografía Poppins + Roboto y formas redondeadas. Sin colores dinámicos, para mantener la marca. |
| Navegación funcional | Splash → Login → Inicio. Barra inferior con 5 secciones (Inicio, Programas, Catálogo, Colaborar, Perfil) y pantallas secundarias (Registro, Solicitud de interés, Mis solicitudes, Sedes) con botón para volver. |
| Formularios validados | **Registro**: nombre (String), fecha de nacimiento (Date) con la edad (Int) calculada automáticamente, tipo de cuenta, clave y confirmación, checkbox de términos, switch de novedades y botón. **Login**. **Solicitud de interés**: tipo de colaboración, monto (Int), fecha de contacto y mensaje. Cada campo muestra un ícono y un mensaje de error o de válido. |
| Validaciones en la lógica | Reglas en `domain/validation/Validators.kt`, separadas de la interfaz y cubiertas con pruebas unitarias. Ejemplo: la edad se calcula desde la fecha de nacimiento y debe estar entre 18 y 110 años. |
| Gestión de estado | Patrón MVVM: cada pantalla tiene un ViewModel que expone su estado con `StateFlow`. El estado se conserva al rotar la pantalla. |
| Almacenamiento local | **Room** (SQLite) guarda usuarios y solicitudes; **DataStore** recuerda la sesión. La clave se guarda cifrada con PBKDF2 y sal aleatoria, nunca en texto plano. |
| Recursos nativos | **Cámara**: foto de perfil, guardada en el almacenamiento privado de la app. **Ubicación aproximada**: muestra la sede más cercana. Ambos piden permiso en tiempo de ejecución y explican para qué se usa si el usuario lo rechaza. |
| Animaciones | Transiciones entre pantallas, Splash animado, campos que tiemblan al enviar un formulario con errores, mensajes de error que aparecen y desaparecen animados, y listas animadas al eliminar o reordenar. |

Todos los datos de prueba (usuarios, sedes, montos) son **ficticios**, como exige el caso.

## Tecnologías

- Kotlin + Jetpack Compose + Material 3
- Navigation Compose
- ViewModel + StateFlow (MVVM)
- Room (SQLite) y DataStore Preferences
- Coroutines y Flow
- JUnit (pruebas unitarias) y AndroidJUnit4 (pruebas instrumentadas)

## Estructura del proyecto

```
app/src/main/java/cl/duoc/corpaliv/
├── MainActivity.kt          Punto de entrada
├── CorpalivApplication.kt   Crea el contenedor de dependencias
├── ui/
│   ├── screens/             Pantallas (Composables), una carpeta por sección
│   ├── components/          Componentes reutilizables: campos, barra inferior, animaciones, permisos
│   ├── theme/               Colores, tipografía y formas
│   └── util/                Formato de fechas y montos
├── viewmodel/               Un ViewModel por pantalla
├── data/
│   ├── model/               Usuario, Solicitud, Sede
│   ├── repository/          Acceso a datos para los ViewModels
│   ├── local/               Room: base de datos, entidades y DAOs
│   ├── session/             DataStore de la sesión
│   ├── location/            Ubicación y sedes de ejemplo
│   ├── storage/             Foto de perfil
│   └── di/                  AppContainer (inyección de dependencias manual)
├── domain/                  Lógica pura: validaciones, cifrado de la clave y cálculo de distancia
└── navigation/              Rutas y NavHost
```

## Pasos para ejecutar

### Requisitos

- **Android Studio 2026.2** o superior (el proyecto usa Android Gradle Plugin 9.4.1).
- Un emulador o teléfono con **Android 8.0 (API 26)** o superior.

### Ejecutar la app

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/DrayBox/CORPALIV_PROJECT.git
   ```
2. En Android Studio: **File → Open** y elegir la carpeta clonada.
3. Esperar a que termine la sincronización de Gradle (la primera vez descarga las dependencias).
4. Elegir un emulador o teléfono y presionar **Run ▶**.
5. En la app: **Crear cuenta**, completar el registro con datos ficticios y entrar.

Para probar la ubicación en el emulador: *Extended controls (···) → Location*, elegir un punto en Santiago y luego **Sedes → Usar mi ubicación**.

### Ejecutar las pruebas

```bash
./gradlew testDebugUnitTest
```
Pruebas unitarias de validaciones, cifrado de la clave, cálculo de distancia y conversión de datos.

```bash
./gradlew connectedDebugAndroidTest
```
Pruebas instrumentadas de los repositorios con Room. Necesitan un emulador encendido.

## Planificación

Tareas y su distribución en Trello: [CORPALIV App – DSY1105](https://trello.com/b/B9LgpYJa/corpaliv-app-dsy1105).
