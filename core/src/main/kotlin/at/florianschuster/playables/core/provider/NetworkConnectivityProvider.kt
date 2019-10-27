package at.florianschuster.playables.core.provider

import kotlinx.coroutines.flow.Flow

sealed class NetworkConnectivity {
    object Unknown : NetworkConnectivity()
    object Unavailable : NetworkConnectivity()
    object Available : NetworkConnectivity()

    val connected: Boolean get() = this is Available
}

interface NetworkConnectivityProvider {
    val currentNetworkConnectivity: NetworkConnectivity
    val networkConnectivity: Flow<NetworkConnectivity>
}