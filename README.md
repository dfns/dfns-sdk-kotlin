# Dfns Android SDK

Welcome, builders 👋🔑 This repo holds Dfns Android SDK. Useful links:

- [Dfns Website](https://www.dfns.co)
- [Dfns API Docs](https://docs.dfns.co)

## BETA Warning

:warning: **Attention: This project is currently in BETA.**

This means that while we've worked hard to ensure its functionality there may still be bugs,
performance issues, or unexpected behavior.

## Installation

TBC

## Concepts

### `PasskeysSigner`

All state-changing requests made to the Dfns API need to be cryptographically signed by credentials
registered with the User.

> **Note:** To be more precise, it's not the request itself that needs to be signed, but rather a "
> User Action Challenge" issued by Dfns. For simplicity, we refer to this process as "request
> signing".

This request signature serves as cryptographic proof that only authorized entities are making the
request. Without it, the request would result in an Unauthorized error.
While implementing an iOS application your backend server will have to communicate with the DFNS API
to retrieve this challenge and pass it to your application, `PasskeysSigner` will be used to
register
and authenticate a user.

```
val passkeysSigner = PasskeysSigner(context)
```

#### Register

```
val fido2Attestation = passkeysSigner.register(challenge)
```

#### Sign

```
val fido2Assertion = passkeysSigner.sign(challenge)
```

## DfnsDemo

A demo application using the SDK can be
found [here](https://github.com/dfns/dfns-sdk-kotlin/tree/main/app). This demo application is to be
used in conjunction with
the [delegated registration and login tutorial](https://github.com/dfns/dfns-sdk-ts/tree/m/examples/sdk/auth-delegated#mobile-frontend).
It is a replacement for the `Android` section, you should read and execute all instruction written
above
this section to get this demo running.

#### Prerequisites

To run the demo application on an iOS device, you must have an `Application` for Android. To create
a
new `Application`, go
to `Dfns Dashboard` > `Settings` > `Org Settings` > `Applications` > `New Application`, and enter
the following information

- Name, choose any name, for example `Dfns Tutorial Android`
- Application Type, leave as the default `Default Application`
- Relying Party, set to the domain you associated with the app, e.g. `panda-new-kit.ngrok-free.app`
- Origin, the Android format is android:apk-key-hash:<sha256_hash-of-apk-signing-cert>. For this tutorial app, the signing cert is fixed, the value is android:apk-key-hash:-sYXRdwJA3hvue3mKpYrOZ9zSPC7b4mbgzJmdZEDO5w. For your own application, follow [Android's guide](https://developer.android.com/training/sign-in/passkeys#verify-origin) to derive the correct origin.

After the `Application` is created, copy and save the `App ID`,
e.g. `ap-39abb-5nrrm-9k59k0u3jup3vivo`.

#### Configuration

In the `./app/src/main/java/co/dfns/sdk/tutorial/mobile/Constants.kt` set the following values,

- `appId` = the `App ID` of the new `Application`
- `url` = either `http://localhost:8000` or if using ngrok, the public
  url `https://panda-new-kit.ngrok-free.app`


# Publishing
Uses [tddworks/central-portal-publisher](https://github.com/tddworks/central-portal-publisher) and requires signing + env var export for maven central auth tokens
```
## Provide Sonatype Portal credentials
SONATYPE_USERNAME=[your-sonatype-username]
SONATYPE_PASSWORD=[your-sonatype-password]
```