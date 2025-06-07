FROM clojure:temurin-24-lein-alpine

RUN mkdir -p /usr/src/app /usr/src/target

WORKDIR /usr/src/app

COPY app/project.clj /usr/src/app/

RUN lein deps

COPY app /usr/src/app

RUN mv "$(lein ring uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" app-standalone.jar

EXPOSE 3000

CMD ["java", "-jar", "app-standalone.jar"]
