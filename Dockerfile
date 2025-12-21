FROM eclipse-temurin:21-jdk-jammy

ARG version=undefined
ENV VERSION=${version} \
    JAVA_OPTS="\
-Dfile.encoding=UTF-8 \
-Djava.awt.headless=true \
-Djava.security.egd=file:/dev/./urandom \
-Xms512m -Xmx4g \
-XX:+UseContainerSupport \
-XX:+AlwaysPreTouch \
-XX:+ExitOnOutOfMemoryError \
-XX:+UseStringDeduplication \
-XX:+UnlockExperimentalVMOptions \
-XX:+UseZGC"

LABEL version=${VERSION}

COPY build/libs/test-0.0.1-SNAPSHOT.jar /test.jar

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /test.jar" ]
