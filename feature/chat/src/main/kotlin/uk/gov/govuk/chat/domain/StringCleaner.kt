package uk.gov.govuk.chat.domain

import kotlin.text.toRegex

object StringCleaner {
    /*
     * Based on: https://github.com/alphagov/govuk-chat/blob/main/app/validators/pii_validator.rb
     */
    private val emailRegex = "[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*".toRegex(RegexOption.MULTILINE)
    private val creditCardRegex = "[0-9]{13,16}|[0-9]{4} [0-9]{4} [0-9]{4} [0-9]{4}|[0-9]{4} [0-9]{6} [0-9]{5}".toRegex(RegexOption.MULTILINE)
    private val phoneNumberRegex = "(?:\\+?([0-9]{1,3}))? ?[-.(]?([0-9]{3,5})?[-.)]? ?([0-9]{3})[-. ]?([0-9]{3,4})".toRegex(RegexOption.MULTILINE)
    private val nationalInsuranceNumberRegex = "[A-Za-z]{2} ?([0-9 ]+){6,8} ?[A-Za-z]".toRegex(RegexOption.MULTILINE)
    private val piiRegexes = listOf(
        emailRegex,
        creditCardRegex,
        phoneNumberRegex,
        nationalInsuranceNumberRegex
    )

    fun includesPII(string: String): Boolean {
        piiRegexes.forEach { regex ->
            if (regex.containsMatchIn(string)) {
                return true
            }
        }
        return false
    }
}
