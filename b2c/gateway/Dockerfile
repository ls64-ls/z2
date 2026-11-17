FROM eclipse-temurin:8-jre

WORKDIR /app

COPY target/*.jar app.jar

ENV TZ=Asia/Shanghai \
    JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8088

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -jar app.jar"]
