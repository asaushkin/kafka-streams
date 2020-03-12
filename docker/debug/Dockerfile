FROM nginx

RUN set -ex \
    && apt-get update \
    && DEBIAN_FRONTEND=noninteractive apt-get install -yq \
        vim git jq htop wget curl sudo gnupg software-properties-common

RUN set -ex \
    && wget -qO - https://packages.confluent.io/deb/5.4/archive.key | apt-key add - \
    && add-apt-repository "deb [arch=amd64] https://packages.confluent.io/deb/5.4 stable main" \
    && apt-get update \
    && DEBIAN_FRONTEND=noninteractive apt-get -yq install confluent-community-2.12

RUN set -ex \
    && mkdir -p /usr/share/man/man1 \
    && DEBIAN_FRONTEND=noninteractive apt-get install -yq ca-certificates \
        openjdk-11-jre-headless openjdk-11-jdk-headless

ENV VERSION=1.5.0-1.1

ENV BUILD_PACKAGES="build-essential cmake python git curl zlib1g-dev libsasl2-dev libssl-dev pkgconf libcurl4-openssl-dev"

RUN echo "Install build-essentials ....." \
    && apt-get update -y \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y $BUILD_PACKAGES

RUN echo "Building kafka-cat" \
    && git clone https://github.com/edenhill/kafkacat \
    && cd kafkacat \
    && git checkout tags/debian/$VERSION \
    && ./bootstrap.sh \
    && make \
    && make install \
    && cd / \
    && rm -fr kafkacat


RUN set -ex \
    && DEBIAN_FRONTEND=noninteractive apt-get -yq install procps


WORKDIR /root/

COPY .bashrc /etc/skel/.bashrc.local

COPY provision.sh /provision.sh
COPY connectors /connectors

