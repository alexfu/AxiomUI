package com.alexfu.axiomui.state

import kotlinx.coroutines.flow.Flow

/**
 * Collects this [Flow] and reduces each emitted value into the state
 * via the provided [reducer].
 *
 * @param store The [Store] whose state will be updated.
 * @param reducer A function describing how each emitted value transforms the current state.
 */
suspend fun <T, STATE: Any> Flow<T>.reduceInto(store: Store<STATE>, reducer: Reducer<STATE, T>) {
    collect { item ->
        store.update { reducer(item) }
    }
}

suspend fun <STATE: Any> Flow<Action<STATE>>.collectInto(store: Store<STATE>) {
    collect { action ->
        store.update(action)
    }
}
