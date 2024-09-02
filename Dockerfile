FROM amazoncorretto:17

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8080 available to the world outside this container
EXPOSE 8080

# The application's jar file
ENV JAR_FILE="redis-cache.jar"
ARG JAR_PATH="build/libs/${JAR_FILE}"

# Add the application's jar to the container
COPY ${JAR_PATH} ${JAR_FILE}

# Run app
ENTRYPOINT exec java -jar ${JAR_FILE}