package at.florianschuster.playables.controller

import androidx.annotation.RestrictTo

object ControllerLib {
    private var escalateCrashes: Boolean = false
    private var errorHandler: ((Throwable) -> Unit)? = null

    /**
     * Handles error messages, which are swallowed by the state stream by default.
     * Change escalateCrashes to true only when you know what you are doing!
     *
     * @param escalateCrashes Boolean When set to true, certain `Reactor` components crash on errors
     * @param handler (Throwable) -> Unit Is called when
     */
    fun onErrors(
        escalateCrashes: Boolean = false,
        handler: ((Throwable) -> Unit)? = null
    ) {
        this.escalateCrashes = escalateCrashes
        this.errorHandler = handler
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun handleError(throwable: Throwable) {
        when {
            escalateCrashes -> throw throwable
            else -> errorHandler?.invoke(throwable)
        }
    }
}