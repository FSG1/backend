FROM maven:latest
LABEL maintainer="Tobias Derksen <tobias.derksen@student.fontys.nl>"

ENV HOST=0.0.0.0
ENV PORT=8080

VOLUME /root/.m2

COPY . /usr/src/app
WORKDIR /usr/src/app

RUN mvn clean install -Dcheckstyle.failOnViolation=false -B

EXPOSE 8080
# Cache maven dependencies
VOLUME /root/.m2

ENTRYPOINT [ "mvn" ]

# Execution without checkstyle and tests
CMD [ "exec:java", "-Dcheckstyle.skip", "-DskipTests=true", "-B" ]