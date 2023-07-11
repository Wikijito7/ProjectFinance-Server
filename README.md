# ProjectFinance-Server
Custom backend for Project Finance Android app.

## Index
* [Pre-requisites](https://github.com/Wikijito7/ProjectFinance-Server#pre-requisites)
* [How to run it](https://github.com/Wikijito7/ProjectFinance-Server#how-to-run-it)
* [How it works](https://github.com/Wikijito7/ProjectFinance-Server#how-it-works)
* [TODO](https://github.com/Wikijito7/ProjectFinance-Server#todo)

## Pre-requisites
* Java 17.
* Other requisites are already built in on `build.gradle.kts`. ([See more here](https://github.com/Wikijito7/ProjectFinance-Server/blob/master/build.gradle.kts))
* Knowledge using a terminal.
* A little bit of time.
* Optional: Coffee to drink while executing the app.

## How to run it
- After checking pre-requisites and having all dependencies installed, you can just go to `Application.kt` and run `fun main()`.
- Also, you can generate a `fat-jar` by executing `fatJar` task on `build.gradle.kts`. After that, you may execute the app by double clicking it or by executing it on a terminal using `java -jar base-ktor-server.jar`.
- Lastly, you can download last version of the project, check it out on [here](https://github.com/Wikijito7/ProjectFinance-Server/releases).

## How it works
Project Finance Server is the custom backend server for Project Finance's Android app. It is used to support its online functionality, such as invoices synchronization or user profiles customization.

You may configure some stuff on `app.conf` file. You can check it out [here](https://github.com/Wikijito7/ProjectFinance-Server/blob/master/src/main/resources/app.conf).

In order to use this Server, you must use it via Project Finance's Android app. Check it out [here](https://github.com/Wikijito7/ProjectFinance-Android).

## TODO:
* Any suggestion? Create a ticket [here](https://github.com/Wikijito7/ProjectFinance-Server/issues).

## Known bugs
* Have you found one? Create a ticket [here](https://github.com/Wikijito7/ProjectFinance-Server/issues).
