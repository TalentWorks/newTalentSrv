newTalentSrv
=========

REST API for Talent Service

***Warning: This project uses lambdas and requires Java 1.8***

*The TalentJS API project is still on an early and immature state. Therefore, the interface
exposed by the API will evolve. Expect frequent changes that, in some cases, will break existing code.*

## Running locally

### Steps

***TDB***

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

## Authentication
Some API calls require clients to provide authentication using HTTP Basic authentication.

    Authorization: Basic <token>

Where *&lt;token>* contains the Base64 encoded user's credentials (See [RFC 2617] (http://tools.ietf.org/html/rfc2617) for details)

## Get list of all users
**Request**

    GET /api/users

**Response**

    HTTP 200
    [
      {
        "_id":"53c8242710cb6fc813dac19d",
        "firstName":"Isabel",
        "lastName":"Raggi",
        "primaryEmail":"isa@email.com",
        "__v":0
      },
      {
        "_id":"53c8244010cb6fc813dac19e",
        "firstName":"Federico",
        "lastName":"Raggi",
        "primaryEmail":"feder@email.com",
        "__v":0
      },
      ... additional results ...
    ]


**Notes**

- Requires authentication
- Use for debugging only; it will NOT be supported on a production environment

**Known Issues**

## Get user details

**Request**

    GET /api/users/53c8242710cb6fc813dac19d

**Response**

    HTTP 200

    {
      "_id":"53c8242710cb6fc813dac19d",
      "firstName":"Isabel",
      "lastName":"Raggi",
      "primaryEmail":"isa@email.com",
      "__v":0
    }

**Notes**

- Requires authentication

## Create a new user
**Request**

    POST /api/users

    {
      "firstName":"User",
      "lastName":"JoseUser",
      "primaryEmail":"JoseUser@email.com",
      "password":"hello"
    }

**Response**

    HTTP 201

**Notes**

## Update a user's properties or change its password
**Request**

    PUT /api/users/53c8242710cb6fc813dac19d

    {
      "firstName":"User",
      "lastName":"JoseUser",
      "primaryEmail":"JoseUser@email.com",
      "password":"hello"
    }

**Response**

    HTTP 200

**Notes**

- Requires authentication
- Only properties included on the request will be modified
- Password can be changed by providing a new value

## Delete a user
**Request**

    DELETE /api/users/53c8242710cb6fc813dac19d
**Response**

    HTTP 204

**Notes**

- Requires authentication

# Json Schemas
The API uses [json-schema](http://json-schema.org/) to specify the list of accepted attributes for a resource and define the constrains on them.
## User Schema

    [
      {
        "key": "UserSchema",
        "schema": {
          "$schema": "http://json-schema.org/draft-04/schema#",
          "title": "Json representation of a user",
          "description": "User properties",
          "type": "object",
          "properties": {
            "_id": {
              "type": "string"
            },
            "firstName": {
              "type": "string"
            },
            "middleName": {
              "type": "string"
            },
            "lastName": {
              "type": "string"
            },
            "dateOfBirth": {
              "type": "string",
              "format": "date-time"
            },
            "primaryEmail": {
              "type": "string",
              "format": "email"
            },
            "password": {
              "description": "Password must start with a letter, contain at least 4 and no more than 15 characters. Only letters, numbers, and underscore may be used",
              "type": "string",
              "pattern": "^[a-zA-Z]\\w{3,14}$"
            },
            "interests": {
              "type": "array",
              "items": {
                "type": "string"
              },
              "minItems": 0,
              "uniqueItems": true
            },
            "address": {
              "$ref": "vertxjsonschema://AddressSchema"
            }
          },
          "required": [
            "firstName",
            "lastName",
            "primaryEmail"
          ]
        }
      },
      {
        "key": "AddressSchema",
        "schema": {
          "$schema": "http://json-schema.org/draft-04/schema#",
          "title": "Json representation of an Address",
          "description": "Address properties",
          "type": "object",
          "properties": {
            "street1": {
              "type": "string",
               "maxLength": 50
            },
            "street2": {
              "type": "string",
              "maxLength": 50
            },
            "city": {
              "type": "string",
              "maxLength": 50
            },
            "state": {
              "type": "string",
              "minLength": 2,
              "maxLength": 50
            },
            "postalCode": {
              "type": "string",
              "minLength": 2,
              "maxLength": 10
            },
            "country": {
              "type": "string",
              "maxLength": 50
            }
          }
        }
      }
    ]
