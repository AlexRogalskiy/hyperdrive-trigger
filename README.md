<!--
  ~
  ~ Copyright 2018 ABSA Group Limited
  ~
  ~  Licensed under the Apache License, Version 2.0 (the "License");
  ~  you may not use this file except in compliance with the License.
  ~  You may obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~  Unless required by applicable law or agreed to in writing, software
  ~  distributed under the License is distributed on an "AS IS" BASIS,
  ~  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~  See the License for the specific language governing permissions and
  ~  limitations under the License.
  ~
  -->
# Hyperdrive-trigger

### <a name="build_status"/>Build Status
| develop |
| ------------- |
| [![Build Status](https://opensource.bigusdatus.com/jenkins/buildStatus/icon?job=Absa-OSS-Projects%2Fhyperdrive-trigger%2Fdevelop)](https://opensource.bigusdatus.com/jenkins/job/Absa-OSS-Projects/job/hyperdrive-trigger/job/develop/) |
___

<!-- toc -->
- [What is Hyperdrive-trigger?](#what-is-hyperdrive-trigger)
- [Requirements](#requirements)
- [How to build and run](#how-to-build-and-run)
    - [Application properties](#application-properties)
    - [Embedded Tomcat](#embedded-tomcat)
    - [Docker image](#docker-image)
    - [Web Application Archive](#web-application-archive)
- [User Interface](#user-interface)
- [Development](#development)
- [How to contribute](#how-to-contribute)
<!-- tocstop -->

# What is Hyperdrive-trigger?
**Hyperdrive-trigger** is a **Event based workflow manager and scheduler**.

A workflow is defined via the graphical interface and consists of three parts: **details**, **sensor** and **jobs**:
- **Details** - General workflow's information (workflow name, project name and whether is workflow active)
- **Sensor** - Definition of workflow trigger, describes when workflow will be executed. Sensors that could be used:
  - *Kafka* - waits for kafka message with specific message 
  - *Time* - time based trigger, cron-quartz expression or user friendly time recurring definition can be used
  - *Recurring* - workflow is triggered always when previous execution is finished 
- **Jobs** - list of jobs. Supported job types: 
  - *Generic Spark Job* - spark job deployed to Apache Hadoop YARN
  - *Generic Shell Job* - job executing a user-defined shell script

The user interface provides a visualization of running workflows with the ability to monitor and troubleshoot executions.

# Requirements
Tested with:
|              |                        |
| ------------ | ---------------------- | 
| OpenJDK      | 1.8.0_25               |
| Scala        | 2.11                   |
| Maven        | 3.5.4                  |
| PostgreSQL   | 12.2_1                 | 
| Spark        | 2.4.4                  | 
| Hadoop Yarn  | 2.6.4.0                | 
| Tomcat       | 9.0.24                 | 
| Docker       | 2.3.0.5                | 
| NPM          | 6.14.4                 | 
| Angular CLI  | 9.0.3                  | 

> Note: Docker is not required.

# How to build and run
## Application properties
Adjusted application properties have to be provided. An application properties template file can be found at `src/main/resources/application.properties`. Application properties that can be adjusted: 

```
# Version of application. 
version=@project.version@
# Enviroment where application will be running
environment=Local
```
```
# How will users authenticate. Available options: inmemory, ldap
auth.mechanism=inmemory
# INMEMORY authentication: username and password defined here will be used for authentication.
auth.inmemory.user=user
auth.inmemory.password=password
# LDAP authentication: props template that has to be defined in case of LDAP authentication
auth.ad.domain=
auth.ad.server=
auth.ldap.search.base=
auth.ldap.search.filter=
```
```
# Unique app id, for easier application identification
appUniqueId=
```
```
# Core properties.
# How many threads to use for each part of the "scheduler".
# Heart beat interval in milliseconds.
# Lag threshold, before instance is deactivated by another instance.
scheduler.thread.pool.size=10
scheduler.sensors.thread.pool.size=20
scheduler.executors.thread.pool.size=30
scheduler.jobs.parallel.number=100
scheduler.heart.beat=5000
scheduler.lag.threshold=20000
```
```
#Kafka sensor properties. Not all are required. Adjust according to your use case.
kafkaSource.group.id=hyper_drive_${appUniqueId}
kafkaSource.key.deserializer=org.apache.kafka.common.serialization.StringDeserializer
kafkaSource.value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
kafkaSource.poll.duration=500
kafkaSource.max.poll.records=3
kafkaSource.properties.enable.auto.commit=false
kafkaSource.properties.auto.offset.reset=latest
kafkaSource.properties.security.protocol=
kafkaSource.properties.ssl.truststore.location=
kafkaSource.properties.ssl.truststore.password=
kafkaSource.properties.ssl.keystore.location=
kafkaSource.properties.ssl.keystore.password=
kafkaSource.properties.ssl.key.password=
kafkaSource.properties.sasl.kerberos.service.name=
kafkaSource.properties.sasl.mechanism=
kafkaSource.properties.sasl.jaas.config=
```
```
#Spark yarn sink properties. Properties used to deploy and run Spark job in Yarn. Not all are required. Adjust according to your use case.
sparkYarnSink.hadoopResourceManagerUrlBase=
sparkYarnSink.hadoopConfDir=
sparkYarnSink.sparkHome=
sparkYarnSink.master=yarn
sparkYarnSink.submitTimeout=160000
sparkYarnSink.filesToDeploy=
sparkYarnSink.additionalConfs.spark.ui.port=
sparkYarnSink.additionalConfs.spark.executor.extraJavaOptions=
sparkYarnSink.additionalConfs.spark.driver.extraJavaOptions=
sparkYarnSink.additionalConfs.spark.driver.memory=2g
sparkYarnSink.additionalConfs.spark.executor.instances=2
sparkYarnSink.additionalConfs.spark.executor.cores=2
sparkYarnSink.additionalConfs.spark.executor.memory=2g
sparkYarnSink.additionalConfs.spark.yarn.keytab=
sparkYarnSink.additionalConfs.spark.yarn.principal=
sparkYarnSink.additionalConfs.spark.shuffle.service.enabled=true
sparkYarnSink.additionalConfs.spark.dynamicAllocation.enabled=true
```
```
#Postgresql properties for connection to trigger database
db.driver=org.postgresql.Driver
db.url=jdbc:postgresql://
db.user=
db.password=
db.keepAliveConnection=true
db.connectionPool=HikariCP
db.numThreads=4
db.skip.liquibase=false
```

## Embedded Tomcat

For development purposes, hyperdrive-trigger can be executed as an application with an embedded tomcat. Please check out branch **feature/embedded-tomcat-2** to use it.

To build an executable jar and execute it, use the following commands:
- Package jar: `mvn clean package` or without tests `mvn clean package -DskipTests`
- Execute it: `java -jar ./target/hyperdrive-trigger-<VERSION>.jar`

Access the application at 
```
http://localhost:7123/#
```

For local and iterative front end development, the UI can be run using a live development server with the following commands: 
- Install required packages: `cd ui && npm install`
- Start front end application: `cd ui && ng serve` or `cd ui && npm start`

Access the application at 
```
http://localhost:4200/#
```
 
## Docker image

From the project root directory, run the Docker build command
```
docker build -t {your-image-name:your-tag} .
```

### Building Docker with Maven
The Docker image can also be built using Maven, with the [https://github.com/spotify/dockerfile-maven](Spotify Dockerfile Maven plugin).

From the project root directory, run the Maven install command with the docker profile enabled (see below):
```
mvn clean install \
  -D skipTests \                  # Skip unit and integration tests
  -D docker \                     # Activate "docker" profile
  -D dockerfile.repositoryUrl=my  # The name prefix of the final Docker image(s)
```

This will create a docker image with the name `my/hyperdrive-trigger`, tagged as `{project_version-{commit-id}` and `latest`
e.g. `my/hyperdrive-trigger:0.5.3-SNAPSHOT-6514d3f22a4dcd73a734c614db96694e7ebc6efc`, and `my/hyperdrive-trigger:latest`

## Web Application Archive
 
Hyperdrive-trigger can be packaged as a Web Application Archive and executed in a web server.  

- Without tests: `mvn clean package -DskipTests`
- With unit tests: `mvn clean package`

## Liquibase
The liquibase maven plugin may be used to issue liquibase commands. To use it, copy 
`/etc/liquibase/liquibase-maven-plugin.properties.template` to `/etc/liquibase/liquibase-maven-plugin.properties` and modify it as needed.

Then, the liquibase maven plugin can be executed, e.g.
- `mvn liquibase:status` to view the status
- `mvn liquibase:dropAll` to drop all tables, views etc.
- `mvn liquibase:update` to apply all pending changesets

# User Interface
- **Workflows**: Overview of all workflows.
![](/docs/img/all_workflows.png)

- **Show workflow**
![](/docs/img/create_workflow.png)

- **Create workflow**
![](/docs/img/create_workflow.png)

- **Workflow history comparison**
![](/docs/img/history_comparison.png)

- **Runs**: Overview of all workflow's runs.
![](/docs/img/all_runs.png)

- **Run Detail**
![](/docs/img/run_detail.png)


# How to contribute
Please see our [**Contribution Guidelines**](CONTRIBUTING.md).
