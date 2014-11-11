newTalentSrv
=========

REST API for Talent Service

***Warning: This project uses lambdas and requires Java 1.8***

## Usage

## Configuration
The module takes the following configuration:

    {
      "instancesPerCore": 1,
      "router": {
          "host": "0.0.0.0",
          "port": 8080
      },
      "mongodb": {
        "address": "vertx.mongopersistor",
        "host": <mongodb host>,
        "port": <mongodb port>,
        "username": <mongodb user>,
        "password": <mongodb password>,
        "db_name": <mongodb db name>
      }
    }
