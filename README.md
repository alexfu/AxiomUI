# AxiomUI

AxiomUI is a small set of Kotlin libraries for unidirectional UI state management on Android.
It is split into focused modules for state containers, async commands, and ViewModel orchestration.

API reference: [https://alexfu.github.io/AxiomUI/index.html](https://alexfu.github.io/AxiomUI/index.html)

## Modules

- `:state`: core state primitives (`Store`, `Action`, `Reducer`) and flow helpers.
- `:command`: async command abstraction (`Command`) and a built-in loading command (`LoadCommand`).
- `:viewmodel`: `AxiomViewModel` for wiring commands and reducers into `viewModelScope`.

## Why AxiomUI

- Simple state transitions with pure functions.
- Async work modeled as streams of state actions.
- Works naturally with Kotlin Coroutines and Flow.
- Minimal API surface and easy composition.

## Requirements

- JDK 17
- Android Gradle Plugin / Kotlin versions defined in `gradle/libs.versions.toml`

## Quick Start

### 1. Create state

```kotlin
data class CounterState(
    val value: Int = 0,
    val isLoading: Boolean = false
)

val store = Store(CounterState())
store.update { copy(value = value + 1) }
```

### 2. Define a command

```kotlin
class LoadNextValue : Command<CounterState, Unit> {
    override fun invoke(input: Unit) = flow<Action<CounterState>> {
        emit { copy(isLoading = true) }
        delay(300)
        emit { copy(value = value + 1, isLoading = false) }
    }
}
```

### 3. Run from a ViewModel

```kotlin
class CounterViewModel : AxiomViewModel<CounterState>(Store(CounterState())) {
    fun load() {
        runCommand(LoadNextValue())
    }
}
```

## Build

```bash
./gradlew build
```

## Test

```bash
./gradlew test
```
