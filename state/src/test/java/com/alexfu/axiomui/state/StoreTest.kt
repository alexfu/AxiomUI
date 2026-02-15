package com.alexfu.axiomui.state

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class StoreTest {
    @Test
    fun simple_test() {
        runTest {
            val store = Store(Counter(number = 66))
            store.update { copy(number = number + 1) }
            expectThat(store.state.number).isEqualTo(67)
        }
    }

    @Test
    fun concurrency_test() {
        runTest {
            val store = Store(Counter(number = 66))

            coroutineScope {
                repeat(1_000) {
                    launch {
                        store.update { copy(number = number + 1) }
                    }
                }
            }

            expectThat(store.state.number).isEqualTo(66 + 1_000)
        }
    }
}

data class Counter(val number: Int)
