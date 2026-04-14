// Created 2026-04-13
// Sonar en el host + Jenkins en Docker: NO uses http://localhost:9001 en "Manage Jenkins > System > SonarQube servers"
// ni como parámetro por defecto; localhost es el contenedor. Usa http://host.docker.internal:9001 (Linux: extra_hosts en docs/jenkins/docker-compose.yml).
//
// Clone explícito (no solo checkout scm): si en el job tienes "Lightweight checkout" para leer el Jenkinsfile,
// el workspace puede no ser un repo git completo y aparecer "not in a git directory".
pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
    }

    parameters {
        string(
                name: 'GIT_BRANCH',
                defaultValue: 'main',
                description: 'Rama a compilar (ej. main o master).'
        )
        string(
                name: 'GIT_CREDENTIALS_ID',
                defaultValue: '',
                description: 'Solo si el repo es privado: ID de credencial Jenkins (usuario/token GitHub). Déjalo vacío en repos públicos.'
        )
        booleanParam(name: 'RUN_SONAR', defaultValue: true, description: 'Run Sonar (needs credential id sonar-token)')
        booleanParam(
                name: 'SONAR_WAIT_QUALITY_GATE',
                defaultValue: true,
                description: 'Si está activo, el build falla cuando la Quality Gate de Sonar no pasa. Desactívalo solo mientras corriges hallazgos en el dashboard de Sonar (el análisis igual se sube).'
        )
        string(
                name: 'SONAR_HOST_URL',
                defaultValue: 'http://host.docker.internal:9001',
                description: 'URL de SonarQube. Por defecto sirve con Jenkins en Docker y Sonar en el host. Si Jenkins está instalado en el mismo SO que Sonar (sin Docker), usa http://127.0.0.1:9001.'
        )
        booleanParam(
                name: 'BUILD_DOCKER_IMAGE',
                defaultValue: true,
                description: 'Construir imagen Docker de la API al final del pipeline.'
        )
        booleanParam(
                name: 'DEPLOY_CONTAINER',
                defaultValue: false,
                description: 'Levantar/actualizar contenedor Docker con la imagen construida en este build.'
        )
        string(
                name: 'DOCKER_CONTAINER_NAME',
                defaultValue: 'employees-api',
                description: 'Nombre del contenedor a desplegar.'
        )
        string(
                name: 'DOCKER_HOST_PORT',
                defaultValue: '8081',
                description: 'Puerto del host para publicar la API (contenedor escucha en 8080).'
        )
        string(
                name: 'DATABASE_URL',
                defaultValue: 'jdbc:postgresql://postgresql:5432/invex',
                description: 'JDBC URL para el contenedor desplegado (si Postgres corre en compose, usa el servicio postgresql).'
        )
        string(
                name: 'DATABASE_USERNAME',
                defaultValue: 'test',
                description: 'Usuario de base de datos para el contenedor desplegado.'
        )
        string(
                name: 'DOCKER_NETWORK',
                defaultValue: 'docs_default',
                description: 'Red Docker para conectar el contenedor (ej. docs_default si Postgres corre en docs/docker-compose.yml).'
        )
        password(
                name: 'DATABASE_PASSWORD',
                defaultValue: 'test123',
                description: 'Password de base de datos para el contenedor desplegado.'
        )
        booleanParam(
                name: 'CLEAN_DOCKER_IMAGES',
                defaultValue: true,
                description: 'Eliminar imagen del build al terminar para no acumular tags locales.'
        )
    }

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }

    environment {
        PATH = "/usr/local/bin:/usr/bin:/bin:/usr/local/sbin:/usr/sbin:/sbin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    deleteDir()
                    def repoUrl = 'https://github.com/rodmp/employees.git'
                    def urc = [url: repoUrl]
                    if (params.GIT_CREDENTIALS_ID?.trim()) {
                        urc.credentialsId = params.GIT_CREDENTIALS_ID.trim()
                    }
                    checkout([
                            $class           : 'GitSCM',
                            branches         : [[name: "*/${params.GIT_BRANCH}"]],
                            userRemoteConfigs: [urc]
                    ])
                }
            }
        }

        stage('Unit tests + JaCoCo') {
            steps {
                sh 'mvn -B -ntp clean verify'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube') {
            when {
                expression { return params.RUN_SONAR }
            }
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    script {
                        // Env del contenedor (docs/jenkins/docker-compose) > parámetro del job > host.docker.internal (evita localhost dentro de Docker).
                        def sonarUrl = (env.SONAR_HOST_URL?.trim()
                                ?: params.SONAR_HOST_URL?.trim()
                                ?: 'http://host.docker.internal:9001')
                        echo "Sonar analysis using host URL: ${sonarUrl}"
                        def qgWait = params.SONAR_WAIT_QUALITY_GATE ? 'true' : 'false'
                        def cmd = "mvn -B -ntp sonar:sonar -Dsonar.host.url=${sonarUrl} -Dsonar.token=${env.SONAR_TOKEN} -Dsonar.qualitygate.wait=${qgWait}"
                        if (env.SONAR_ORGANIZATION?.trim()) {
                            cmd += " -Dsonar.organization=${env.SONAR_ORGANIZATION}"
                        }
                        sh cmd
                    }
                }
            }
        }

        stage('Docker image') {
            when {
                expression { return params.BUILD_DOCKER_IMAGE }
            }
            steps {
                sh """
                    set -e
                    docker --version
                    DOCKER_BUILDKIT=0 docker build -t employees-api:build-${env.BUILD_NUMBER} .
                """
            }
        }

        stage('Deploy container') {
            when {
                expression { return params.BUILD_DOCKER_IMAGE && params.DEPLOY_CONTAINER }
            }
            steps {
                sh """
                    set -e
                    if docker ps -a --format '{{.Names}}' | grep -Fxq "${params.DOCKER_CONTAINER_NAME}"; then
                      docker rm -f "${params.DOCKER_CONTAINER_NAME}"
                    fi
                    NET_ARG=""
                    if [ -n "${params.DOCKER_NETWORK}" ]; then
                      NET_ARG="--network ${params.DOCKER_NETWORK}"
                    fi
                    docker run -d \
                      --name "${params.DOCKER_CONTAINER_NAME}" \
                      \$NET_ARG \
                      --add-host host.docker.internal:host-gateway \
                      -p "${params.DOCKER_HOST_PORT}:8081" \
                      -e DATABASE_URL="${params.DATABASE_URL}" \
                      -e SPRING_DATASOURCE_URL="${params.DATABASE_URL}" \
                      -e SPRING_DATASOURCE_USERNAME="${params.DATABASE_USERNAME}" \
                      -e SPRING_DATASOURCE_PASSWORD="${params.DATABASE_PASSWORD}" \
                      employees-api:build-${env.BUILD_NUMBER}

                    # Espera activa por arranque: valida que el contenedor siga vivo y que Spring termine de iniciar.
                    for i in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20; do
                      if ! docker ps --format '{{.Names}}' | grep -Fxq "${params.DOCKER_CONTAINER_NAME}"; then
                        echo "Deploy failed: el contenedor no está en ejecución"
                        docker logs --tail 100 "${params.DOCKER_CONTAINER_NAME}" || true
                        exit 1
                      fi
                      if docker logs "${params.DOCKER_CONTAINER_NAME}" 2>&1 | grep -Eq 'Started .* in [0-9]'; then
                        echo "Health-check OK (Spring iniciado, intento \$i)"
                        exit 0
                      fi
                      sleep 3
                    done
                    echo "Health-check failed: la aplicación no reportó arranque completo a tiempo"
                    docker logs --tail 150 "${params.DOCKER_CONTAINER_NAME}" || true
                    exit 1
                """
            }
        }

        stage('Docker cleanup') {
            when {
                expression { return params.BUILD_DOCKER_IMAGE && params.CLEAN_DOCKER_IMAGES }
            }
            steps {
                sh """
                    set +e
                    docker image rm "employees-api:build-${env.BUILD_NUMBER}" >/dev/null 2>&1 || true
                    docker image prune -f >/dev/null 2>&1 || true
                """
            }
        }

    }

    post {
        failure {
            echo 'Check test failures, Sonar quality gate (dashboard en Sonar + parámetro SONAR_WAIT_QUALITY_GATE), or Docker build.'
        }
    }
}
