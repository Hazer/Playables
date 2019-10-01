package at.florianschuster.playables.controller.android

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import at.florianschuster.playables.controller.Controller

/**
 * Gets or creates the [ViewModel] [Controller] for a [FragmentActivity] scope.
 */
inline fun <reified R> FragmentActivity.getControllerViewModel(): R where R : Controller<*, *, *>, R : ViewModel =
    ViewModelProviders.of(this).get(R::class.java)

/**
 * Lazily gets or creates the [ViewModel] [Controller] for a [FragmentActivity] scope.
 */
inline fun <reified R> FragmentActivity.controllerViewModel(): Lazy<R> where R : Controller<*, *, *>, R : ViewModel =
    lazy { getControllerViewModel<R>() }

/**
 * Lazily gets or creates the [ViewModel] [Controller] for a [FragmentActivity] scope with a provided initialization lambda.
 */
inline fun <reified R> FragmentActivity.getControllerViewModel(
    crossinline factory: () -> R
): R where R : Controller<*, *, *>, R : ViewModel =
    ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = factory() as T
    }).get(R::class.java)

/**
 * Lazily gets or creates the [ViewModel] [Controller] for a [FragmentActivity] scope with a provided initialization lambda.
 */
inline fun <reified R> FragmentActivity.controllerViewModel(
    crossinline factory: () -> R
): Lazy<R> where R : Controller<*, *, *>, R : ViewModel = lazy {
    getControllerViewModel(factory)
}

/**
 * Gets or creates the [ViewModel] [Controller] for a [Fragment] scope.
 */
inline fun <reified R> Fragment.getControllerViewModel(): R where R : Controller<*, *, *>, R : ViewModel =
    ViewModelProviders.of(this).get(R::class.java)

/**
 * Lazily gets or creates the [ViewModel] [Controller] for a [Fragment] scope.
 */
inline fun <reified R> Fragment.controllerViewModel(): Lazy<R> where R : Controller<*, *, *>, R : ViewModel =
    lazy { getControllerViewModel<R>() }

/**
 * Lazily gets or creates the [ViewModel] [Controller] for a [Fragment] scope with a provided initialization lambda.
 */
inline fun <reified R> Fragment.getControllerViewModel(
    crossinline factory: () -> R
): R where R : Controller<*, *, *>, R : ViewModel =
    ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = factory() as T
    }).get(R::class.java)

/**
 * Lazily gets or creates the [ViewModel] [Controller] for a [Fragment] scope with a provided initialization lambda.
 */
inline fun <reified R> Fragment.controllerViewModel(
    crossinline factory: () -> R
): Lazy<R> where R : Controller<*, *, *>, R : ViewModel = lazy {
    getControllerViewModel(factory)
}