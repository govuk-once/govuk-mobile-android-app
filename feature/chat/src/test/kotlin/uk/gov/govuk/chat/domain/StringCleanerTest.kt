package uk.gov.govuk.chat.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

@RunWith(Enclosed::class)
class StringCleanerTest {
    class IncludesPIITest {
        /*
         * Based on: https://github.com/alphagov/govuk-chat/blob/main/spec/validators/pii_validator_spec.rb
         */
        @Test
        fun `when the input does not contain any personal information`() {
            val question = "How much VAT do I have to pay?"
            assertFalse(StringCleaner.includesPII(question))
        }

        @Test
        fun `when the input contains an email address`() {
            val emailAddresses = listOf(
                "test@gmail.com",
                "test@localhost",
                "test.user@yahoo.co.uk",
            )

            emailAddresses.forEach { emailAddress ->
                val question = "My email address is $emailAddress"
                assertTrue(StringCleaner.includesPII(question))
            }
        }

        @Test
        fun `when the input contains a credit card number`() {
            val creditCardNumbers = listOf(
                1234567890123,
                12345678901234,
                123456789012345,
                1234567890123456,
            )

            creditCardNumbers.forEach { creditCardNumber ->
                val question = "My credit card number is $creditCardNumber"
                assertTrue(StringCleaner.includesPII(question))
            }
        }

        @Test
        fun `returns true when the it contains a normal credit card number`() {
            val question = "My credit card number is 1234 5678 9012 3456"
            assertTrue(StringCleaner.includesPII(question))
        }

        @Test
        fun `returns true when the it contains an AMEX credit card number`() {
            val question = "My credit card number is 1234 567890 12345"
            assertTrue(StringCleaner.includesPII(question))
        }

        @Test
        fun `when the input contains a uk or international phone number`() {
            val phoneNumbers = listOf(
                "07555666777",
                "(01234)555666",
                "01234 555666",
                "(01234) 555666",
                "+441234567890",
                "+(44)1234567890",
                "+44 1234567890",
                "+(44) 1234567890",
                "+44 1234 567890",
                "+(44) 1234 567890",
                "+44 1234 567 890",
                "+(44) 1234 567 890",
                "+11234567",
                "+112345678",
                "+1123456789",
                "+11234567890",
                "+121234567890",
                "+1231234567890",
                "+(123)1234567890",
                "+1 1234567",
                "+1 12345678",
                "+1 123456789",
                "+1 1234567890",
                "+12 1234567890",
                "+123 1234567890",
                "+(123) 1234567890",
                "+1 123 4567890",
                "+(123) 123 4567",
                "+1-123-4567890",
                "+(123)-123-4567",
                "+1.123.4567890",
                "+(123).123.4567",
            )

            phoneNumbers.forEach { phoneNumber ->
                val question = "My phone number is $phoneNumber"
                assertTrue(StringCleaner.includesPII(question))
            }
        }

        @Test
        fun `when the input contains a national insurance number`() {
            val niNumbers = listOf(
                "AB 12 34 56 A",
                "AB123456A",
                "AB 123 456 A",
                "AB 123 456A",
                "AB123456 A",
                "AB 123456A"
            )

            niNumbers.forEach { niNumber ->
                val question = "My ni number is $niNumber"
                assertTrue(StringCleaner.includesPII(question))
            }
        }
    }
}
