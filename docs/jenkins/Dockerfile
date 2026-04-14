FROM jenkins/jenkins:lts-jdk17

USER root

SHELL ["/bin/bash", "-eo", "pipefail", "-c"]

RUN apt-get update \
    && apt-get install -y --no-install-recommends ca-certificates curl git \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

ARG DOCKER_VERSION=24.0.9
RUN ARCH=$(uname -m) \
    && case "$ARCH" in \
         x86_64) DARCH=x86_64 ;; \
         aarch64|arm64) DARCH=aarch64 ;; \
         *) echo "Unsupported architecture: $ARCH" >&2; exit 1 ;; \
       esac \
    && curl -fsSL "https://download.docker.com/linux/static/stable/${DARCH}/docker-${DOCKER_VERSION}.tgz" \
      | tar xz -C /tmp \
    && mv /tmp/docker/docker /usr/bin/docker \
    && chmod +x /usr/bin/docker \
    && rm -rf /tmp/docker \
    && docker --version \
    && git --version

ARG BUILDX_VERSION=v0.16.2
RUN ARCH=$(uname -m) \
    && case "$ARCH" in \
         x86_64) BARCH=amd64 ;; \
         aarch64|arm64) BARCH=arm64 ;; \
         *) echo "Unsupported architecture: $ARCH" >&2; exit 1 ;; \
       esac \
    && mkdir -p /usr/libexec/docker/cli-plugins \
    && curl -fsSL "https://github.com/docker/buildx/releases/download/${BUILDX_VERSION}/buildx-${BUILDX_VERSION}.linux-${BARCH}" \
      -o /usr/libexec/docker/cli-plugins/docker-buildx \
    && chmod +x /usr/libexec/docker/cli-plugins/docker-buildx \
    && docker buildx version

USER jenkins

ENV PATH="/usr/local/bin:/usr/bin:/bin:/usr/local/sbin:/usr/sbin:/sbin:${PATH}"
