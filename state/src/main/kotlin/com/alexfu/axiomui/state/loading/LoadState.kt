package com.alexfu.axiomui.state.loading

/**
 * Represents the lifecycle status of a load operation so the UI can react appropriately.
 *
 * `LoadState` is intentionally lightweight: it describes *where the load is in its lifecycle*
 * (never loaded, loading, succeeded, failed).
 *
 * Error details are exposed as a raw [Throwable] ([Error.cause]) so the ViewModel can translate
 * failures into meaningful, user-facing UI messaging and behaviors (e.g., retry visibility,
 * error banners, analytics).
 */
sealed class LoadState {

    /**
     * No successful load has completed yet.
     *
     * This is the initial state used to communicate that the UI has never received loaded data.
     */
    data object Initial : LoadState()

    /**
     * A load operation is currently in progress.
     */
    data object Loading : LoadState()

    /**
     * At least one load operation has completed successfully.
     *
     * This state is expected to remain active until a subsequent load attempt changes it.
     */
    data object Success : LoadState()

    /**
     * The most recent load operation failed.
     *
     * The raw [cause] is provided for the ViewModel to interpret and convert into user-facing UI.
     *
     * @property cause The underlying error that caused the load to fail.
     */
    data class Error(val cause: Throwable) : LoadState()
}
