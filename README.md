# Advanced Big Data Applications and Indexing Techniques
## Northeastern University (Sept 2020 - Jan 2021)

Repository related to development for REST Api prototype model demo work for INFO 7255  
  
## Contents
In this project, we will develop a REST Api to parse a JSON schema model divided into three demos
1. **Prototype demo 1**
    - https://spring.io/guides/gs/spring-boot/
    - Develop a Spring Boot based REST Api to parse a given sample JSON schema.
    - Save the JSON schema in a redis key value store.
    - Demonstrate the use of operations like `GET`, `POST` and `DELETE` for the first prototype demo.
    - Add Etag
2. **Prototype demo 2**
    - Regress on your model and perform additional operations like `PUT` and `PATCH`.
    - Secure the REST Api with a security protocol like JWT or OAuth2.
3. **Prototype demo 3**
    - Adding Elasticsearch capabilities
    - Adding Kafka system for REST API queueing

## Pre-requisites
1. Redis Server
`$ brew install redis`
`$ redis-server /usr/local/etc/redis.conf`
2. Elasticsearch and Kibana(Local or cloud based)
3. Apache Kafka
