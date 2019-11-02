package at.florianschuster.playables.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import reactivecircus.flowbinding.recyclerview.scrollEvents

fun <R : RecyclerView> R.scrolledPastItemChanges(
    itemIndex: Int = 0,
    samplePeriodMillis: Long = 500
): Flow<Boolean> {
    require(layoutManager != null)

    return when (layoutManager) {
        is LinearLayoutManager -> {
            scrollEvents()
                .sample(samplePeriodMillis)
                .map { it.view.layoutManager as LinearLayoutManager }
                .map { it.findFirstVisibleItemPosition() > itemIndex }
                .distinctUntilChanged()
        }
        is StaggeredGridLayoutManager -> {
            scrollEvents()
                .sample(samplePeriodMillis)
                .map { it.view.layoutManager as StaggeredGridLayoutManager }
                .map {
                    val min = it.findFirstVisibleItemPositions(null).min()
                    if (min != null) min > itemIndex else false
                }
                .distinctUntilChanged()
        }
        else -> emptyFlow()
    }
}

fun <R : RecyclerView> R.shouldLoadMore(threshold: Int = 8): Boolean {
    require(layoutManager != null)

    return when (val layoutManager = layoutManager ?: return false) {
        is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition() + threshold > layoutManager.itemCount
        is StaggeredGridLayoutManager -> {
            val max = layoutManager.findLastVisibleItemPositions(null).max()
            if (max != null) max + threshold > layoutManager.itemCount else false
        }
        else -> false
    }
}