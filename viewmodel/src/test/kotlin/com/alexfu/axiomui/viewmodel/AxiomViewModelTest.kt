package com.alexfu.axiomui.viewmodel

import com.alexfu.axiomui.command.LoadCommand
import com.alexfu.axiomui.state.Store
import com.alexfu.axiomui.state.loading.LoadState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEqualTo

class AxiomViewModelTest {
    private lateinit var store: Store<TestState>
    private lateinit var viewModel: TestViewModel

    @JvmField @Rule
    val dispatcherRule = MainDispatcherRule()

    @Before
    fun before() {
        store = Store(TestState())
        viewModel = TestViewModel(store)
    }

    @Test
    fun runCommand() = runTest {
        val job = viewModel.runCommand(AppendToCompletedCommands, "command-1")
        job.join()

        expectThat(store.state.completedCommands).isEqualTo(listOf("command-1"))
    }

    @Test
    fun runCommandOn_cancelsPreviousCommandAndAppliesLatestResult() = runTest {
        val job = viewModel.runLatestCommandOn(
            selector = {
                map { state -> state.counter }
                    .distinctUntilChanged()
                    .map { counter ->
                        "command-${counter}"
                    }
                    .take(2) // 1 for the initial emission, 1 for when counter is updated.
            },
            command = AppendToCompletedCommands
        )

        runCurrent()
        store.update { copy(counter = counter + 1) }
        job.join()

        expectThat(store.state.completedCommands).containsExactly("command-1")
    }
}

private data class TestState(
    val loadState: LoadState = LoadState.Initial,
    val completedCommands: List<String> = emptyList(),
    val counter: Int = 0,
)

private class TestViewModel(store: Store<TestState>) : AxiomViewModel<TestState>(store)

private object AppendToCompletedCommands : LoadCommand<TestState, String, String>(
    loadData = { name ->
        delay(200)
        name
    },
    loadStateReducer = { copy(loadState = it) },
    dataReducer = { copy(completedCommands = completedCommands + it) }
)

class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
