package com.alexfu.axiomui.state

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * A minimal, generic state container.
 *
 * `Store` holds a single state value of type [T] and provides:
 *
 * - Synchronous access to the current state via [state]
 * - Reactive observation via [observe]
 * - Safe state transitions via [update]
 *
 * This class is designed for unidirectional data flow patterns where state
 * changes are expressed as pure transformation functions (see [Action]).
 *
 * @param initialState The initial state value.
 */
open class Store<T : Any>(initialState: T) {
    private val stateFlow = MutableStateFlow(initialState)

    /**
     * The current state value.
     *
     * This provides a synchronous snapshot of the latest state.
     */
    val state: T
        get() = stateFlow.value

    /**
     * Returns a [Flow] that emits the current state and all subsequent updates.
     */
    fun observe(): Flow<T> {
        return stateFlow
    }

    /**
     * Updates the state by applying the provided [action] to the current state.
     *
     * The action should be pure and return a new state derived from the previous one.
     *
     * @param action A function that transforms the current state into the next state.
     */
    fun update(action: Action<T>) {
        stateFlow.update { state -> action(state) }
    }

    /**
     * Updates the state by applying the provided [actions] to the current state.
     *
     * The action should be pure and return a new state derived from the previous one.
     *
     * @param actions A list of functions that transforms the current state into the next state.
     */
    fun update(actions: List<Action<T>>) {
        stateFlow.update(compositeAction(actions))
    }
}
