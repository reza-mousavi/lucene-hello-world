# lucene-hello-world

This is a simple Spring Boot command-line application that demonstrates the use of Apache Lucene.

## What is Lucene?

Apache Lucene is a high-performance, full-featured text search engine library written entirely in Java. It is a technology suitable for nearly any application that requires full-text search, especially cross-platform.

## How to build and run the application

1.  Clone the repository.
2.  Open a terminal and navigate to the project's root directory.
3.  Run the following command:

    ```bash
    mvn spring-boot:run
    ```

### Passing Environment Variables

You can override configuration properties using environment variables when running the application. For example, to set the `create` property in the Lucene configuration to `FALSE`, use:

```bash
APP_CONFIG_LUCENE_CREATE=FALSE mvn spring-boot:run
```

This sets the `create` property in the application's configuration to `FALSE` for this run, which controls whether Lucene creates a new index or appends to an existing one. Environment variables prefixed with `APP_CONFIG_LUCENE_` map to the fields in the `AppProperties` record class.

This approach works for any property defined in your configuration class. For example:

```bash
APP_CONFIG_LUCENE_INDEXPATH=/path/to/index APP_CONFIG_LUCENE_DOCSPATH=/path/to/docs mvn spring-boot:run
```

#### Example Run Commands

To control whether Lucene creates a new index or appends to an existing one, use the `create` flag:

- **Create a new index (create = true):**
  ```bash
  APP_CONFIG_LUCENE_CREATE=TRUE mvn spring-boot:run
  ```
- **Append to an existing index (create = false):**
  ```bash
  APP_CONFIG_LUCENE_CREATE=FALSE mvn spring-boot:run
  ```

### Fake Data Generation

The application supports generating fake Person data using the `PersonFakerService`. This is controlled by the `execute` flag in your configuration (application.yml or environment variable):

- If `execute` is set to `true`, the application will generate fake Person records and write them as JSON files to the specified output directory.
- If `execute` is set to `false`, fake data generation will be skipped.

You can set the flag using an environment variable:

```bash
APP_CONFIG_EXECUTE=true mvn spring-boot:run
```

You can also configure the number of records and output path in your configuration:

```yaml
app:
  config:
    execute: true
    fake-data:
      count: 100
      output-path: /path/to/output
```

This will generate 100 fake Person JSON files in the specified directory when you run the application.

### Spring Profiles

This project provides three main Spring profiles for different use cases:

- **search**: For searching and querying Lucene indexes. Disables fake data generation.
- **fake**: For generating fake Person data and writing it to files. Disables Lucene indexing/search.
- **default** (or your third profile name): The default profile for general use, or for custom configuration. (Update this description if your third profile has a specific name and purpose.)

You can activate a profile using either the Maven property or an environment variable:

**To run with the `search` profile:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=search
```

**To run with the `fake` profile:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=fake
```

**To run with the `default` (or your third) profile:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=default
```

Or using environment variables:
```bash
SPRING_PROFILES_ACTIVE=search mvn spring-boot:run
SPRING_PROFILES_ACTIVE=fake mvn spring-boot:run
SPRING_PROFILES_ACTIVE=default mvn spring-boot:run
```

Update your `application-{profile}.yml` files to customize settings for each profile as needed.

## Configuration Structure

The configuration is managed by the `AppProperties` record, which contains two nested records:
- `FakeData`: Controls fake data generation (fields: `execute`, `count`, `outputPath`).
- `Lucene`: Controls Lucene indexing and search (fields: `index`, `indexPath`, `docsPath`, `field`, `paging`, `repeat`, `raw`, `create`).

## Project Structure

Project directory tree:

```
lucene-hello-world/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       ├── java/
│       │   ├── com/
│       │   │   └── reza/
│       │   │       └── learning/
│       │   │           └── config/
│       │   │               └── AppProperties.java
│       │   └── org/
│       │       └── apache/
│       │           └── lucene/
│       │               └── demo/
│       │                   └── IndexFiles.java
│       └── resources/
│           └── application.yml
├── docs/
│   └── ... (your text files to be indexed)
```

Project data could be structured as follows:
```
project data
├── indexes/
│   └── ... (Lucene index files)
└── target/
    └── ... (build output)
```

The main folders and files in this project are:

- `src/main/java/com/reza/learning/config/AppProperties.java`: Spring configuration class for Lucene properties.
- `src/main/java/org/apache/lucene/demo/IndexFiles.java`: Lucene indexer logic.
- `src/main/resources/application.yml`: Main configuration file (YAML format).

### How docs and indexes folders are used

- The `docs` folder should contain the documents (e.g., text files) you want to make searchable. The application reads files from this folder when building the index.
- The `indexes` folder is where Lucene stores its index data. When you run the application, it will create or update the index in this folder, depending on the configuration (e.g., the `create` property).

You can configure the paths for these folders using the `application.yml` file or by passing environment variables as described above.

## More Info

| Technology      | Website                                              | License Name & Link                                                                 |
|-----------------|------------------------------------------------------|-------------------------------------------------------------------------------------|
| Apache Lucene   | [lucene.apache.org](https://lucene.apache.org/)      | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)                   |
| Spring Boot     | [spring.io](https://spring.io/projects/spring-boot)  | [Apache License 2.0](https://github.com/spring-projects/spring-boot/blob/main/LICENSE.txt) |
| Spring Profiles | [docs.spring.io](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.profiles) | See Spring Boot license above                                                      |
| Maven           | [maven.apache.org](https://maven.apache.org/)        | [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)                   |
| YAML            | [yaml.org](https://yaml.org/)                        | [YAML Specification License](https://yaml.org/spec/1.2.2/#10-license)               |
| This project    | —                                                    | [MIT License](./LICENSE)                                                            |

For further details, see the official documentation and license for each technology above.
