package at.florianschuster.playables.controller

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

fun <T> Flow<T>.bind(
    action: suspend (T) -> Unit
): Flow<T> = catch { e: Throwable -> Timber.e(e) }.onCompletion {  }.onEach { action(it) }
