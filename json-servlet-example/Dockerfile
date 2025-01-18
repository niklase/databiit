FROM ghcr.io/graalvm/graalvm-ce:ol7-java17-22.3.0-b2
EXPOSE 8080
RUN gu install js
WORKDIR application
COPY target/exploded/dependencies/ ./
COPY target/exploded/spring-boot-loader/ ./
COPY target/exploded/snapshot-dependencies/ ./
COPY target/exploded/application/ ./
ENV JAVA_OPTS="-XX:MaxRAMPercentage=50.0 -XshowSettings:vm -Xlog:gc*:stdout:tags,level,time"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=container -Djava.security.egd=file:/dev/./urandom org.springframework.boot.loader.JarLauncher" ]
