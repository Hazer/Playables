package at.florianschuster.playables.core.provider

import at.florianschuster.playables.core.provider.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher

internal object TestDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher get() = TestCoroutineDispatcher()
    override val io: CoroutineDispatcher get() = TestCoroutineDispatcher()
}