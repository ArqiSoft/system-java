# Java Service Template

## System Requirements

Java 1.8, Maven 3.x
Optional: docker, docker-compose

## Local Build Setup

```terminal
# build
mvn -Pdocker clean package

# run as standalone application
mvn spring-boot:run
```

## How to create a new service

1. Copy *template* folder content to service repository
2. Change *artifactId** and *name* properties in pom.xml
3. Import maven project into IDE
4. In *com.sds.osdr.model* package update *SampleCommand* and events 
5. Rename *SampleCommand* class and update all references to it
6. Rename *SampleCommandMessageCallback* class and its references
7. Make sure *OSDR_RABBIT_MQ* and *OSDR_MONGO_DB* environment variables are defined
8. Update *queueName* variable in *src/main/resources/application.properties* Value should correspond to the event queue name to read from in rabbitmq
9. Rename and implement *SampleCommandProcessor* class

## Create and start docker image

1. Update *service name*, *container_name*, and *image* parameters in *docker-compose.yml* file.
2. Update envitonment variable values and *port* parameter in *docker-compose.yml* file if needed.
3. Use *docker-compose build* command to build the docker image.
4. Use *docker-compose up -d* command to launch the docker image.
