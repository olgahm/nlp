FROM anapsix/alpine-java:8u162b12_server-jre
MAINTAINER Librairy <librairy.framework@gmail.com>
VOLUME /tmp
ADD librairy-nlp.jar /app.jar
ADD bin /bin
ENTRYPOINT exec java $JAVA_OPTS -server -jar /app.jar
