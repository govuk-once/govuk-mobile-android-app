package uk.gov.govuk.notifications.data

/**
 * Logged on app start when what the OneSignal SDK saved from previous sessions doesn't match a
 * user who has consented, i.e. the device isn't properly linked to their external ID. Pushes sent
 * to that user then fail ("can't find the ID" or "not subscribed").
 *
 * The app logs in again straight after the check, which usually repairs the state. That
 * self-healing is what has made these failures invisible, so this records them first.
 *
 * The message holds flags only, so a report never contains an ID. pushOptedIn is there for
 * context: it depends on the OS permission, which the user is free to turn off, so on its own it
 * doesn't mean anything is wrong.
 */
internal class InconsistentOneSignalStateException(
    externalIdSet: Boolean,
    externalIdMatches: Boolean,
    onesignalIdSet: Boolean,
    pushSubscriptionIdSet: Boolean,
    pushOptedIn: Boolean
) : Exception(
    "OneSignal state inconsistent on app start: " +
        "externalIdSet=$externalIdSet, " +
        "externalIdMatches=$externalIdMatches, " +
        "onesignalIdSet=$onesignalIdSet, " +
        "pushSubscriptionIdSet=$pushSubscriptionIdSet, " +
        "pushOptedIn=$pushOptedIn"
)
