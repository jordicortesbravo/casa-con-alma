FROM public.ecr.aws/jht/eclipse-temurin:21-jre-alpine

# define environment variable artifact name
ENV ARTIFACT_NAME="${JAR_FILE}" \
    JMX_OPTS="-Dcom.sun.management.jmxremote.port=9999 \
    -Dcom.sun.management.jmxremote.rmi.port=9999 \
    -Dcom.sun.management.jmxremote.authenticate=false \
    -Dcom.sun.management.jmxremote.ssl=false \
    -Djava.rmi.server.hostname=127.0.0.1"

COPY ./${ARTIFACT_NAME} /opt/app/${ARTIFACT_NAME}

EXPOSE 8080

# Execute artifact by java
CMD java ${JAVA_OPTS} ${JMX_OPTS} -jar /opt/app/${ARTIFACT_NAME}