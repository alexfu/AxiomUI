package com.alexfu.axiomui.state

/**
 * Represents a state transition driven by an input value.
 *
 * A `Reducer` describes how a [STATE] evolves in response to an input of type [T].
 * The current state is provided as the receiver (`this`), and the input value is
 * passed as the parameter. The function must return the next state.
 *
 * Reducers are intended to be pure transformations:
 * - They should not mutate the current state
 * - They should return a new state derived from the receiver
 * - They should avoid side effects
 *
 * ### Example
 * ```
 * data class Counter(val number: Int)
 *
 * val increment: Reducer<Counter, Int> = { delta ->
 *     copy(number = number + delta)
 * }
 * ```
 *
 * In this example, the existing `Counter` instance is the receiver and
 * `delta` is the input that drives the state transition.
 */
typealias Reducer<STATE, T> = STATE.(T) -> STATE
