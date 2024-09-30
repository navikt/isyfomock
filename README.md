# isyfomock

Backend for Team iSYFO sin mock. Appen ligger i dev-gcp og APIet er dokumentert og kan brukes fra https://isyfomock.intern.dev.nav.no/api/v1/docs/

## Technologies used

* Docker
* Gradle
* Kotlin
* Ktor

##### Test Libraries:

* Kluent
* Mockk
* Spek

#### Requirements

* JDK 17

### Build

Run `./gradlew clean shadowJar`

### Lint (Ktlint)

##### Command line

Run checking: `./gradlew --continue ktlintCheck`

Run formatting: `./gradlew ktlintFormat`

##### Git Hooks

Apply checking: `./gradlew addKtlintCheckGitPreCommitHook`

Apply formatting: `./gradlew addKtlintFormatGitPreCommitHook`

## Contact

### For NAV employees

We are available at the Slack channel `#isyfo`.
