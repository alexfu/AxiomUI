package com.alexfu.axiomui.command

import com.alexfu.axiomui.state.Action
import com.alexfu.axiomui.state.Reducer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * A [Command] that performs a load operation and emits [Action]s to update the state
 *
 * This command is intended for long-running I/O work (network, disk, etc.). When invoked, it:
 * - Marks the load as in progress via [LoadState.Loading]
 * - Executes [loadData] using the provided input
 * - On success, marks the load as [LoadState.Success] and applies the loaded [DATA] via [dataReducer]
 * - On failure, marks the load as [LoadState.Error]
 *
 * Cancellation is treated as control flow: if the coroutine is cancelled, the cancellation exception
 * is rethrown rather than being converted into an error state.
 */
class LoadCommand<STATE, INPUT, DATA>(
    private val loadData: suspend (INPUT) -> DATA,
    private val loadStateReducer: Reducer<STATE, LoadState>,
    private val dataReducer: Reducer<STATE, DATA>
) : Command<STATE, INPUT> {
    override fun invoke(input: INPUT): Flow<Action<STATE>> {
        return flow {
            emit { loadStateReducer(LoadState.Loading) }
            try {
                val data = loadData(input)
                emit { dataReducer(loadStateReducer(LoadState.Success), data) }
            } catch (error: Throwable) {
                if (error is CancellationException) throw error
                emit { loadStateReducer(LoadState.Error(error)) }
            }
        }
    }
}
