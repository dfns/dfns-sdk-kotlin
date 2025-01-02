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

While implementing an Android application your backend server will have to communicate with the DFNS API to retrieve this challenge and pass it to your application, `PasskeysSigner` will be used to register and authenticate a user.

```
val passkeysSigner = PasskeysSigner(context, RelyingParty(id, name))
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

A demo application using the SDK can be found [here](https://github.com/dfns/dfns-sdk-kotlin/tree/main/app). This demo application is to be used in conjunction with the server in [delegated registration and login tutorial](https://github.com/dfns/dfns-sdk-ts/tree/m/examples/sdk/auth-delegated#mobile-frontend). It is a replacement for the `Android` section over there, you should read and execute all instruction written above this section to get this demo running.

#### Configuration

In the `./app/src/main/java/co/dfns/sdk/tutorial/mobile/Constants.kt` set the following values,

* `DFNS_APP_ID`: Dfns Application ID (grab one in Dfns Dashboard: `Settings` > `Applications`)
* `SERVER_BASE_URL`: base url of the server you launched (eg `http://localhost:8000`, or if using ngrok, the public ngrok url)
* `PASSKEY_RELYING_PARTY_ID`: the passkey relying party id, aka, the domain on which the above server is served ((Read more [here](https://developer.mozilla.org/en-US/docs/Web/API/PublicKeyCredentialCreationOptions#rp))). If serving the server on `http://localhost:8000`, then set it to `localhost`. If serving the server through ngrok (eg `https://d0d7-31-217-63-194.ngrok-free.app`), then set it to `ngrok-free.app`. In general, we advise you use the root domain (eg. `acme.com`, not `app.acme.com`) for more passkey flexibility (so that passkey is re-usable on subdomains).
* `PASSKEY_RELYING_PARTY_NAME`: A string representing the name of the relying party, aka, your company name (e.g. "Acme"). The user will be presented with that name when creating or using a passkey.

