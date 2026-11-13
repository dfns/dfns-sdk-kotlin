package co.dfns.androidsdk

/** Thrown when the user dismisses the passkey sheet. */
class PasskeyCancellationException(cause: Throwable) : Exception(
    cause.message ?: "The passkey prompt was dismissed",
    cause,
)
