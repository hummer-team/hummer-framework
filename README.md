# The Hummer Project

## About this project

glue spring boot,mybatis,kafka,rabbitmq,redis,mongodb...framework,Make business development easy

## Technical Stack

- Java 1.8+
- Maven 3.5+
- Spring boot 2.4.X
- Lombok abstraction
- Swagger 2 API documentation
- REST API model validation
- Kafka
- redis
- rocksDB
- multiple thread 

## model describe

- hummer-api
    - this is demo module,include all feature test
- message plugin  
    - kafka customer，product，stream 
    - rabbitmq customer，product
- hummer-dao
    - support multiple dataSource,dynamic switch data source
- hummer-core
    - customer property container
- hummer-pipeline-plugin
    - customer multiple thread pipeline
- hummer-rest
    - glue fast json serial deserialization http request response message
    - customer simple binding module,convert http query string parameter to business entity...      

## about release
So the snapshot version is used ，because it is convenient for the company project team to enjoy the new features of the framework without upgrading
