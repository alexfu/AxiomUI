package com.alexfu.axiomui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexfu.axiomui.state.Reducer
import com.alexfu.axiomui.state.Store
import com.alexfu.axiomui.state.reduceInto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Base [ViewModel] that owns a single [Store] representing its UI state.
 */
abstract class AxiomViewModel<STATE: Any>(val store: Store<STATE>) : ViewModel() {
    fun <T> Flow<T>.reduceIntoState(reducer: Reducer<STATE, T>) {
        viewModelScope.launch {
            reduceInto(store, reducer)
        }
    }
}
