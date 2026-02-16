package com.alexfu.axiomui.command

import com.alexfu.axiomui.state.loading.LoadState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo

class LoadCommandTest {
    @Test
    fun testSuccess() {
        runTest {
            val command = createCommand(
                loadData = { input -> input * 100 }
            )

            val states = command(1)
                .runningFold(TestState()) { state, action -> action(state) }
                .toList()

            expectThat(states)
                .isEqualTo(
                    listOf(
                        TestState(),
                        TestState(loadState = LoadState.Loading),
                        TestState(counter = 100, loadState = LoadState.Success),
                    )
                )
        }
    }

    @Test
    fun testFailure() {
        runTest {
            val exception = IllegalArgumentException()
            val command = createCommand(
                loadData = { _ -> throw exception }
            )

            val states = command(1)
                .runningFold(TestState()) { state, action -> action(state) }
                .toList()

            expectThat(states)
                .isEqualTo(
                    listOf(
                        TestState(),
                        TestState(loadState = LoadState.Loading),
                        TestState(loadState = LoadState.Error(exception)),
                    )
                )
        }
    }

    @Test
    fun testCancel() {
        runTest {
            val command = createCommand(
                loadData = { _ -> throw CancellationException() }
            )

            expectThrows<CancellationException> {
                command(1).collect { /**/ }
            }
        }
    }

    private fun createCommand(loadData: suspend (Int) -> Int): LoadCommand<TestState, Int, Int> = LoadCommand(
        loadData = loadData,
        loadStateReducer = { copy(loadState = it) },
        dataReducer = { copy(counter = it) }
    )
}

data class TestState(
    val counter: Int = 0,
    val loadState: LoadState = LoadState.Initial
)
