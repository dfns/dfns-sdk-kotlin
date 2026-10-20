# Dfns Android SDK

Welcome, builders 👋🔑 This repo holds Dfns Android SDK. Useful links:

- [Dfns Website](https://www.dfns.co)
- [Dfns API Docs](https://docs.dfns.co)

## Requirements

- **Android 9 (API level 28) or higher.** The SDK is built with `minSdk = 28`, so consuming apps must declare `minSdk` 28 or above. This floor matches passkey support on Android, which is available from Android 9 onwards.
- **Credential Manager provider.** Passkeys are created and asserted through [Credential Manager](https://developer.android.com/identity/sign-in/credential-manager) (`androidx.credentials`). On Android 13 and below, Credential Manager is backed by Google Play services: the device must have Google Play services, and your app must include the `androidx.credentials:credentials-play-services-auth` dependency alongside the SDK. On Android 14+, the framework supports third-party credential providers as well.
- **Screen lock configured.** The device must have a screen lock (PIN, pattern, or biometric) set up; passkey creation fails without one.
- **A credential provider account.** By default passkeys are saved to Google Password Manager, which requires a signed-in Google account (or, on Android 14+, another credential provider chosen by the user).

## Installation

Maven Central publishing is being finalized. Until it lands, build the SDK from source using one of
the options below. See the [installation guide](https://docs.dfns.co/sdks/frontend/kotlin) for
details.

### Local Maven

Clone the repository and publish it to your local Maven repository (`~/.m2`). The bundled Gradle
wrapper downloads its own Gradle, so no local Gradle install is needed:

```bash
git clone https://github.com/dfns/dfns-sdk-kotlin.git
cd dfns-sdk-kotlin
./gradlew :androidsdk:publishToMavenLocal
```

This installs `co.dfns:androidsdk:1.0.0` into `~/.m2/repository`. Unlike Maven, Gradle does not
search the local repository by default, so add `mavenLocal()` to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}
```

Then add the dependency:

```kotlin
dependencies {
    implementation("co.dfns:androidsdk:1.0.0")
}
```

Transitive dependencies (`androidx.credentials`, Gson) are declared in the published POM and resolved
automatically. Re-run `./gradlew :androidsdk:publishToMavenLocal` after pulling a newer version of the
SDK.

### From the source code

You can skip the publish step and build the SDK from a cloned copy as part of your own build, using a
[composite build](https://docs.gradle.org/current/userguide/composite_builds.html). This needs no
local Maven repository. It also works well in CI: add the SDK as a git submodule pinned to a release
tag, and your build resolves it reproducibly with no registry.

Clone the repository into your project (for example as a git submodule), then reference it from your
`settings.gradle.kts`:

```kotlin
includeBuild("third-party/dfns-sdk-kotlin")
```

Depend on it without a version. Gradle builds the SDK from source and substitutes it for the
dependency, resolving its transitive dependencies automatically:

```kotlin
dependencies {
    implementation("co.dfns:androidsdk")
}
```

On Android 13 and below, also add `androidx.credentials:credentials-play-services-auth` (see
[Requirements](#requirements)).

## Concepts

### `PasskeysSigner`

All state-changing requests made to the Dfns API need to be cryptographically signed by credentials
registered with the User.

> **Note:** To be more precise, it's not the request itself that needs to be signed, but rather a "
> User Action Challenge" issued by Dfns. For simplicity, we refer to this process as "request
> signing".

This request signature serves as cryptographic proof that only authorized entities are making the
request. Without it, the request would result in an Unauthorized error.

While implementing an Android application your backend server will have to communicate with the DFNS API to retrieve this challenge and pass it to your application, `PasskeysSigner` will be used to register and authenticate a user.

```
val passkeysSigner = PasskeysSigner(context, RelyingParty(id, name))
```

#### Register

```
val fido2Attestation = passkeysSigner.create(challenge)
```

#### Sign

```
val fido2Assertion = passkeysSigner.sign(challenge)
```

## DfnsDemo

A demo application using the SDK can be found [here](https://github.com/dfns/dfns-sdk-kotlin/tree/main/app). This demo application is to be used in conjunction with the server in [delegated registration and login tutorial](https://github.com/dfns/dfns-sdk-ts/tree/m/examples/sdk/auth-delegated#mobile-frontend). It is a replacement for the `Android` section over there, you should read and execute all instruction written above this section to get this demo running.

#### Configuration

In the `./app/src/main/java/co/dfns/sdk/tutorial/mobile/Constants.kt` set the following values,

* `SERVER_BASE_URL`: base url of the server you launched (eg `http://localhost:8000`, or if using ngrok, the public ngrok url)
* `PASSKEY_RELYING_PARTY_ID`: the passkey relying party id, aka, the domain on which the above server is served ((Read more [here](https://developer.mozilla.org/en-US/docs/Web/API/PublicKeyCredentialCreationOptions#rp))). If serving the server on `http://localhost:8000`, then set it to `localhost`. If serving the server through ngrok (eg `https://d0d7-31-217-63-194.ngrok-free.app`), then set it to `ngrok-free.app`. In general, we advise you use the root domain (eg. `acme.com`, not `app.acme.com`) for more passkey flexibility (so that passkey is re-usable on subdomains).
* `PASSKEY_RELYING_PARTY_NAME`: A string representing the name of the relying party, aka, your company name (e.g. "Acme"). The user will be presented with that name when creating or using a passkey.

