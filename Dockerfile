# TODO: Replace with jdk11 maven / postgres combo
FROM adrienpessu/docker-maven-postgres

ENV POSTGRES_USER=appuser
ENV POSTGRES_PASSWORD=password
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/appuser
ENV SPRING_DATASOURCE_USERNAME=appuser
ENV SPRING_DATASOURCE_PASSWORD=password

WORKDIR /app
COPY mvnw .
COPY .mvn ./.mvn
COPY pom.xml .
COPY src ./src

RUN chown postgres:postgres /var/log/postgresql/postgresql-9.6-main.log \
    && pg_ctlcluster 9.6 main start && pg_lsclusters \
    && gosu postgres psql -c "CREATE USER appuser WITH PASSWORD 'password';" \
    && gosu postgres psql -c "CREATE DATABASE appuser OWNER appuser;" \
	&& /app/mvnw -v \
    && /app/mvnw clean install

FROM openjdk:8
WORKDIR /app
COPY --from=0 /app/target/*.jar ./target/
COPY run.sh ./

EXPOSE 8080

CMD ["/app/run.sh"]