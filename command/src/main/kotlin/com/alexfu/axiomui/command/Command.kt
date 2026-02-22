package com.alexfu.axiomui.command

import com.alexfu.axiomui.state.Action
import kotlinx.coroutines.flow.Flow

/**
 * Represents a long-running unit of work that produces state transitions over time.
 *
 * A `Command` encapsulates I/O or other asynchronous operations whose results
 * should affect application state. The command is invoked with an [input] value
 * (often a params object) and returns a stream of [Action]s describing how the
 * state should evolve throughout the command’s lifecycle.
 *
 * Commands should not modify state directly. Instead, they communicate intent
 * exclusively through emitted [Action]s, enabling a unidirectional data flow model.
 *
 * The returned flow may emit multiple actions (e.g. start/loading, progress,
 * success, failure) and completes when the command finishes or is cancelled.
 */
interface Command<STATE, INPUT> {
    operator fun invoke(input: INPUT): Flow<Action<STATE>>
}

/**
 * Invokes a [Command] that takes no input.
 *
 * This is a convenience overload for `Command<STATE, Unit>` so callers can write
 * `command()` instead of `command(Unit)`.
 *
 * @return A [Flow] of state [Action]s emitted by the command.
 */
operator fun <STATE> Command<STATE, Unit>.invoke(): Flow<Action<STATE>> {
    return invoke(Unit)
}
