[![Build Status](https://travis-ci.org/Tweetsched/tweetsched-api.svg?branch=master)](https://travis-ci.org/Tweetsched/tweetsched-api)
[![MIT licensed](https://img.shields.io/badge/license-MIT-blue.svg)](./LICENSE)

# tweetsched-api

REST API and Web UI for Scheduled Tweets service.

## Requirements:
 - Java 8 or higher
 - Maven 3.3.3 or higher

## How to build:
`mvn clean package`

## How to run locally:
- Configure next environment variables:
  - PORT
  - REDIS_URL
  - REDIS_PORT
  - REDIS_PASSWORD
- Run `java -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.Log4j2LogDelegateFactory -jar target/tweetsched-api-0.1-SNAPSHOT.jar`
- Open `http://localhost:8080/`
