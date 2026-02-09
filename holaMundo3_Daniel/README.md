# Consumir un Servicio Web RESTful con Spring Boot

---

## **ÍNDICE DE CONTENIDOS**

| Sección | Contenido |
|---------|-----------|
| **1. [Resumen Ejecutivo](#1-resumen-ejecutivo)** | Qué hace este proyecto |
| **2. [Conceptos Fundamentales](#2-conceptos-fundamentales-de-spring)** | `@Bean` y `@Profile` explicados |
| **3. [Secuencia de Inicio](#3-secuencia-de-inicio-completa)** | Flujo de arranque |
| **4. [Estructura de Archivos del Proyecto](#4-estructura-de-archivos-del-proyecto)** | Diagramas y estructura |
| **5. [Código Fuente Detallado](#5-código-fuente-detallado)** | Implementación completa |
| **6. [Guía de Ejecución](#6-guía-de-ejecución-paso-a-paso)** | Cómo ejecutar el proyecto |

---

## **1. RESUMEN EJECUTIVO**

### **¿Qué hace este proyecto?**

Este proyecto implementa un **cliente REST** en Spring Boot con las siguientes características:

```
┌─────────────────────────────────────────────┐
│  AL ARRANCAR LA APLICACIÓN                  │
├─────────────────────────────────────────────┤
│  1. Realiza GET a /api/random               │
│  2. Recibe un JSON con una cita inspiradora │
│  3. Mapea automáticamente el JSON a objetos │
│  4. Muestra el resultado en la consola      │
└─────────────────────────────────────────────┘
```

### **Características principales**

- Cliente REST con `RestClient` (moderno)
- Mapeo automático JSON ↔ Java con Jackson
- Configuración por perfiles (`@Profile`)
- Ejecución automática al arrancar (`ApplicationRunner`)

---

## **2. CONCEPTOS FUNDAMENTALES DE SPRING**

---

### **¿Qué es `@Bean`?**

#### **Definición**

`@Bean` es una anotación que indica a Spring que debe **gestionar un objeto** como un componente reutilizable.

#### **¿Para qué sirve?**

| Característica | Descripción |
|----------------|-------------|
| **Creación única** | Spring crea el objeto **una sola vez** |
| **Contenedor central** | Lo guarda en el **ApplicationContext** |
| **Inyección automática** | Lo proporciona donde se necesite |
| **Ciclo de vida gestionado** | Spring controla creación y destrucción |

#### **Ejemplo práctico**

```java
@SpringBootApplication
public class HolaMundoApplication {
    
    // DEFINICIÓN DEL BEAN
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
        // Spring guarda este objeto y lo reutiliza
    }

    // USO DEL BEAN (inyección automática)
    @Bean
    public ApplicationRunner runner(RestClient.Builder builder) {
        // ⬆️ Spring inyecta automáticamente el Builder
        return args -> {
            RestClient client = builder
                .baseUrl("http://localhost:8080")
                .build();
        };
    }
}
```

#### **Flujo de inyección de dependencias**

```
┌─────────────────────────────────────────────────┐
│  1️⃣ Spring encuentra @Bean restClientBuilder()  │
│       ↓                                          │
│  2️⃣ Crea el RestClient.Builder                  │
│       ↓                                          │
│  3️⃣ Lo guarda en el ApplicationContext          │
│       ↓                                          │
│  4️⃣ Ve que runner() necesita un Builder         │
│       ↓                                          │
│  5️⃣ Inyecta automáticamente el Builder          │
└─────────────────────────────────────────────────┘
```

---

### **¿Qué es `@Profile`?**

#### **Definición**

`@Profile` permite **activar o desactivar componentes** según el **entorno de ejecución**.

#### **¿Para qué sirve?**

Gestionar diferentes configuraciones para distintos entornos:

```
┌──────────────┬─────────────────────────────────┐
│   ENTORNO    │          NECESIDAD              │
├──────────────┼─────────────────────────────────┤
│ Desarrollo   │ Mock local, logs detallados     │
│ Testing      │ Base de datos H2, sin runners   │
│ Producción   │ BD real, servicios externos     │
└──────────────┴─────────────────────────────────┘
```

#### **Sintaxis completa**

```java
// Activo SOLO con perfil "mock"
@Profile("mock")
public class ApiRandomController { ... }

// Activo en TODOS excepto "test"
@Profile("!test")
public ApplicationRunner runner() { ... }

// Activo con "dev" O "local"
@Profile({"dev", "local"})
public class DevConfig { ... }

// Activo con "prod" Y "cloud"
@Profile("prod & cloud")
public class ProdConfig { ... }
```

#### **Casos de uso comunes**

| Perfil | Cuándo usar | Ejemplo de componente |
|--------|-------------|----------------------|
| `mock` | Desarrollo sin servicios externos | Mock de API REST |
| `!test` | Todas las situaciones excepto tests | ApplicationRunner |
| `dev` | Desarrollo local | Logs detallados, datos de prueba |
| `prod` | Producción | Conexiones a BD real, cache |
| `integration` | Tests de integración | Servicios externos reales |

#### **Cómo activar perfiles**

**Opción 1: En línea de comandos (Maven)**
```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mock
```

**Opción 2: Con JAR compilado**
```powershell
java -jar target\hola_mundo-0.0.1-SNAPSHOT.jar --spring.profiles.active=mock
```

**Opción 3: En `application.properties`**
```properties
spring.profiles.active=mock
```

**Opción 4: Variable de entorno**
```powershell
$env:SPRING_PROFILES_ACTIVE="mock"
.\mvnw.cmd spring-boot:run
```

#### **Verificar perfiles activos**

En los logs de arranque busca:
```
The following profiles are active: mock
```

---

## 3. **Secuencia de Inicio Completa**

```
╔══════════════════════════════════════════════════════════╗
║            ARRANQUE DE SPRING BOOT                       ║
╚══════════════════════════════════════════════════════════╝

1️⃣  INICIO
    │
    └─→ SpringApplication.run() ejecutado
    
2️⃣  ESCANEO DE COMPONENTES
    │
    ├─→ @SpringBootApplication encontrado
    ├─→ Busca @Bean, @Component, @Controller, etc.
    └─→ Verifica perfiles activos (@Profile)
    
3️⃣  CREACIÓN DE BEANS (orden de dependencias)
    │
    ├─→ RestClient.Builder bean
    │   └─→ Guardado en ApplicationContext
    │
    └─→ ApplicationRunner bean
        ├─→ Necesita RestClient.Builder
        ├─→ Spring inyecta el Builder
        └─→ Bean listo
    
4️⃣  REGISTRO DE CONTROLADORES
    │
    ├─→ ApiRandomController
    │   └─→ Mapping: GET /api/random
    │
    └─→ GreetingController
        └─→ Mapping: GET /greeting
    
5️⃣  EJECUCIÓN DE RUNNERS
    │
    └─→ ApplicationRunner.run()
        │
        ├─→ Construye RestClient
        ├─→ Hace GET /api/random
        ├─→ Jackson mapea JSON → Quote
        └─→ log.info(quote)
    
6️⃣  APLICACIÓN LISTA 
    │
    └─→ Tomcat started on port 8080
```

---

## **4. ESTRUCTURA DE ARCHIVOS DEL PROYECTO**

---

### **Árbol de Directorios**

```
hola_mundo/
│
├── 📂 src/
│   ├── 📂 main/
│   │   ├── 📂 java/
│   │   │   └── 📂 hola/mundo/hola_mundo/
│   │   │       │
│   │   │       ├── 📄 HolaMundoApplication.java
│   │   │       │   ├── 🔷 main() - Punto de entrada
│   │   │       │   ├── 🔷 @Bean RestClient.Builder
│   │   │       │   └── 🔷 @Bean ApplicationRunner
│   │   │       │
│   │   │       ├── 📂 controladores/
│   │   │       │   ├── 📄 ApiRandomController.java
│   │   │       │   │   └── 🔶 Endpoint Rest (GET /api/random)
│   │   │       │   │
│   │   │       │   └── 📄 GreetingController.java
│   │   │       │       └── 🔶 Endpoint simple (GET /greeting)
│   │   │       │
│   │   │       └── 📂 modelos/
│   │   │           ├── 📄 Quote.java
│   │   │           │   └── 🔶 DTO principal (type + value)
│   │   │           │
│   │   │           └── 📄 Value.java
│   │   │               └── 🔶 DTO embebido (id + quote)
│   │   │
│   │   └── 📂 resources/
│   │       ├── 📄 application.properties
│   │       └── 📄 static/ & templates/ (si los hay)
│   │
│   └── 📂 test/
│       └── 📂 java/ (tests unitarios)
│
├── 📄 pom.xml
├── 📄 mvnw.cmd (Maven Wrapper para Windows)
└── 📄 mvnw (Maven Wrapper para Linux/Mac)
```

---

### **Descripción de Archivos Clave**

| Archivo | Responsabilidad | Anotaciones clave |
|---------|----------------|-------------------|
| **HolaMundoApplication.java** | Configuración principal y punto de entrada | `@SpringBootApplication`, `@Bean` |
| **ApiRandomController.java** | Endpoint para `/api/random` | `@RestController`, `@GetMapping` |
| **GreetingController.java** | Endpoint de prueba simple | `@RestController`, `@GetMapping` |
| **Quote.java** | DTO para respuesta completa | `@JsonIgnoreProperties` |
| **Value.java** | DTO para cita embebida | `@JsonProperty` (si es necesario) |
| **application.properties** | Configuración de la app | `spring.profiles.active`, etc. |

---

## **5. CÓDIGO FUENTE DETALLADO**

---

### **HolaMundoApplication.java**

```java
package hola.mundo.hola_mundo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;
import hola.mundo.hola_mundo.dto.Quote;

@SpringBootApplication
public class HolaMundoApplication {

    // Logger para registrar información
    private static final Logger log = LoggerFactory
            .getLogger(HolaMundoApplication.class);

    // Punto de entrada de la aplicación
    public static void main(String[] args) {
        SpringApplication.run(HolaMundoApplication.class, args);
    }

    // ═══════════════════════════════════════════════════════
    //              CONFIGURACIÓN DE BEANS
    // ═══════════════════════════════════════════════════════

    /**
     * 🔷 BEAN 1: RestClient.Builder
     *
     * Propósito:
     *   - Proporciona un constructor de RestClient
     *   - Spring lo gestiona y lo inyecta donde se necesite
     *
     * ¿Por qué usar @Bean?
     *   - Spring crea UNA SOLA instancia
     *   - La reutiliza en toda la aplicación
     *   - Gestiona su ciclo de vida
     */
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    /**
     * 🔷 BEAN 2: ApplicationRunner
     *
     * Propósito:
     *   - Se ejecuta automáticamente AL ARRANCAR la app
     *   - Consume el servicio REST /api/random
     *   - Registra el resultado en el log
     *
     * @Profile("!test"):
     *   - Solo se activa si el perfil NO es "test"
     *   - Evita que se ejecute durante tests
     *
     * Parámetro builder:
     *   - Spring INYECTA automáticamente el RestClient.Builder
     *   - Es el bean que definimos arriba
     */
    @Bean
    @Profile("!test")  // ← NO ejecutar en tests
    public ApplicationRunner runner(RestClient.Builder builder) {
        return args -> {

            // ─────────────────────────────────────────────
            //   PASO 1: Construir el cliente REST
            // ─────────────────────────────────────────────
            RestClient restClient = builder
                    .baseUrl("http://localhost:8080")
                    .build();

            // ─────────────────────────────────────────────
            //   PASO 2: Realizar petición GET
            // ─────────────────────────────────────────────
            Quote quote = restClient
                    .get()                    // Tipo de petición
                    .uri("/api/random")       // Endpoint
                    .retrieve()               // Ejecutar
                    .body(Quote.class);       // Mapear a Quote

            // ─────────────────────────────────────────────
            //   PASO 3: Registrar resultado
            // ─────────────────────────────────────────────
            Sistem.out.printls("Cita recibida: {}", quote);
        };
    }
}
```

---

### **ApiRandomController.java**

```java
package hola.mundo.hola_mundo.controladores;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import hola.mundo.hola_mundo.dto.Quote;
import hola.mundo.hola_mundo.dto.Value;

/**
 * 🔶 CONTROLADOR MOCK LOCAL
 *
 * Propósito:
 *   - Simula el servicio externo "quoters"
 *   - Devuelve una cita hardcodeada en memoria
 *   - Útil para desarrollo sin dependencias externas
 *
 * @Profile("mock"):
 *   - Solo se activa si el perfil "mock" está activo
 *   - Para activarlo: --spring.profiles.active=mock
 *
 * @RestController:
 *   - Combina @Controller + @ResponseBody
 *   - Devuelve JSON automáticamente
 */
@RestController
public class ApiRandomController {

    /**
     * Endpoint: GET /api/random
     *
     * Devuelve:
     *   {
     *     "type": "success",
     *     "value": {
     *       "id": 10,
     *       "quote": "Really loving Spring Boot..."
     *     }
     *   }
     */
    @GetMapping("/api/random")
    public Quote randomQuote() {
        int id = new Random().nextInt(100);
        return new Quote("success", new Value(id, "Really loving Spring Boot, makes stand alone Spring apps easy."));
    }
}
```


---

### **Quote.java** (DTO Principal)

```java
package hola.mundo.hola_mundo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 🔶 DTO PRINCIPAL
 *
 * Mapea el JSON completo de respuesta:
 *   {
 *     "type": "success",
 *     "value": { ... }
 *   }
 *
 * @JsonIgnoreProperties(ignoreUnknown = true):
 *   - Ignora propiedades JSON que no están en esta clase
 *   - Evita errores si el JSON tiene campos extra
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Quote(String type, Value value) {
}
```

---

### **Value.java** (DTO Embebido)

```java
package hola.mundo.hola_mundo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 🔶 DTO EMBEBIDO
 *
 * Mapea la cita dentro del JSON:
 *   "value": {
 *     "id": 10,
 *     "quote": "Really loving Spring Boot..."
 *   }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Value(Integer id, String quote) {
}
```

---

### 📄 **Formato JSON Esperado**

```json
{
  "type": "success",
  "value": {
    "id": 10,
    "quote": "Really loving Spring Boot, makes stand alone Spring apps easy."
  }
}
```
---

## **6. GUÍA DE EJECUCIÓN PASO A PASO**


#### **TERMINAL 1: Arrancar el Consumidor (`hola_mundo`)**

```powershell
# ───────────────────────────────────────────────────
#   PASO 1: Ir al directorio del proyecto
# ───────────────────────────────────────────────────
cd ./hola_mundo

# ───────────────────────────────────────────────────
#   PASO 2: Compilar el proyecto
# ───────────────────────────────────────────────────
.\mvnw.cmd clean package

# ───────────────────────────────────────────────────
#   PASO 3: Ejecutar la aplicación
# ───────────────────────────────────────────────────
.\mvnw.cmd spring-boot:run
```

**Log esperado en consola:**
```
Quote[type=success, value=Value[id=81, quote=Really loving Spring Boot, makes stand alone Spring apps easy.]]
```

---


#### **Probar Endpoints**

**Con PowerShell:**
```powershell
# Endpoint de la aplicación consumidora
curl http://localhost:8080/api/random
```

**Con Postman:**

| Método | URL | Resultado esperado |
|--------|-----|-------------------|
| GET | `http://localhost:8080/api/random` | JSON con cita |

**En el Navegador:**
- Visitar: `http://localhost:8080/api/random`
---
