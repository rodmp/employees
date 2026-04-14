# employees

API REST de empleados (Spring Boot 3, Java 17, JPA). Por defecto habla con PostgreSQL; en tests usa H2 en memoria. Los DTO van como *records* en el paquete `model`.

Código y comentarios de aplicación en **inglés**; este README en español para quien despliega o entrega la prueba.

---

## Qué necesitas

- JDK **17** (el repo trae `.java-version` por si usas asdf/jEnv).
- Maven **3.9+**
- Docker solo si vas a levantar Postgres o Sonar con los compose de `docs/`.

---

## Base de datos

En local la app lee `src/main/resources/application.properties`: usuario `test`, contraseña `test123`, base `invex` en `localhost:5432`. Puedes sustituir la URL con la variable de entorno `DATABASE_URL` (MySQL, Oracle u otro JDBC si añades el driver).

En tests (`perfil test`) todo va a H2; los datos semilla están en `src/test/resources/data.sql`.

Hibernate está con `ddl-auto=update` para desarrollo. En producción conviene `validate` o migraciones (Flyway/Liquibase).

### Postgres con Docker

Desde `docs/` (copia o revisa `docs/.env`):

```bash
docker compose up -d --build
```

El compose usa **Postgres 16** fija (evita sorpresas con `postgres:latest` y volúmenes viejos). Si ya te falló antes por datos incompatibles:

```bash
docker compose down
rm -rf postgres/data
docker compose up -d --build
```

---

## Arranque local (solo Maven)

Con Postgres arriba:

```bash
mvn spring-boot-run
```

- API: `http://localhost:8081`
- Swagger: `http://localhost:8081/swagger-ui/index.html`
- OpenAPI YAML: `http://localhost:8081/v3/api-docs.yaml`
- Las rutas bajo `/employees` exigen **HTTP Basic**; credenciales y detalle en [Seguridad de la API](#seguridad-de-la-api).

---

## Seguridad de la API

La app usa **HTTP Basic** con **sesión stateless** (sin cookie de sesión) y **CSRF desactivado**, típico en una API REST. La cadena está en `src/main/java/com/invex/employees/config/SecurityConfiguration.java`.

**Sin credenciales** puedes llamar a salud y documentación: `/actuator/health`, `/actuator/health/**`, `/v3/api-docs/**`, `/swagger-ui/**` y `/swagger-ui.html`. **Todo lo demás** (incluido `/employees/**`) requiere usuario y contraseña válidos.

El usuario en memoria lo define Spring Boot con `spring.security.user.name` y `spring.security.user.password` en [`application.properties`](src/main/resources/application.properties). En este repo el valor por defecto es usuario **`invex`** y contraseña **`invex123`** (solo para desarrollo; en producción usa secretos reales y no los subas al repositorio).

Para sobrescribir en despliegue puedes usar variables de entorno estándar: `SPRING_SECURITY_USER_NAME` y `SPRING_SECURITY_USER_PASSWORD`.

Ejemplo de listado con `curl`:

```bash
curl -s -u invex:invex123 http://localhost:8081/employees
```

**Swagger UI** se abre sin login, pero si usas *Try it out* contra `/employees` recibirás **401** hasta que configures autenticación: botón **Authorize** y credenciales Basic (mismo usuario y contraseña que en propiedades).

En **Postman**, en la colección [`docs/invex.postman_collection.json`](docs/invex.postman_collection.json), añade **Authorization → Basic Auth** con el mismo usuario y contraseña (además del header `transaction-id` que ya lleva la colección).

---

## Docker (solo la API)

En la raíz del proyecto:

```bash
docker build -t employees-api:latest .
docker run --rm -p 8081:8081 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/invex \
  employees-api:latest
```

En Linux sustituye `host.docker.internal` por la IP del host o usa la misma red Docker que Postgres.

---

## API (resumen)

Todo bajo **`/employees`** (cada fila requiere **HTTP Basic**; ver [Seguridad de la API](#seguridad-de-la-api)):

| Método | Ruta | Notas |
|--------|------|--------|
| GET | `/employees` | Listado |
| GET | `/employees/{id}` | Detalle |
| POST | `/employees` | Cuerpo `{"employees":[{...}]}` (uno o varios) → 201 |
| PUT | `/employees/{id}` | Actualización parcial permitida |
| DELETE | `/employees/{id}` | 204 si borra |
| GET | `/employees/search?name=` | Búsqueda parcial |

---

## Pruebas y cobertura

```bash
mvn test          # solo tests
mvn verify        # tests + JaCoCo + umbral de líneas ≥ 90 %
```

Informe HTML: `target/site/jacoco/index.html`.

JaCoCo y Sonar **no cuentan** cobertura en `domain` ni `model` (entidad + DTOs); el `pom` también excluye tests bajo esos paths en Surefire. El resto del código sí entra en la métrica.

---

## CI/CD y Sonar

Hay dos caminos equivalentes:

- **GitHub Actions**: [.github/workflows/ci.yml](.github/workflows/ci.yml) — en `push`/`PR` a `main`, `master` o `develop`: JDK 17, `mvn verify`, si existe el secret `SONAR_TOKEN` entonces `sonar:sonar`, y `docker build`.
- **Jenkins**: [Jenkinsfile](Jenkinsfile) — mismo flujo; hace falta un credential de texto `sonar-token` y el JDK/Maven configurados en Jenkins con los nombres del archivo (`JDK-17`, `Maven-3.9`). Crea el job como **Pipeline script from SCM** (repositorio Git + rama + ruta al `Jenkinsfile`). Si ves *not in a git directory*, desactiva **Lightweight checkout** en la sección Git del job (o deja el `Jenkinsfile` actual: hace clone explícito del repo y no depende solo del workspace ligero). Si Jenkins corre **en Docker** y Sonar en tu máquina, no configures la URL de Sonar como `http://localhost:9001` (desde el contenedor “localhost” es el propio Jenkins): usa `http://host.docker.internal:9001` en el parámetro del job, en la variable `SONAR_HOST_URL` del [compose de Jenkins](docs/jenkins/docker-compose.yml) o en *Manage Jenkins → System → SonarQube servers*. El stage **Docker image** necesita el cliente Docker y el socket del host: levanta Jenkins con [docs/jenkins/docker-compose.yml](docs/jenkins/docker-compose.yml) (`docker compose build --no-cache && docker compose up -d`). El compose usa **`user: root`** en desarrollo para evitar *permission denied* al socket; si prefieres no usar root, comenta esa línea, descomenta `group_add` en el mismo archivo y ejecuta [docs/jenkins/gen-env.sh](docs/jenkins/gen-env.sh) para generar `.env` con `DOCKER_GID`.

Sonar en local: `docs/sonarqube/docker-compose.yaml` (UI en `http://localhost:9001`, mapeo host **9001** → contenedor **9000**). Crea el proyecto con la misma clave que `sonar.projectKey` en el `pom` (`invex-employees`), genera un token de usuario y pásalo **solo por entorno o por línea de comandos**, nunca en el repositorio:

```bash
export SONAR_TOKEN="<pega_aqui_tu_token>"
mvn -B verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9001 \
  -Dsonar.qualitygate.wait=true
```

Alternativa equivalente: `-Dsonar.token=...` en lugar del export.

Si Sonar dice *Not authorized*, falta token o la URL no apunta a tu servidor. En **SonarCloud** además suele hacer falta `-Dsonar.organization=...` (puedes ponerla como variable `SONAR_ORGANIZATION` en GitHub o en el job de Jenkins).

---

## Carpeta `docs`

| Ruta | Para qué |
|------|----------|
| `docker-compose.yml` | Postgres + opción de build de la API |
| `postgres/init.sql` | DDL/datos iniciales del contenedor |
| `sonarqube/docker-compose.yaml` | SonarQube LTS para análisis local |
| `api-docs.yaml` | Contrato OpenAPI de referencia |
| `invex.postman_collection.json` | Colección Postman |

Los pipelines (GitHub/Jenkins) viven en la **raíz** del repo, no dentro de `docs/`.

---

## Dependencias principales

Spring Web, Spring Security, Data JPA, Validation, Actuator, springdoc-openapi, PostgreSQL (runtime), H2 (tests), Lombok.

_Documentación actualizada: 2026-04-13._
