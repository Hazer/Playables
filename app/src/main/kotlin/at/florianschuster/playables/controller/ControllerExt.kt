package at.florianschuster.playables.controller

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.onEach

fun <T> Flow<T>.bind(
    to: suspend (T) -> Unit
): Flow<T> {
    return catch {
        Control.handleError(it)
        emitAll(emptyFlow())
    }.onEach { to(it) }
}