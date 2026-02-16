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
 *
 * @param STATE The type of state that this command affects.
 * @param T The type of input used to start the command (commonly a params object).
 */
interface Command<STATE, T> {
    operator fun invoke(input: T): Flow<Action<STATE>>
}
