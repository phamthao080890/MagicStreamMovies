# Streaming APIs - Backend Setup Instructions

## Requirements / Installations

Install the following things:

- **IntelliJ Community Edition**
    1. [Download IntelliJ](https://www.jetbrains.com/idea/download/?var=1&section=mac)

- **Java 17** (via IntelliJ)
  1. Open IntelliJ. Go to `File -> Project Structure`.
  2. On the left, go to `Platform Settings -> SDKs`.
  3. Click on the `+` sign at the top of the middle column, select `Download JDK`.
  4. Select version `corretto-17` as the vendor, then click `Download`.
  5. Check the version installed:
      ```
      $ /usr/libexec/java_home -V
      Matching Java Virtual Machines (1):
          17.0.15 (arm64) "Amazon.com Inc." - "Amazon Corretto 17" /Users/user/Library/Java/JavaVirtualMachines/corretto-17.0.15/Contents/Home
      ```
  6. Set `JAVA_HOME` by adding the following to `~/.zshrc` (or `~/.bash_profile`):
     ```
     export JAVA_17_HOME=$(/usr/libexec/java_home -v17)
     export JAVA_HOME=$JAVA_17_HOME
     ```
  7. Reload and verify:
      ```
      $ source ~/.zshrc
      $ echo $JAVA_HOME
      /Users/user/Library/Java/JavaVirtualMachines/corretto-17.0.15/Contents/Home
      ```

- **MongoDB Atlas** (cloud-hosted, no local installation required)

  The application connects to a MongoDB Atlas cluster. Connection details are
  configured in `src/main/resources/application.properties`:

  ```
  spring.data.mongodb.uri=mongodb+srv://<user>:<password>@<cluster>.mongodb.net/?appName=<appName>
  spring.data.mongodb.database=<database>
  ```

  To use a different Atlas cluster or a local MongoDB instance, override these
  values in `src/main/resources/application-local.properties` and run with
  the `local` Spring profile:
  ```
  # Local MongoDB (uncomment to use)
  # spring.data.mongodb.uri=mongodb://localhost:27017/mystreamingdb
  # spring.data.mongodb.database=mystreamingdb

  # Atlas (default)
  spring.data.mongodb.uri=mongodb+srv://<user>:<password>@<cluster>.mongodb.net/?appName=<appName>
  spring.data.mongodb.database=<database>
  ```

## Data Seeding (Automatic)

> **No manual `mongoimport` step is required.**

The application includes a `DataSeeder` component that runs automatically on
startup. It checks each collection and **only imports data when the collection
is empty**, so it is safe to restart without duplicating records.

Collections seeded from `src/main/resources/seed/`:

| Collection  | Source file          |
|-------------|----------------------|
| `genres`    | `seed/genres.json`   |
| `rankings`  | `seed/rankings.json` |
| `movies`    | `seed/movies.json`   |
| `users`     | `seed/users.json`    |

Startup log output when seeding runs:
```
[DataSeeder] 'genres' is empty — importing from seed/genres.json...
[DataSeeder] Inserted 9 document(s) into 'genres'.
```

Startup log output when data already exists (no-op):
```
[DataSeeder] 'genres' already has data — skipping import.
```

## MongoDB Driver

This project uses **`mongodb-driver-sync`** directly for all database access
instead of Spring Data `MongoRepository` interfaces. Each collection has a
dedicated DAO class in `src/main/java/com/streaming/api/repositories/`:

| DAO class           | Collection   |
|---------------------|--------------|
| `UserRepository`    | `users`      |
| `MovieRepository`   | `movies`     |
| `GenreRepository`   | `genres`     |
| `RankingRepository` | `rankings`   |

The `MongoDatabase` bean is provided by `MongoConfig` and injected into each
DAO via constructor injection. Spring Data repository auto-scanning is
disabled (`MongoRepositoriesAutoConfiguration` excluded).

## Setup

> Make sure Java 17 is active and you have access to the configured MongoDB
> Atlas cluster (or a local MongoDB instance if using the `local` profile).

1. Configure IntelliJ:
    - Open the cloned `backend` folder in IntelliJ.
    - Right-click `pom.xml` and select **"Add as Maven Project"**.
    - Go to `File -> Project Structure` and set the **Project SDK** to Java 17.
    - Setup [Run configurations](#intellij-run-configurations) in IntelliJ.

2. Build the project:
   ```
   $ ./mvnw clean package
   ```

## Running

Run using the Maven wrapper:
```
$ ./mvnw spring-boot:run
```

Or with the `local` Spring profile (uses `application-local.properties`):
```
$ ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Or run the packaged jar directly:
```
$ java -jar target/streaming-api-0.0.1-SNAPSHOT.jar
```

Or run the application using the run configurations set up in IntelliJ.

## Docker

Build and run the application in a container (no local Maven or Java required):

```
$ docker build -t streaming-api -f dockerfile .

$ docker run --rm -p 8080:8080 \
  -e PORT=8080 \
  -e SPRING_DATA_MONGODB_URI=mongodb+srv://<user>:<password>@<cluster>.mongodb.net/?appName=<appName> \
  -e SPRING_DATA_MONGODB_DATABASE=<database> \
  streaming-api
```

> Inside Docker, use `host.docker.internal` instead of `localhost` if
> pointing at a MongoDB instance running on your Mac host.

## IntelliJ Run Configurations

### Local

This configuration is saved in `.idea/runConfigurations/Local.xml` and should
be available when IntelliJ is started.

### Other Run Configurations

On the top right side of IntelliJ, between the build and run icons, there is
a dropdown with the selected run configuration. Click it and select
**"Edit Configurations..."**.

Click the `+` sign on the top left of the dialog box to add a new
configuration.
