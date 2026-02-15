package com.alexfu.axiomui.state

/**
 * A state transformation for a [Store].
 *
 * An `Action` is a function that takes the current state as its receiver
 * and returns the next state. It describes how state evolves, not what
 * side effects occur.
 *
 * Actions should:
 * - Treat the current state as immutable
 * - Return a new state derived from the current one
 * - Avoid side effects
 *
 * ### Example
 * ```
 * data class Counter(val value: Int)
 *
 * val increment: Action<Counter> = {
 *     copy(value = value + 1)
 * }
 * ```
 *
 * In this example, the current `Counter` instance is the receiver,
 * and `copy(...)` produces the next state.
 */
typealias Action<STATE> = STATE.() -> STATE
