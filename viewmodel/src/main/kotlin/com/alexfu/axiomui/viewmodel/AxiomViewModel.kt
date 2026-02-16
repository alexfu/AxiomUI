package com.alexfu.axiomui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexfu.axiomui.command.Command
import com.alexfu.axiomui.state.Reducer
import com.alexfu.axiomui.state.Store
import com.alexfu.axiomui.state.reduceInto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Base [ViewModel] that owns a single [Store] representing its UI state.
 */
abstract class AxiomViewModel<STATE: Any>(val store: Store<STATE>) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun <R> runCommandOn(selector: Flow<STATE>.() -> Flow<R>, command: Command<STATE, R>) {
        store.observe()
            .let(selector)
            .flatMapLatest { input ->
                 command(input)
            }
            .onEach { action ->
                store.update(action)
            }
            .launchIn(viewModelScope)
    }

    fun <T> runCommand(command: Command<STATE, T>, input: T) {
        viewModelScope.launch {
            command(input)
                .collect { action ->
                    store.update(action)
                }
        }
    }

    fun <T> Flow<T>.reduceIntoState(reducer: Reducer<STATE, T>) {
        viewModelScope.launch {
            reduceInto(store, reducer)
        }
    }
}
