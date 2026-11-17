# Configuración del diagnóstico

## Selección del proyecto de IA

Las dos configuraciones están disponibles. El proyecto que procesa las solicitudes
a Gemini se selecciona al compilar mediante `plantcare.aiProject`:

```properties
# Valor actual en gradle.properties:
plantcare.aiProject=plantcare-ia
```

- `plantcare-ia`: proyecto separado PlantCare IA, plan Spark y cuota gratuita disponible.
  Configuración pública del cliente Android en `app/src/main/assets/firebase_ai.json`.
- `plantcare`: proyecto principal PlantCare (`plantcare-3a58c`), usando las opciones
  de `app/google-services.json`. Está en Blaze y Gemini está sujeto a pago por uso;
  puede requerir saldo de prepago.

Para cambiar explícitamente en una compilación:

```powershell
.\gradlew.bat :app:assembleDebug -Pplantcare.aiProject=plantcare
```

Cambiar esta opción exige recompilar y reiniciar la app. No se cambia de proyecto
automáticamente cuando hay un error o se agota la cuota. Una opción inválida hace fallar
la compilación; una configuración ausente o incorrecta no recurre al proyecto de pago.

## Datos de la app

Authentication, Firestore y Storage siguen siempre en el Firebase principal PlantCare,
con sus emuladores locales en debug. La selección anterior solo afecta a la IA.
No reemplazar `app/google-services.json` con el archivo del proyecto de IA.

Características: `usuarios/{uid}/caracteristicas/{plantId}`.
Resultados: `usuarios/{uid}/plantas/{plantId}/diagnosticos/{diagnosticId}`.
La foto se envía al proveedor al pulsar Analizar. El resultado se guarda al pulsar Guardar diagnóstico.
Los reintentos mantienen ID y fecha para evitar duplicados.

## App Check

1. Abrir Firebase AI Logic en el proyecto elegido y habilitar Gemini Developer API si hace falta.
2. En Seguridad > App Check > Apps, localizar la app `plat.plantcare.app` de ese proyecto.
3. Ejecutar la versión debug actualizada y buscar `Firebase App Check debug token:` en Logcat.
4. En el menú de tres puntos de la app, abrir Administrar tokens de depuración y registrar
   el token de esa instalación. Los registros de tokens son independientes por proyecto.
5. Reiniciar la app. `FirebaseAiSetup` indica el ID del proyecto para el que App Check se autoriza.
6. Para release, configurar Play Integrity y la firma SHA-256 correspondiente en el proyecto elegido.

App Check se prepara al iniciar debug sin consultar Gemini ni enviar fotos.
No compartir tokens ni subirlos al repositorio.

## Código compartido

- `core/ai/firebase/AiProjectConfiguration.kt`: selección explícita de opciones.
- `core/ai/firebase/FirebaseAiApp.kt`: instancia SDK de IA con Authentication aislado.
- `core/ai/gemini/GeminiClient.kt`: creación de modelos y solicitudes de imagen/texto.
- `core/data/firebase/FirestoreFlows.kt`: observadores con limpieza de listeners.
- `diagnosis/application`: casos de uso y composición Hilt.
- `diagnosis/data/gemini`: prompt, esquema y validación de la respuesta específica de plantas.

El modelo predeterminado es `gemini-3.8-flash`, configurable con `-Pplantcare.geminiModel=NOMBRE`.
Las pruebas de selección leen ambas configuraciones sin enviar solicitudes a Gemini.
