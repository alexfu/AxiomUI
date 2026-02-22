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

/**
 * Combines multiple [Action]s into a single [Action].
 *
 * The returned action applies each action in [actions] from left to right,
 * passing the result of one action as the input state to the next.
 *
 * If [actions] is empty, the returned action behaves as a no-op and returns
 * the original state unchanged.
 *
 * @param actions The ordered list of actions to apply.
 * @return A single action representing the sequential composition of [actions].
 */
fun <STATE> compositeAction(actions: List<Action<STATE>>): Action<STATE> {
    return {
        actions.fold(this) { prevState, action -> action(prevState) }
    }
}
