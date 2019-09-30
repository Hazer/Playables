package at.florianschuster.playables.controller

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

sealed class Data<out T> {
    open operator fun invoke(): T? = null

    object Uninitialized : Data<Nothing>()
    object Loading : Data<Nothing>()
    data class Failure(val error: Throwable) : Data<Nothing>()
    data class Success<out T>(val element: T) : Data<T>() {
        override operator fun invoke(): T = element
    }

    val uninitialized: Boolean get() = this is Uninitialized
    val loading: Boolean get() = this is Loading
    val failed: Boolean get() = this is Failure
    val successful: Boolean get() = this is Success
    val complete: Boolean get() = this is Error || this is Success

    companion object {
        inline fun <T> of(dataFunction: () -> T): Data<T> =
            try {
                Success(dataFunction())
            } catch (e: Exception) {
                Failure(e)
            }
    }
}

fun <T> Flow<Data<T>>.filterDataLoading(): Flow<Unit> =
    filterIsInstance<Data.Loading>().map { Unit }

fun <T> Flow<Data<T>>.filterDataSuccess(): Flow<T> =
    filterIsInstance<Data.Success<T>>().map { it.element }

fun <T> Flow<Data<T>>.filterDataFailure(): Flow<Throwable> =
    filterIsInstance<Data.Failure>().map { it.error }