package com.litera.app.testutil

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Swaps Dispatchers.Main for a test dispatcher so ViewModels using
 * viewModelScope (which runs on Dispatchers.Main by default) can be
 * unit tested on the JVM, where Dispatchers.Main is otherwise unset.
 *
 * Uses an unconfined dispatcher by default so `viewModelScope.launch { ... }`
 * blocks (init-time Flow collection, button-click actions) run eagerly and
 * synchronously — matching how these ViewModel tests assert state right
 * after calling a method, without manually driving a scheduler.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
