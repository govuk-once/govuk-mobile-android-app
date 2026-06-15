package uk.gov.govuk.dvla.util

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class StringProviderImplTest {
    private val context = mockk<Context>(relaxed = true)
    private val stringProvider = StringProviderImpl(context)

    @Test
    fun `Given a resource id but no arguments, when getString is called, then do not get the string from context`() {
        val resId = 123
        val expectedString = "expected"
        every { context.getString(resId) } returns expectedString

        verify(exactly = 0) { context.getString(resId) }
    }

    @Test
    fun `Given a resource id and a single argument, when getString is called, then return the formatted string from context`() {
        val resId = 123
        val argument = "argument"
        val expectedString = "expected argument"
        every { context.getString(resId, argument) } returns expectedString

        val result = stringProvider.getString(resId, argument)

        assertEquals(expectedString, result)
        verify(exactly = 1) { context.getString(resId, argument) }
    }

    @Test
    fun `Given a resource id and multiple arguments, when getString is called, then return the formatted string from context`() {
        val resId = 123
        val argument1 = "argument"
        val argument2 = 999
        val expectedString = "expected argument 999"
        every { context.getString(resId, argument1, argument2) } returns expectedString

        val result = stringProvider.getString(resId, argument1, argument2)

        assertEquals(expectedString, result)
        verify(exactly = 1) { context.getString(resId, argument1, argument2) }
    }
}
