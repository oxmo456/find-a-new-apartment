FROM ubuntu

RUN apt-get update

RUN apt-get install -y redis-server

RUN \
  apt-get install -y software-properties-common && \
  echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -y oracle-java8-installer && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk8-installer

ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

WORKDIR /

USER daemon

ADD target/scala-2.10/fana-service.jar /fana-service.jar

ENTRYPOINT [ "java", "-jar", "/fana-service.jar", "prod" ]

EXPOSE 8080




