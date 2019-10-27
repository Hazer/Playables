package at.florianschuster.playables.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import ru.ldralighieri.corbind.recyclerview.scrollEvents

fun <R : RecyclerView> R.scrolledPastItemChanges(
    itemIndex: Int = 0,
    samplePeriodMillis: Long = 500
): Flow<Boolean> {
    return scrollEvents()
        .sample(samplePeriodMillis)
        .map { it.view.layoutManager as? LinearLayoutManager }
        .filterNotNull()
        .map { it.findFirstVisibleItemPosition() > itemIndex }
        .distinctUntilChanged()
}
