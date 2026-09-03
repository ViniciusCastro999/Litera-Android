package com.litera.app.testutil

import android.util.Patterns
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.regex.Pattern

/**
 * android.util.Patterns is an Android framework stub on the JVM unit-test
 * classpath (calling it throws "Method ... not mocked"). Any use case that
 * validates e-mails with Patterns.EMAIL_ADDRESS (see AuthUseCases) needs
 * this rule so the real RFC-ish regex backs it during the test instead.
 */
class AndroidPatternsRule : TestWatcher() {

    private val emailPattern: Pattern = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )

    override fun starting(description: Description) {
        mockkStatic(Patterns::class)
        every { Patterns.EMAIL_ADDRESS } returns emailPattern
    }

    override fun finished(description: Description) {
        unmockkStatic(Patterns::class)
    }
}
