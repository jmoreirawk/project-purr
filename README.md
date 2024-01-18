# Project Purr

## Description

Project Purr is an Android application developed using Kotlin, and built with Gradle.

## Setup

To set up the project, follow these steps:

1. Clone the repository.
2. Open the project in Android Studio.
3. Sync the project with Gradle files.

## Unit Tests

To run unit tests from the terminal, use the Gradle Wrapper command `./gradlew`. The command to run
all unit tests is:

```bash
./gradlew test
```

## Running Tests

To run Android UI tests (androidTests) from the terminal, use the Gradle Wrapper
command `./gradlew`. The command to run all UI tests is:

```bash
./gradlew connectedAndroidTest
```

## Features

The application has the following features:

- A screen with a list of cat breeds, displaying:
  - Cat image
  - Breed name
- The cat breeds screen contains a search bar to filter the list by breed name.
- The cat breeds screen contains a button to mark the breed as favourite.
- A favourites tab that shows the breeds marked as favourites, displaying:
  - The average lifespan of all the favourite breeds (using the higher value in the range).
- A screen with a detailed view of a breed, displaying:
  - Breed Name
  - Origin
  - Temperament
  - Description
  - A button to add/remove the breed from the favourites.
- Navigation between the different screens is managed by a Jetpack Navigation Component.
- Pressing on one of the list elements (in any screen) opens the detailed view of a breed.

## Technical Requirements

The application meets the following technical requirements:

- MVVM architecture
- Usage of Jetpack Compose for UI building
- Unit test coverage
- Offline functionality (using Room for data persistence)
- Follows the Single Source of Truth principle
- Error Handling
- Pagination for the list of cat breeds
- Modular design
- Integration and E2e tests

## Decisions

The following decisions were made during the development of the application:

- Tech stack used:
  - Jetpack Compose for UI building
  - Jetpack Navigation Component for navigation
  - Landscapist for loading images
  - Paging 3 for pagination
  - Room for data persistence
  - Retrofit with OkHttp for network calls
  - Moshi for JSON parsing
  - Hilt for dependency injection
  - Coroutines for asynchronous calls
  - JUnit for unit tests
  - Turbine for testing coroutines
  - Faker for generating fake data
  - Mockk for mocking objects in unit tests
  - Android Test Runner for running UI tests

- Modular structure:

  - `app`: Contains the application initialization, the main activity and the app navigation logic.
  - `data`: Contains the data layer, including the network and database as well as the data modules used.
  - `feature`: Contains the different features of the application, including the cat breeds list, the cat breed details and the favourites. Note that the `feature` module is divided into `breeds` and `details` modules, which contain the UI and logic for the cat breeds list and the cat breed details respectively.
  - `common`: Contains common code used across the application, including test utility functions and UI assets and functions.

This modular structure allows for a clear separation of concerns, and makes it easier to add new features to the application.
