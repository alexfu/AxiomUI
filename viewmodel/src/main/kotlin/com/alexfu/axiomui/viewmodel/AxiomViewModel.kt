package com.alexfu.axiomui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexfu.axiomui.command.Command
import com.alexfu.axiomui.state.Reducer
import com.alexfu.axiomui.state.Store
import com.alexfu.axiomui.state.reduceInto
import com.alexfu.axiomui.viewmodel.CommandCollectionMode.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.subscribeOn
import kotlinx.coroutines.launch

/**
 * Base [ViewModel] that owns a single [Store] representing its UI state.
 */
abstract class AxiomViewModel<STATE: Any>(val store: Store<STATE>) : ViewModel() {
    /**
     * Observes inputs derived from state and runs [command] with latest-only semantics.
     *
     * Inputs are produced by applying [selector] to [Store.observe]. When a new input is emitted,
     * the previous in-flight command flow is cancelled and replaced (`flatMapLatest` semantics).
     * Emitted actions are applied to [store].
     *
     * @param selector Builds an input [Flow] from the state stream.
     * @param command Command to execute for each selected input value.
     * @return The launched [Job] tied to [viewModelScope].
     */
    fun <R> runLatestCommandOn(selector: (Flow<STATE>) -> Flow<R>, command: Command<STATE, R>): Job {
        return runCommandOn(selector = selector, command = command, mode = LATEST)
    }

    /**
     * Observes inputs derived from state and runs [command] with sequential semantics.
     *
     * Inputs are produced by applying [selector] to [Store.observe]. Each emitted input starts a
     * command flow that is queued behind earlier ones (`flatMapConcat` semantics). Emitted actions
     * are applied to [store].
     *
     * @param selector Builds an input [Flow] from the state stream.
     * @param command Command to execute for each selected input value.
     * @return The launched [Job] tied to [viewModelScope].
     */
    fun <R> runCommandOn(selector: (Flow<STATE>) -> Flow<R>, command: Command<STATE, R>): Job {
        return runCommandOn(selector = selector, command = command, mode = CONCAT)
    }

    /**
     * Runs [command] once with the provided [input] and applies all emitted actions to [store].
     *
     * @param command Command to execute.
     * @param input Input passed to the command.
     * @return The launched [Job] tied to [viewModelScope].
     */
    fun <T> runCommand(command: Command<STATE, T>, input: T): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            command(input)
                .collect { action ->
                    store.update(action)
                }
        }
    }

    /**
     * Runs a no-input [command] and applies all emitted actions to [store].
     *
     * This overload is equivalent to calling `runCommand(command, Unit)`.
     *
     * @param command Command to execute.
     * @return The launched [Job] tied to [viewModelScope].
     */
    fun runCommand(command: Command<STATE, Unit>): Job {
        return runCommand(command, Unit)
    }

    /**
     * Observes a derived input stream from state and runs [command] for each new input.
     *
     * Inputs are produced by applying [selector] to [Store.observe]. Collection behavior depends
     * on [mode]:
     * - [CONCAT] queues command flows and runs them sequentially (`flatMapConcat` semantics).
     * - [LATEST] cancels the previous in-flight command flow and switches to the newest one
     *   (`flatMapLatest` semantics).
     *
     * Emitted actions are applied to [store].
     *
     * @param selector Builds an input [Flow] from the state stream.
     * @param command Command to execute for each selected input value.
     * @param mode Command collection strategy for command flows derived from selected inputs.
     * @return The launched [Job] tied to [viewModelScope].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <R> runCommandOn(selector: (Flow<STATE>) -> Flow<R>, command: Command<STATE, R>, mode: CommandCollectionMode = CONCAT): Job {
        return store.observe()
            .let(selector)
            .let { flow ->
                val newFlow = when (mode) {
                    CONCAT -> flow.flatMapConcat { input -> command(input) }
                    LATEST -> flow.flatMapLatest { input -> command(input) }
                }
                newFlow.flowOn(Dispatchers.IO)
            }
            .onEach { action ->
                store.update(action)
            }
            .launchIn(viewModelScope)
    }

    /**
     * Collects this flow and reduces each emission into [store] using [reducer].
     *
     * @receiver A stream of values to fold into state.
     * @param reducer Transforms current state and emitted value into next state.
     */
    fun <T> Flow<T>.reduceIntoState(reducer: Reducer<STATE, T>) {
        viewModelScope.launch {
            reduceInto(store, reducer)
        }
    }
}

private enum class CommandCollectionMode { CONCAT, LATEST }
