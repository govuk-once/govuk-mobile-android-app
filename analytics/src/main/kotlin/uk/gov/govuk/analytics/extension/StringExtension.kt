package uk.gov.govuk.analytics.extension

fun String.redactPii(): String {
    // Postcode
    val postcode = "([A-Za-z]{1,2}[0-9]{1,2}[A-Za-z]? *[0-9][A-Za-z]{2})"
    var output = Regex(postcode).replace(this, "[postcode]")

    // Email
    val email = "[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
    output = Regex(email).replace(output, "[email]")

    // NI number
    val niNumber = "[A-Za-z]{2} *[0-9]{2} *[0-9]{2} *[0-9]{2} *[A-Za-z]"
    output = Regex(niNumber).replace(output, "[NI number]")

    return output
}