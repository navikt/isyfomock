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

## Download packages from Github Package Registry

Certain packages (syfotjenester) must be downloaded from Github Package Registry, which requires authentication. The
packages can be downloaded via build.gradle

`githubUser` and `githubPassword` are properties that are set in `~/.gradle/gradle.properties`:

```
githubUser=x-access-token
githubPassword=<token>
```

Where `<token>` is a personal access token with scope `read:packages`(and SSO enabled).

The variables can alternatively be configured as environment variables or used in the command lines:

* `ORG_GRADLE_PROJECT_githubUser`
* `ORG_GRADLE_PROJECT_githubPassword`

```
./gradlew -PgithubUser=x-access-token -PgithubPassword=[token]
```

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
