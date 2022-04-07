## Antaeus

Antaeus (/ænˈtiːəs/), in Greek mythology, a giant of Libya, the son of the sea god Poseidon and the Earth goddess Gaia. He compelled all strangers who were passing through the country to wrestle with him. Whenever Antaeus touched the Earth (his mother), his strength was renewed, so that even if thrown to the ground, he was invincible. Heracles, in combat with him, discovered the source of his strength and, lifting him up from Earth, crushed him to death.

Welcome to our challenge.

## The challenge

As most "Software as a Service" (SaaS) companies, Pleo needs to charge a subscription fee every month. Our database contains a few invoices for the different markets in which we operate. Your task is to build the logic that will schedule payment of those invoices on the first of the month. While this may seem simple, there is space for some decisions to be taken and you will be expected to justify them.

## Instructions

Fork this repo with your solution. Ideally, we'd like to see your progression through commits, and don't forget to update the README.md to explain your thought process.

Please let us know how long the challenge takes you. We're not looking for how speedy or lengthy you are. It's just really to give us a clearer idea of what you've produced in the time you decided to take. Feel free to go as big or as small as you want.

## Developing

Requirements:
- \>= Java 11 environment

Open the project using your favorite text editor. If you are using IntelliJ, you can open the `build.gradle.kts` file and it is gonna setup the project in the IDE for you.

### Building

```
./gradlew build
```

### Running

There are 2 options for running Anteus. You either need libsqlite3 or docker. Docker is easier but requires some docker knowledge. We do recommend docker though.

*Running Natively*

Native java with sqlite (requires libsqlite3):

If you use homebrew on MacOS `brew install sqlite`.

```
./gradlew run
```

*Running through docker*

Install docker for your platform

```
docker build -t antaeus
docker run antaeus
```

### App Structure
The code given is structured as follows. Feel free however to modify the structure to fit your needs.
```
├── buildSrc
|  | gradle build scripts and project wide dependency declarations
|  └ src/main/kotlin/utils.kt 
|      Dependencies
|
├── pleo-antaeus-app
|       main() & initialization
|
├── pleo-antaeus-core
|       This is probably where you will introduce most of your new code.
|       Pay attention to the PaymentProvider and BillingService class.
|
├── pleo-antaeus-data
|       Module interfacing with the database. Contains the database 
|       models, mappings and access layer.
|
├── pleo-antaeus-models
|       Definition of the Internal and API models used throughout the
|       application.
|
└── pleo-antaeus-rest
        Entry point for HTTP REST API. This is where the routes are defined.
```

### Main Libraries and dependencies
* [Exposed](https://github.com/JetBrains/Exposed) - DSL for type-safe SQL
* [Javalin](https://javalin.io/) - Simple web framework (for REST)
* [kotlin-logging](https://github.com/MicroUtils/kotlin-logging) - Simple logging framework for Kotlin
* [JUnit 5](https://junit.org/junit5/) - Testing framework
* [Mockk](https://mockk.io/) - Mocking library
* [Sqlite3](https://sqlite.org/index.html) - Database storage engine

Happy hacking 😁!


## What i have done

### 1st commit
- Refactored "services" to be split into queries depending on a generic data layer instead of depending on a specific sql implementation.
- Implemented CQRS and Clean Architecture. 
- Queries should only read and never manipulate data.
- Commands should be used either for create or update or for use cases for example "ProcessOrderCommand", "EnrollCustomerCommand", etc


### 2nd commit
- Should have been part of the 1st, since 1st commit would contain build errors(i wasnt able to build in the beginning, but settings autoclrf to false, using gradle version 6.2 instead of newest and jdk 11 instead of newest helped)....
- Refactored rest and app to respect the new core architecture.

### 3rd commit
- Renamed input parameter from id to input on IQueryWithInput implementations to match interface and get rid of kotlin warning(I think it should actually be named what it is, but im not a fan of supressing warning so i kept it, same goes for currentdate.


### 4th commit
- Changed dependencies to implementation, since it seems they should only be used between the projects for now, like they are used already from the app.
- Refactored external to a seperate service layer so that the core logic will refer the service interface and a specific implemenation can be made in a new project like i did with the data and data-sql-implementation. Added an empty command class implementing a generic interface and a corresponding test class for the implementation.

### 5th commit
- Fixed build error caused by last commit(This would not happen in a real world with pull request into the dev branch, with build validation).
- Fixed copy/paste error in rest 
- Refactored IRepository to IReadRepository
- Created new IWriteRepository used for update
- Implemented logic in the new command
- Still missing - unit test implementation, manipulation of invoice status before calling update, implementation of update transaction.


### 6th commit
- Implemented manipulation of invoice status before calling update, implementation of update transaction.

### 7th commit
- Renamed PaymentService to IPaymentService
- Refactored the schedulePaymentsCommand from being called by rest to being a processPaymentsCommand called every day by a timer in the app
- Refactored processPaymentsCommand to take input param with date in order to mock it in test
- Added ILogger interface and implementation in order to make a generic logging not dependent on the KLogger framework.
- Implemented unit test  


    
